package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.cluster.cluster.ClusterType;
import com.ctrip.platform.dal.cluster.multihost.ClusterRouteStrategyConfig;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.mgr.MGRStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob.OBStrategy;
import com.ctrip.platform.dal.cluster.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import static com.ctrip.platform.dal.cluster.cluster.ClusterType.MGR;
import static com.ctrip.platform.dal.cluster.cluster.ClusterType.OB;

/**
 * @author c7ch23en
 */
public class MultiHostClusterPropertiesAdapter implements MultiHostClusterProperties {

    private final ClusterRouteStrategyConfig routeStrategyConfig;

    private ClusterType clusterType;

    public MultiHostClusterPropertiesAdapter(ClusterRouteStrategyConfig routeStrategyConfig, ClusterType clusterType, String clusterName) {
        this.routeStrategyConfig = routeStrategyConfig;
        this.clusterType = clusterType;
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
    public CaseInsensitiveProperties routeStrategyProperties() {
        return routeStrategyConfig.routeStrategyProperties();
    }

    @Override
    public RouteStrategy getRouteStrategy() {
        String strategyName = routeStrategyName();
        RouteStrategy strategy;
        if (MGR.equals(clusterType) && MGR.defaultRouteStrategies().equalsIgnoreCase(strategyName)) {
            strategy = new MGRStrategy();
        } else if (OB.equals(clusterType) && OB.defaultRouteStrategies().equalsIgnoreCase(strategyName)) {
            strategy = new OBStrategy();
        } else {
            try {
                Class clazz = Class.forName(strategyName);
                strategy = (RouteStrategy) clazz.newInstance();
            } catch (Throwable t) {
                String msg = "Errored constructing route strategy: " + strategyName;
                throw new DalRuntimeException(msg, t);
            }
        }

        return strategy;
    }
}
