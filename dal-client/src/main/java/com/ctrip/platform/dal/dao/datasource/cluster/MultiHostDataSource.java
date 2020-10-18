package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.DataSourceCreator;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;
import com.ctrip.platform.dal.exceptions.InvalidConnectionException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class MultiHostDataSource extends DataSourceDelegate implements DataSource {

    private final Map<HostSpec, SingleDataSource> wrappedDataSources = new HashMap<>();
    private final ConnectionFactory connFactory;
    private final RouteOptions routeOptions;
    private final RouteStrategy routeStrategy;
    private final RouteStrategyManager routeStrategyManager = RouteStrategyManager.DEFAULT;

    protected RequestContext reqCtxForTest;

    public MultiHostDataSource(Map<HostSpec, DataSourceConfigure> dataSourceConfigs,
                               MultiHostClusterProperties clusterProperties) {
        prepareDataSources(dataSourceConfigs);
        this.connFactory = buildConnectionFactory();
        this.routeOptions = buildRouteOptions(clusterProperties);
        this.routeStrategy = buildRouteStrategy(clusterProperties.routeStrategy());
    }

    private void prepareDataSources(Map<HostSpec, DataSourceConfigure> dataSourceConfigs) {
        dataSourceConfigs.forEach((host, config) -> wrappedDataSources.put(host, prepareDataSource(config)));
    }

    protected SingleDataSource prepareDataSource(DataSourceConfigure dataSourceConfig) {
        return DataSourceCreator.getInstance().getOrCreateDataSourceWithoutPool(dataSourceConfig.getName(),
                dataSourceConfig, null);
    }

    protected ConnectionFactory buildConnectionFactory() {
        return new ConnectionFactory() {
            @Override
            public Connection getPooledConnectionForHost(HostSpec host) throws SQLException, InvalidConnectionException {
                return wrappedDataSources.get(host).getDataSource().getConnection();
            }

            @Override
            public Connection createConnectionForHost(HostSpec host) throws SQLException, InvalidConnectionException {
                return wrappedDataSources.get(host).getDataSource().getConnection();
            }
        };
    }

    protected RouteOptions buildRouteOptions(MultiHostClusterProperties clusterProperties) {
        return new DefaultRouteOptions(wrappedDataSources.keySet(), clusterProperties);
    }

    protected RouteStrategy buildRouteStrategy(String routeStrategy) {
        return routeStrategyManager.getOrCreateRouteStrategy(routeStrategy);
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
    public DataSource getDelegated() {
        return wrappedDataSources.values().iterator().next().getDataSource();
    }

}
