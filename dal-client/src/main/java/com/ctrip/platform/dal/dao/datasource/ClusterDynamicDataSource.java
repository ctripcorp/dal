package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.cluster.ClusterType;
import com.ctrip.framework.dal.cluster.client.cluster.DrcCluster;
import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.framework.dal.cluster.client.cluster.ClusterSwitchedEvent;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.ServiceLoaderHelper;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ClusterDynamicDataSource implements DataSource, ClosableDataSource, SingleDataSourceWrapper, DataSourceConfigureChangeListener {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String CAT_LOG_TYPE = "DAL.dataSource";
    private static final String CAT_LOG_NAME_DRC = "createDrcDataSource:%s";
    private static final String CAT_LOG_NAME_DRC_FAIL = "createDrcDataSource:EXCEPTION:%s";
    private static final String CAT_LOG_NAME_NORMAL = "createNormalDataSource:%s";

    private ClusterInfo clusterInfo;
    private Cluster cluster;
    private DataSourceConfigureProvider provider;
    private LocalizationValidatorFactory factory;

    private AtomicReference<RefreshableDataSource> dataSourceRef = new AtomicReference<>();

    public ClusterDynamicDataSource(ClusterInfo clusterInfo, Cluster cluster, DataSourceConfigureProvider provider,
                                    LocalizationValidatorFactory factory) {
        this.cluster = cluster;
        this.clusterInfo = clusterInfo;
        this.provider = provider;
        this.factory = factory;

        registerListeners();
        this.dataSourceRef.set(createInnerDataSource(clusterInfo, cluster, provider));
    }

    @Override
    public void configChanged(DataSourceConfigureChangeEvent event) throws SQLException {
        getInnerDataSource().configChanged(event);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getInnerDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String paramString1, String paramString2) throws SQLException {
        return getInnerDataSource().getConnection(paramString1, paramString2);
    }

    @Override
    public SingleDataSource getSingleDataSource() {
        return getInnerDataSource().getSingleDataSource();
    }

    @Override
    public void forceRefreshDataSource(String name, DataSourceConfigure configure) {
        getInnerDataSource().forceRefreshDataSource(name, configure);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return getInnerDataSource().getLogWriter();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return getInnerDataSource().getLoginTimeout();
    }

    @Override
    public void setLogWriter(PrintWriter paramPrintWriter) throws SQLException {
        getInnerDataSource().setLogWriter(paramPrintWriter);
    }

    @Override
    public void setLoginTimeout(int paramInt) throws SQLException {
        getInnerDataSource().setLoginTimeout(paramInt);
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return getInnerDataSource().getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getInnerDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getInnerDataSource().isWrapperFor(iface);
    }

    @Override
    public void close() {
        RefreshableDataSource ds = getInnerDataSource();
        if (ds != null) {
            ds.close();
        }
    }

    private RefreshableDataSource createInnerDataSource(ClusterInfo clusterInfo, Cluster cluster, DataSourceConfigureProvider provider) {
        DataSourceIdentity id = getDataSourceIdentity(clusterInfo, cluster);
        DataSourceConfigure config = provider.getDataSourceConfigure(id);
        try {
            if (cluster != null && cluster.getClusterType() == ClusterType.DRC) {
                LocalizationConfig localizationConfig = cluster.getLocalizationConfig();
                LocalizationValidator validator = factory.createValidator(clusterInfo, localizationConfig);
                LOGGER.logEvent(CAT_LOG_TYPE, String.format(CAT_LOG_NAME_DRC, clusterInfo.toString()), localizationConfig.toString());
                return new LocalizedDataSource(validator, id, config);
            }
        } catch (Exception e) {
            LOGGER.logEvent(CAT_LOG_TYPE, String.format(CAT_LOG_NAME_DRC_FAIL, clusterInfo.toString()), e.getMessage());
            throw e;
        }
        LOGGER.logEvent(CAT_LOG_TYPE, String.format(CAT_LOG_NAME_NORMAL, clusterInfo.toString()), "");
        return new RefreshableDataSource(id, config);
    }

    private void registerListeners() {
        cluster.addListener(new Listener<ClusterSwitchedEvent>() {
            @Override
            public void onChanged(ClusterSwitchedEvent event) {
                try {
                    doSwitch();
                } catch (Throwable t) {
                    String msg = "Cluster switch listener error";
                    LOGGER.error(msg, t);
                    throw new DalRuntimeException(msg, t);
                }
            }
        });
    }

    private RefreshableDataSource getInnerDataSource() {
        return dataSourceRef.get();
    }

    private void doSwitch() throws SQLException {
        RefreshableDataSource ds = createInnerDataSource(clusterInfo, cluster, provider);
        RefreshableDataSource oldDs = dataSourceRef.getAndSet(ds);
        if (oldDs != null)
            oldDs.close();
    }

    private DataSourceIdentity getDataSourceIdentity(ClusterInfo clusterInfo, Cluster cluster) {
        if (clusterInfo.getRole() == DatabaseRole.MASTER)
            return new ClusterDataSourceIdentity(cluster.getMasterOnShard(clusterInfo.getShardIndex()));
        else {
            List<Database> slaves = cluster.getSlavesOnShard(clusterInfo.getShardIndex());
            if (slaves == null || slaves.size() == 0)
                throw new IllegalStateException(String.format(
                        "slave is not found for cluster '%s', shard %d",
                        clusterInfo.getClusterName(), clusterInfo.getShardIndex()));
            if (slaves.size() > 1)
                throw new UnsupportedOperationException(String.format(
                        "multi slaves are found for cluster '%s', shard %d, which is not supported yet",
                        clusterInfo.getClusterName(), clusterInfo.getShardIndex()));
            return new ClusterDataSourceIdentity(slaves.iterator().next());
        }
    }

}
