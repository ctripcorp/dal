package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.multihost.ClusterRouteStrategyConfig;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;

/**
 * @author c7ch23en
 */
public class MultiHostClusterPropertiesAdapter implements MultiHostClusterProperties {

    private final ClusterRouteStrategyConfig routeStrategyConfig;

    public MultiHostClusterPropertiesAdapter(ClusterRouteStrategyConfig routeStrategyConfig, String clusterName) {
        this.routeStrategyConfig = routeStrategyConfig;
        setClusterName(clusterName);
    }

    private void setClusterName(String clusterName) {
        if (this.routeStrategyConfig != null) {
            CaseInsensitiveProperties properties = this.routeStrategyConfig.routeStrategyProperties();
            if (properties != null) {
                properties.set(CLUSTER_NAME, clusterName);
            }
        }
    }

    @Override
    public String routeStrategyName() {
        return routeStrategyConfig.routeStrategyName();
    }

    @Override
    public boolean multiMaster() {
        return routeStrategyConfig.multiMaster();
    }

    @Override
    public CaseInsensitiveProperties routeStrategyProperties() {
        return routeStrategyConfig.routeStrategyProperties();
    }

    @Override
    public RouteStrategy generate() {
        return routeStrategyConfig.generate();
    }
}
