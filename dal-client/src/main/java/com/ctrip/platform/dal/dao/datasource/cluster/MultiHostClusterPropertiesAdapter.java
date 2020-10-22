package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.multihost.ClusterRouteStrategyConfig;

import java.util.Properties;

/**
 * @author c7ch23en
 */
public class MultiHostClusterPropertiesAdapter implements MultiHostClusterProperties {

    private final ClusterRouteStrategyConfig routeStrategyConfig;

    public MultiHostClusterPropertiesAdapter(ClusterRouteStrategyConfig routeStrategyConfig) {
        this.routeStrategyConfig = routeStrategyConfig;
    }

    @Override
    public String routeStrategyName() {
        return routeStrategyConfig.routeStrategyName();
    }

    @Override
    public Properties routeStrategyProperties() {
        return routeStrategyConfig.routeStrategyProperties();
    }

}
