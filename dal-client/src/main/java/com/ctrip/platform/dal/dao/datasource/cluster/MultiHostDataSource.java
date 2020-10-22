package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfigXMLConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.ClosableDataSource;
import com.ctrip.platform.dal.dao.datasource.DataSourceCreator;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;
import com.ctrip.platform.dal.dao.datasource.SingleDataSourceWrapper;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.EnvUtils;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.ctrip.platform.dal.exceptions.InvalidConnectionException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class MultiHostDataSource extends DataSourceDelegate implements DataSource, ClosableDataSource, SingleDataSourceWrapper {

    protected static final String ORDERED_ACCESS_STRATEGY = "OrderedAccessStrategy";
    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
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
        String strategyName = clusterProperties.routeStrategyName();
        RouteStrategy strategy;
        if (ClusterConfigXMLConstants.ORDERED_ACCESS_STRATEGY.equalsIgnoreCase(strategyName))
            strategy = new OrderedAccessStrategy();
        else {
            try {
                Class clazz = Class.forName(strategyName);
                strategy = (RouteStrategy) clazz.newInstance();
            } catch (Throwable t) {
                String msg = "Errored constructing route strategy: " + strategyName;
                LOGGER.error(msg, t);
                throw new DalRuntimeException(msg, t);
            }
        }
        strategy.initialize(dataSourceConfigs.keySet(), connFactory, clusterProperties.routeStrategyProperties());
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
    public void close() {
        wrappedDataSources.values().forEach(dataSource -> {
            if (dataSource != null)
                DataSourceCreator.getInstance().returnDataSource(dataSource);
        });
        if (routeStrategy != null) {
            routeStrategy.destroy();
        }
    }

    @Override
    public DataSource getDelegated() {
        return wrappedDataSources.values().iterator().next().getDataSource();
    }

    @Override
    public SingleDataSource getSingleDataSource() {
        return wrappedDataSources.values().iterator().next();
    }

    @Override
    public void forceRefreshDataSource(String name, DataSourceConfigure configure) {}

}
