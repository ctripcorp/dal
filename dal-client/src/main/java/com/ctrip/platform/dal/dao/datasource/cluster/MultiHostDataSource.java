package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.DataSourceCreator;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author c7ch23en
 */
public class MultiHostDataSource implements DataSource {

    private final Map<HostSpec, SingleDataSource> mappedDataSources = new HashMap<>();
    private final ConnectionFactory connFactory;
    private final RouteOptions routeOptions;
    private final RouteStrategy routeStrategy;

    protected RequestContext reqCtxForTest;

    public MultiHostDataSource(Map<HostSpec, DataSourceConfigure> dataSourceConfigs,
                               MultiHostClusterOptions clusterOptions) {
        initDataSources(dataSourceConfigs);
        this.connFactory = buildConnectionFactory();
        this.routeOptions = buildRouteOptions(clusterOptions);
        this.routeStrategy = buildRouteStrategy();
    }

    private void initDataSources(Map<HostSpec, DataSourceConfigure> dataSourceConfigs) {
        dataSourceConfigs.forEach((host, config) -> mappedDataSources.put(host, getOrCreateDataSource(config)));
    }

    protected SingleDataSource getOrCreateDataSource(DataSourceConfigure dataSourceConfig) {
        return DataSourceCreator.getInstance().getOrCreateDataSource(dataSourceConfig.getName(), dataSourceConfig);
    }

    protected ConnectionFactory buildConnectionFactory() {
        return host -> mappedDataSources.get(host).getDataSource().getConnection();
    }

    protected RouteOptions buildRouteOptions(MultiHostClusterOptions clusterOptions) {
        return new DefaultRouteOptions(mappedDataSources.keySet(), clusterOptions);
    }

    protected RouteStrategy buildRouteStrategy() {
        return null;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return routeStrategy.pickConnection(connFactory, reqCtxForTest, routeOptions);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

}
