package com.ctrip.framework.dal.cluster.client.multihost;

import com.ctrip.framework.dal.cluster.client.cluster.RouteStrategyEnum;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import static com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.MultiMasterStrategy.MULTI_MASTER;

/**
 * @author c7ch23en
 */
public class DefaultClusterRouteStrategyConfig implements ClusterRouteStrategyConfig {

    private final String strategyName;

    private final CaseInsensitiveProperties properties = new CaseInsensitiveProperties();

    public DefaultClusterRouteStrategyConfig(String strategyName) {
        this.strategyName = strategyName;
        setProperty(MULTI_MASTER, String.valueOf(generate().multiMaster()));
    }

    @Override
    public String routeStrategyName() {
        return strategyName;
    }

    @Override
    public boolean multiMaster() {
        return properties.getBool(MULTI_MASTER, false);
    }

    @Override
    public CaseInsensitiveProperties routeStrategyProperties() {
        return properties;
    }

    @Override
    public RouteStrategy generate() {
        String strategyName = routeStrategyName();
        String clazz = RouteStrategyEnum.parse(strategyName);
        try {
            return  (com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy)Class.forName(clazz).newInstance();
        } catch (Throwable t) {
            String msg = "Error constructing route strategy: " + strategyName;
            throw new DalRuntimeException(msg, t);
        }
    }

    public void setProperty(String name, String value) {
        properties.set(name, value);
    }

}
