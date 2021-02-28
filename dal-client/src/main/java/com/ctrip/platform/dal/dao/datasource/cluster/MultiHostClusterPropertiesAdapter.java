package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.multihost.ClusterRouteStrategyConfig;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;

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
    public CaseInsensitiveProperties routeStrategyProperties() {
        return routeStrategyConfig.routeStrategyProperties();
    }

}
