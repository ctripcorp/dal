package com.ctrip.platform.dal.dao.cluster;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.base.ListenableSupport;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.framework.dal.cluster.client.cluster.ClusterType;
import com.ctrip.framework.dal.cluster.client.cluster.RouteStrategyEnum;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfigImpl;
import com.ctrip.framework.dal.cluster.client.config.DatabaseConfigImpl;
import com.ctrip.framework.dal.cluster.client.config.DatabaseShardConfigImpl;
import com.ctrip.framework.dal.cluster.client.database.DatabaseCategory;
import com.ctrip.framework.dal.cluster.client.multihost.DefaultClusterRouteStrategyConfig;
import com.ctrip.platform.dal.common.enums.DBModel;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringConfigureProvider;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.MultiMasterStrategy;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import static com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.MultiMasterStrategy.MULTI_MASTER;

/**
 * @author c7ch23en
 */
public class ClusterConfigAdapter extends ListenableSupport<ClusterConfig> implements ClusterConfig {

    private final ConnectionStringConfigureProvider provider;
    private final AtomicReference<ClusterConfig> clusterConfigRef = new AtomicReference<>();
    private final AtomicReference<DalConnectionStringConfigure> connStrConfigRef = new AtomicReference<>();

    public ClusterConfigAdapter(ConnectionStringConfigureProvider provider) {
        this.provider = provider;
        init();
    }

