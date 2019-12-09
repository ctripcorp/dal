package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.cluster.DrcCluster;
import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.framework.dal.cluster.client.cluster.ClusterSwitchedEvent;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.helper.ServiceLoaderHelper;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ClusterDynamicDataSource implements DataSource, ClosableDataSource, DataSourceConfigureChangeListener {

    private ClusterInfo clusterInfo;
    private Cluster cluster;
    private DataSourceConfigureProvider provider;
    private AtomicReference<RefreshableDataSource> dataSourceRef = new AtomicReference<>();

    public ClusterDynamicDataSource(ClusterInfo clusterInfo, Cluster cluster, DataSourceConfigureProvider provider) {
        this.cluster = cluster;
        this.clusterInfo = clusterInfo;
        this.provider = provider;
        registerListener();
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
            if (cluster.isWrapperFor(DrcCluster.class)) {
                DrcCluster drcCluster = cluster.unwrap(DrcCluster.class);
                LocalizationConfig localizationConfig = drcCluster.getLocalizationConfig();
                LocalizationValidator validator = ServiceLoaderHelper.getInstance(LocalizationValidator.class);
                if (validator == null)
                    throw new DalRuntimeException("load LocalizationValidator exception");
                validator.initialize(localizationConfig);
                return new LocalizedDataSource(validator, id, config);
            }
        } catch (SQLException e) {
            // log
        }
        return new RefreshableDataSource(id, config);
    }

    private void registerListener() {
        cluster.addListener(new Listener<ClusterSwitchedEvent>() {
            @Override
            public void onChanged(ClusterSwitchedEvent event) {
                try {
                    doSwitch();
                } catch (Throwable t) {
                    // log
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
        switch (clusterInfo.getRole()) {
            case MASTER:
                return new ClusterDataSourceIdentity(cluster.getMasterOnShard(clusterInfo.getShardIndex()));
            default:
                throw new UnsupportedOperationException(String.format("unsupported role '%s' for cluster '%s'",
                        clusterInfo.getRole().getValue(), clusterInfo.getClusterName()));
        }
    }

}
