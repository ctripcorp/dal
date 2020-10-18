package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.DataSourceCreator;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.EnvUtils;
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

    private static final EnvUtils ENV_UTILS = DalElementFactory.DEFAULT.getEnvUtils();

    private final Map<HostSpec, DataSourceConfigure> dataSourceConfigs;
    private final Map<HostSpec, SingleDataSource> wrappedDataSources = new HashMap<>();
    private final ConnectionFactory connFactory;
    private final RouteStrategy routeStrategy;
    private final MultiHostClusterProperties clusterProperties;

    public MultiHostDataSource(Map<HostSpec, DataSourceConfigure> dataSourceConfigs,
                               MultiHostClusterProperties clusterProperties) {
        this.dataSourceConfigs = dataSourceConfigs;
        this.clusterProperties = clusterProperties;
        this.connFactory = prepareConnectionFactory();
        this.routeStrategy = prepareRouteStrategy();
        prepareDataSources();
    }

    protected ConnectionFactory prepareConnectionFactory() {
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

    protected RouteStrategy prepareRouteStrategy() {
        RouteStrategy strategy = null;
        strategy.initialize(wrappedDataSources.keySet(), connFactory, clusterProperties.routeStrategyProperties());
        return strategy;
    }

    private void prepareDataSources() {
        dataSourceConfigs.forEach((host, config) -> wrappedDataSources.put(host, prepareDataSource(config)));
    }

    protected SingleDataSource prepareDataSource(DataSourceConfigure dataSourceConfig) {
        return DataSourceCreator.getInstance().getOrCreateDataSourceWithoutPool(dataSourceConfig.getName(),
                dataSourceConfig, null, routeStrategy.getConnectionValidator());
    }

    @Override
    public Connection getConnection() throws SQLException {
        return routeStrategy.pickConnection(buildRequestContext());
    }

    protected RequestContext buildRequestContext() {
        return new DefaultRequestContext(ENV_UTILS.getZone());
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