    private void init() {
        try {
            DalConnectionStringConfigure configure = provider.getConnectionString();
            load(configure);
            provider.addListener(current -> {
                try {
                    load(current);
                } catch (Throwable t) {
                    // ignore
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Init ClusterConfigAdapter failed: " + provider.getDbName(), e);
        }
    }

    private void load(DalConnectionStringConfigure configure) {
        ClusterConfig clusterConfig;
        if (configure instanceof InvalidVariableConnectionString) {
            throw new DalRuntimeException("connectionString invalid for db: " + provider.getDbName(),
                    ((InvalidVariableConnectionString) configure).getConnectionStringException());
        }
        if (configure == null)
            throw new RuntimeException("Get null config from mysqlapi for db: " + provider.getDbName());
        if (configure instanceof MultiHostConnectionStringConfigure)
            clusterConfig = buildMultiHostClusterConfig((MultiHostConnectionStringConfigure) configure);
        else
            clusterConfig = buildNormalClusterConfig(configure);
        clusterConfigRef.getAndSet(clusterConfig);
        DalConnectionStringConfigure prev = connStrConfigRef.getAndSet(configure);
        if (prev != null && !equals(prev, configure))
            for (Listener<ClusterConfig> listener : getListeners()) {
                try {
                    listener.onChanged(this);
                } catch (Throwable t) {
                    // ignore
                }
            }
    }

    private boolean equals(DalConnectionStringConfigure currentConfigure, DalConnectionStringConfigure newConfigure) {
        if (currentConfigure == null && newConfigure == null)
            return true;
        if (currentConfigure == null || newConfigure == null)
            return false;
        if (currentConfigure.getClass() != newConfigure.getClass())
            return false;
        if (currentConfigure instanceof MultiHostConnectionStringConfigure) {
            MultiHostConnectionStringConfigure currentConfigure1 = (MultiHostConnectionStringConfigure) currentConfigure;
            MultiHostConnectionStringConfigure newConfigure1 = (MultiHostConnectionStringConfigure) newConfigure;
            TreeSet<HostSpec> currentHosts = new TreeSet<>(currentConfigure1.getHosts());
            TreeSet<HostSpec> newHosts = new TreeSet<>(newConfigure1.getHosts());
            return Objects.equals(currentHosts, newHosts)
                    && Objects.equals(currentConfigure1.getDbName(), newConfigure1.getDbName())
                    && Objects.equals(currentConfigure1.getZonesPriority(), newConfigure1.getZonesPriority())
                    && Objects.equals(currentConfigure1.getFailoverTimeMS(), newConfigure1.getFailoverTimeMS())
                    && Objects.equals(currentConfigure1.getBlacklistTimeoutMS(), newConfigure1.getBlacklistTimeoutMS())
                    && Objects.equals(currentConfigure1.getFixedValidatePeriodMS(), newConfigure1.getFixedValidatePeriodMS())
                    && Objects.equals(currentConfigure1.getUserName(), newConfigure1.getUserName())
                    && Objects.equals(currentConfigure1.getPassword(), newConfigure1.getPassword());
        } else {
            return Objects.equals(currentConfigure.getConnectionUrl(), newConfigure.getConnectionUrl())
                    && Objects.equals(currentConfigure.getUserName(), newConfigure.getUserName())
                    && Objects.equals(currentConfigure.getPassword(), newConfigure.getPassword());
        }
    }

    private ClusterConfig buildMultiHostClusterConfig(MultiHostConnectionStringConfigure configure) {
        ClusterConfigWithNoVersion clusterConfig =
                new ClusterConfigWithNoVersion(configure.getName(), ClusterType.NORMAL, DatabaseCategory.MYSQL);
        DatabaseShardConfigImpl databaseShardConfig = new DatabaseShardConfigImpl(clusterConfig, 0);
        List<HostSpec> hosts = configure.getHosts();
        hosts.forEach(host -> {
            DatabaseConfigImpl databaseConfig = new DatabaseConfigImpl(databaseShardConfig);
            databaseConfig.setIp(host.host());
            databaseConfig.setPort(host.port());
            databaseConfig.setZone(host.zone());
            databaseConfig.setDbName(configure.getDbName());
            databaseConfig.setUid(configure.getUserName());
            databaseConfig.setPwd(configure.getPassword());
            databaseShardConfig.addDatabaseConfig(databaseConfig);
        });
        clusterConfig.addDatabaseShardConfig(databaseShardConfig);
        DBModel dbModel = configure.getDbModel();
        String routeStrategyName = DBModel.MGR == dbModel ? RouteStrategyEnum.WRITE_ORDERED.getAlias() : RouteStrategyEnum.WRITE_CURRENT_ZONE_FIRST.getAlias();
        DefaultClusterRouteStrategyConfig routeStrategy =
                new DefaultClusterRouteStrategyConfig(routeStrategyName);
        if (configure.getZonesPriority() != null)
            routeStrategy.setProperty(MultiMasterStrategy.ZONES_PRIORITY,
                    configure.getZonesPriority());
        if (configure.getFailoverTimeMS() != null)
            routeStrategy.setProperty(MultiMasterStrategy.FAILOVER_TIME_MS,
                    String.valueOf(configure.getFailoverTimeMS()));
        if (configure.getBlacklistTimeoutMS() != null)
            routeStrategy.setProperty(MultiMasterStrategy.BLACKLIST_TIMEOUT_MS,
                    String.valueOf(configure.getBlacklistTimeoutMS()));
        if (configure.getFixedValidatePeriodMS() != null)
            routeStrategy.setProperty(MultiMasterStrategy.FIXED_VALIDATE_PERIOD_MS,
                    String.valueOf(configure.getFixedValidatePeriodMS()));
        clusterConfig.setRouteStrategyConfig(routeStrategy);
        clusterConfig.setCustomizedOption(new DefaultDalConfigCustomizedOption());
        return clusterConfig;
    }

    private ClusterConfig buildNormalClusterConfig(DalConnectionStringConfigure configure) {
        ClusterConfigWithNoVersion clusterConfig =
                new ClusterConfigWithNoVersion(configure.getName(), ClusterType.NORMAL, DatabaseCategory.MYSQL);
        DatabaseShardConfigImpl databaseShardConfig = new DatabaseShardConfigImpl(clusterConfig, 0);
        DatabaseConfigImpl databaseConfig = new DatabaseConfigImpl(databaseShardConfig);
        HostAndPort hostAndPort = ConnectionStringParser.parseHostPortFromURL(configure.getConnectionUrl());
        databaseConfig.setIp(hostAndPort.getHost());
        databaseConfig.setPort(hostAndPort.getPort());
        databaseConfig.setDbName(provider.getDbName());
        databaseConfig.setUid(configure.getUserName());
        databaseConfig.setPwd(configure.getPassword());
        databaseShardConfig.addDatabaseConfig(databaseConfig);
        clusterConfig.addDatabaseShardConfig(databaseShardConfig);
        // todo-lhj  make configurable RouteStrategy of
        clusterConfig.setRouteStrategyConfig(new DefaultClusterRouteStrategyConfig(RouteStrategyEnum.READ_MASTER.name()));
        clusterConfig.setCustomizedOption(new DefaultDalConfigCustomizedOption());
        return clusterConfig;
    }

    @Override
    public String getClusterName() {
        return clusterConfigRef.get().getClusterName();
    }

    @Override
    public boolean checkSwitchable(ClusterConfig newConfig) {
        return false;
    }

    @Override
    public Cluster generate() {
        return clusterConfigRef.get().generate();
    }

    private static class ClusterConfigWithNoVersion extends ClusterConfigImpl {
        public ClusterConfigWithNoVersion(String clusterName, DatabaseCategory databaseCategory) {
            super(clusterName, databaseCategory, 0);
        }

        public ClusterConfigWithNoVersion(String clusterName, ClusterType clusterType,
                                          DatabaseCategory databaseCategory) {
            super(clusterName, clusterType, databaseCategory, 0);
        }

        @Override
        public boolean checkSwitchable(ClusterConfig newConfig) {
            return true;
        }
    }

}
