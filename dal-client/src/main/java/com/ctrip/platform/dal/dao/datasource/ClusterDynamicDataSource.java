package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.cluster.cluster.ClusterType;
import com.ctrip.platform.dal.cluster.config.LocalizationConfig;
import com.ctrip.platform.dal.cluster.database.ConnectionString;
import com.ctrip.platform.dal.cluster.database.Database;
import com.ctrip.platform.dal.cluster.database.DatabaseRole;
import com.ctrip.platform.dal.cluster.util.StringUtils;
import com.ctrip.platform.dal.common.enums.ForceSwitchedStatus;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.datasource.cluster.*;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.ServiceLoaderHelper;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.ctrip.platform.dal.exceptions.UnsupportedFeatureException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClusterDynamicDataSource extends DataSourceDelegate implements DataSource,
        ClosableDataSource, SingleDataSourceWrapper, DataSourceConfigureChangeListener, IForceSwitchableDataSource {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private static final String CAT_LOG_NAME_NORMAL = "createNormalDataSource:%s";
    private static final String CAT_LOG_NAME_DRC = "createDrcDataSource:%s";
    private static final String CAT_LOG_NAME_DRC_FAIL = "createDrcDataSource:EXCEPTION:%s";

    private ClusterInfo clusterInfo;
    private Cluster cluster;
    private DataSourceConfigureProvider provider;
    private LocalizationValidatorFactory factory;
    private DataSourceIdentity dataSourceId;
    private AtomicReference<DataSource> dataSourceRef = new AtomicReference<>();

    public ClusterDynamicDataSource(ClusterInfo clusterInfo, Cluster cluster, DataSourceConfigureProvider provider,
                                    LocalizationValidatorFactory factory) {
        this.clusterInfo = clusterInfo;
        this.clusterInfo.setCluster(cluster);
        this.cluster = cluster;
        this.provider = provider;
        this.factory = factory;
        prepare();
    }

    protected void prepare() {
        cluster.addListener(event -> {
            if (status.get() == ForceSwitchedStatus.UnForceSwitched) {
                try {
                    switchDataSource();
                } catch (Throwable t) {
                    String msg = "Cluster switch listener error";
                    LOGGER.error(msg, t);
                    throw new DalRuntimeException(msg, t);
                }
            } else
                LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE,
                        String.format("switchIgnored:%s", cluster.getClusterName()), "");
        });
        switchDataSource();
    }

    private void switchDataSource() {
        switchDataSource(createInnerDataSource());
        updateCurrentHost();
    }

    private void switchDataSource(DataSource newDataSource) {
        close(dataSourceRef.getAndSet(newDataSource));
    }

    protected DataSource createInnerDataSource() {
        if (cluster == null)
            throw new DalRuntimeException("null cluster");
        return !cluster.getRouteStrategyConfig().multiMaster() ? createStandaloneDataSource() : createMultiHostDataSource();
    }

    protected DataSource createStandaloneDataSource() {
        DataSourceIdentity id = getStandaloneDataSourceIdentity(clusterInfo, cluster);
        dataSourceId = id;
        DataSourceConfigure config = provider.getDataSourceConfigure(id);
        try {
            if (cluster.getClusterType() == ClusterType.DRC) {
                LocalizationConfig localizationConfig = cluster.getLocalizationConfig();
                LocalizationConfig lastLocalizationConfig = cluster.getLastLocalizationConfig();
                LocalizationValidator validator = factory.createValidator(clusterInfo, localizationConfig, lastLocalizationConfig);
                LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format(CAT_LOG_NAME_DRC, clusterInfo.toString()), localizationConfig.toString());
                return new LocalizedDataSource(validator, id, config);
            }
        } catch (Throwable t) {
            LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format(CAT_LOG_NAME_DRC_FAIL, clusterInfo.toString()), t.getMessage());
            throw t;
        }
        LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format(CAT_LOG_NAME_NORMAL, clusterInfo.toString()), "");
        return new RefreshableDataSource(id, config);
    }

    protected DataSource createMultiHostDataSource() {
        if (cluster.dbShardingEnabled())
            throw new UnsupportedFeatureException("ClusterDataSource does not support sharding cluster, cluster name: " + cluster.getClusterName());
        DataSourceIdentity id = getMultiHostDataSourceIdentity(cluster);
        dataSourceId = id;
        return new ClusterDataSource(id, cluster, provider);
    }

    @Override
    public void configChanged(DataSourceConfigureChangeEvent event) throws SQLException {
        if (status.get() == ForceSwitchedStatus.UnForceSwitched) {
            DataSource ds = getDelegated();
            if (ds instanceof DataSourceConfigureChangeListener)
                ((DataSourceConfigureChangeListener) ds).configChanged(event);
        } else
            LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, String.format("switchIgnored:%s", event.getName()), "");
    }

    @Override
    public SingleDataSource getSingleDataSource() {
        DataSource ds = getDelegated();
        if (ds instanceof SingleDataSourceWrapper)
            return ((SingleDataSourceWrapper) ds).getSingleDataSource();
        return null;
    }

    @Override
    public void forceRefreshDataSource(String name, DataSourceConfigure configure) {
        DataSource ds = getDelegated();
        if (ds instanceof SingleDataSourceWrapper)
            ((SingleDataSourceWrapper) ds).forceRefreshDataSource(name, configure);
    }

    @Override
    public void close() {
        close(getDelegated());
    }

    protected void close(DataSource dataSource) {
        if (dataSource instanceof ClosableDataSource) {
            ((ClosableDataSource) dataSource).close();
        }
    }

    @Override
    public DataSource getDelegated() {
        return dataSourceRef.get();
    }

    private DataSourceIdentity getStandaloneDataSourceIdentity(ClusterInfo clusterInfo, Cluster cluster) {
        if (!StringUtils.isEmpty(clusterInfo.getTag()))
            return new TagDataSourceIdentity(cluster.getMasterOnShard(clusterInfo.getShardIndex()), clusterInfo.getTag());
        if (clusterInfo.getRole() == DatabaseRole.MASTER)
            return new TraceableClusterDataSourceIdentity(cluster.getMasterOnShard(clusterInfo.getShardIndex()));
        else {
            List<Database> slaves = cluster.getSlavesOnShard(clusterInfo.getShardIndex());
            if (slaves == null || slaves.size() == 0)
                throw new IllegalStateException(String.format(
                        "slave is not found for cluster '%s', shard %d",
                        clusterInfo.getClusterName(), clusterInfo.getShardIndex()));
            if (slaves.size() > 1)
                throw new UnsupportedFeatureException(String.format(
                        "multi slaves are found for cluster '%s', shard %d, which is not supported yet",
                        clusterInfo.getClusterName(), clusterInfo.getShardIndex()));
            return new TraceableClusterDataSourceIdentity(slaves.iterator().next());
        }
    }

    private DataSourceIdentity getMultiHostDataSourceIdentity(Cluster cluster) {
        return new DefaultClusterIdentity(cluster);
    }

    // force switch

    private static final String FORCE_SWITCH = "ForceSwitch::forceSwitch:%s";
    private static final String GET_STATUS = "ForceSwitch::getStatus:%s";
    private static final String RESTORE = "ForceSwitch::restore:%s";
    private final Lock lock = new ReentrantLock();
    private final AtomicReference<HostAndPort> currentHost = new AtomicReference<>();
    private final AtomicReference<ForceSwitchedStatus> status = new AtomicReference<>(ForceSwitchedStatus.UnForceSwitched);
    private final AtomicBoolean poolCreated = new AtomicBoolean(true);
    private static ExecutorService executor;
    private static final DataSourceConfigureConvert converter = ServiceLoaderHelper.getInstance(DataSourceConfigureConvert.class);

    @Override
    public SwitchableDataSourceStatus forceSwitch(FirstAidKit configure, final String ip, final Integer port) {
        synchronized (lock) {
            SwitchableDataSourceStatus currentStatus = getStatus();
            ForceSwitchedStatus prevStatus = status.getAndSet(ForceSwitchedStatus.ForceSwitching);
            try {
                String logName = String.format(FORCE_SWITCH, clusterInfo.toString());
                LOGGER.logTransaction(DalLogTypes.DAL_CONFIGURE, logName, String.format("newIp: %s, newPort: %s", ip, port), () -> {
                    LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName,
                            String.format("old isForceSwitched before force switch: %s, old poolCreated before force switch: %s",
                                    currentStatus.isForceSwitched(), currentStatus.isPoolCreated()));
                    DataSourceConfigure dataSourceConfig = getSingleDataSource().getDataSourceConfigure().clone();
                    LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName, String.format("previous host(s): %s:%s", currentStatus.getHostName(), currentStatus.getPort()));
                    dataSourceConfig.replaceURL(ip, port);
                    LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName, String.format("new host(s): %s:%s", ip, port));
                    getExecutor().submit(() -> {
                        try {
                            switchDataSource(new RefreshableDataSource(dataSourceId, dataSourceConfig));
                            status.set(ForceSwitchedStatus.ForceSwitched);
                            currentHost.set(new HostAndPort(null, ip, port));
                        } catch (Throwable t) {
                            LOGGER.error("DataSource creation failed", t);
                            // TODO: handle pool creation failure
                            status.set(prevStatus);
                        }
                    });
                });
                return currentStatus;
            } catch (Throwable t) {
                status.set(prevStatus);
                LOGGER.error("Force switch error", t);
                throw new DalRuntimeException("Force switch error", t);
            }
        }
    }

    @Override
    public SwitchableDataSourceStatus restore() {
        synchronized (lock) {
            SwitchableDataSourceStatus currentStatus = getStatus();
            try {
                String logName = String.format(RESTORE, clusterInfo.toString());
                LOGGER.logTransaction(DalLogTypes.DAL_CONFIGURE, logName, "restore", () -> {
                    LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName,
                            String.format("old isForceSwitched before restore: %s, old poolCreated before restore: %s",
                                    currentStatus.isForceSwitched(), currentStatus.isPoolCreated()));
                    if (status.get() != ForceSwitchedStatus.ForceSwitched) {
                        LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName, "not in force switched status");
                        return;
                    }
                    LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName, String.format("previous host(s): %s:%s", currentStatus.getHostName(), currentStatus.getPort()));
                    HostAndPort newHost = buildHostAndPort(cluster);
                    LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName, String.format("new host(s): %s:%s", newHost.getHost(), newHost.getPort()));
                    getExecutor().submit(() -> {
                        try {
                            switchDataSource();
                            status.set(ForceSwitchedStatus.UnForceSwitched);
                        } catch (Throwable t) {
                            LOGGER.error("DataSource restoring failed", t);
                        }
                    });
                });
                return currentStatus;
            } catch (Throwable t) {
                LOGGER.error("Restore error", t);
                throw new DalRuntimeException("Restore error", t);
            }
        }
    }

    @Override
    public SwitchableDataSourceStatus getStatus() {
        AtomicReference<HostAndPort> current = new AtomicReference<>();
        try {
            String logName = String.format(GET_STATUS, clusterInfo.toString());
            LOGGER.logTransaction(DalLogTypes.DAL_CONFIGURE, logName,
                    currentHost.get().getHost() + ":" + currentHost.get().getPort(), () -> {
                try (Connection conn = getConnection()) {
                    current.set(ConnectionStringParser.parseHostPortFromURL(conn.getMetaData().getURL()));
                }
            });
        } catch (Throwable t) {
            LOGGER.warn("GetStatus error", t);
        }
        if (current.get() != null)
            return new SwitchableDataSourceStatus(status.get() == ForceSwitchedStatus.ForceSwitched,
                    current.get().getHost(), current.get().getPort(), poolCreated.get());
        else
            return new SwitchableDataSourceStatus(status.get() == ForceSwitchedStatus.ForceSwitched,
                    currentHost.get().getHost(), currentHost.get().getPort(), poolCreated.get());
    }

    @Override
    public FirstAidKit getFirstAidKit() {
        DataSourceConfigure dataSourceConfig = getSingleDataSource().getDataSourceConfigure();
        return SerializableDataSourceConfig.valueOf(converter.desEncrypt(dataSourceConfig));
    }

    @Override
    public void addListener(SwitchListener listener) {
        throw new UnsupportedOperationException("ClusterDynamicDataSource does not support SwitchListener");
    }

    private void updateCurrentHost() {
        currentHost.set(buildHostAndPort(cluster));
    }

    private HostAndPort buildHostAndPort(Cluster cluster) {
        if (cluster.getRouteStrategyConfig().multiMaster()) {
            StringBuilder hosts = new StringBuilder();
            StringBuilder hostsWithPorts = new StringBuilder();
            Set<Integer> ports = new HashSet<>();
            cluster.getDatabases().forEach(database -> {
                ConnectionString connStr = database.getConnectionString();
                if (hosts.length() > 0)
                    hosts.append(",");
                if (hostsWithPorts.length() > 0)
                    hostsWithPorts.append(",");
                hosts.append(connStr.getPrimaryHost());
                hostsWithPorts.append(connStr.getPrimaryHost()).append(":").append(connStr.getPrimaryPort());
                ports.add(connStr.getPrimaryPort());
            });
            if (ports.size() == 1)
                return new HostAndPort(null, hosts.toString(), ports.iterator().next());
            else
                return new HostAndPort(null, hostsWithPorts.toString(), 0);
        } else {
            ConnectionString connStr = cluster.getMasterOnShard(clusterInfo.getShardIndex()).getConnectionString();
            return new HostAndPort(null, connStr.getPrimaryHost(), connStr.getPrimaryPort());
        }
    }

    private static ExecutorService getExecutor() {
        if (executor == null)
            executor = Executors.newFixedThreadPool(1);
        return executor;
    }

}
