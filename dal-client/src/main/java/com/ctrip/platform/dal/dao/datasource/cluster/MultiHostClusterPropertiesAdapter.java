package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.cluster.ClusterType;
import com.ctrip.framework.dal.cluster.client.multihost.ClusterRouteStrategyConfig;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.LocalizedAccessStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.MultiHostStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.OrderedAccessStrategy;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import static com.ctrip.framework.dal.cluster.client.cluster.ClusterType.MGR;
import static com.ctrip.framework.dal.cluster.client.cluster.ClusterType.OB;

/**
 * @author c7ch23en
 */
public class MultiHostClusterPropertiesAdapter implements MultiHostClusterProperties {

    private final ClusterRouteStrategyConfig routeStrategyConfig;

    private ClusterType clusterType;

    public MultiHostClusterPropertiesAdapter(ClusterRouteStrategyConfig routeStrategyConfig, ClusterType clusterType) {
        this.routeStrategyConfig = routeStrategyConfig;
        this.clusterType = clusterType;
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
    public MultiHostStrategy getMultiHostStrategy() {
        String strategyName = routeStrategyName();
        MultiHostStrategy strategy;
        if (MGR.equals(clusterType) && MGR.defaultRouteStrategies().equalsIgnoreCase(strategyName)) {
            strategy = new OrderedAccessStrategy();
        } else if (OB.equals(clusterType) && OB.defaultRouteStrategies().equalsIgnoreCase(strategyName)) {
            strategy = new LocalizedAccessStrategy();
        } else {
            try {
                Class clazz = Class.forName(strategyName);
                strategy = (MultiHostStrategy) clazz.newInstance();
            } catch (Throwable t) {
                String msg = "Errored constructing route strategy: " + strategyName;
                throw new DalRuntimeException(msg, t);
            }
        }

        return strategy;
    }
}
