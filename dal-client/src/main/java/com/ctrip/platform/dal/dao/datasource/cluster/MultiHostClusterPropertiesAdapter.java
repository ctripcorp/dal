package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.cluster.multihost.ClusterRouteStrategyConfig;
import com.ctrip.platform.dal.cluster.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.MultiMasterEnum;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

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
    public RouteStrategy getRouteStrategy() {
        String strategyName = routeStrategyName();
        String clazz = MultiMasterEnum.parse(strategyName);
        try {
            return  (com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy)Class.forName(clazz).newInstance();
        } catch (Throwable t) {
            String msg = "Errored constructing route strategy: " + strategyName;
            throw new DalRuntimeException(msg, t);
        }
    }
}
