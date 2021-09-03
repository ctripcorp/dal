package com.ctrip.platform.dal.cluster.multihost;

import com.ctrip.platform.dal.cluster.exception.ClusterRuntimeException;
import com.ctrip.platform.dal.cluster.util.CaseInsensitiveProperties;

import static com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.MultiMasterStrategy.MULTI_MASTER;

/**
 * @author c7ch23en
 */
public class DefaultClusterRouteStrategyConfig implements ClusterRouteStrategyConfig {

    private final String strategyName;
    private final CaseInsensitiveProperties properties = new CaseInsensitiveProperties();

    public DefaultClusterRouteStrategyConfig(String strategyName) {
        this.strategyName = strategyName;
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

    public void setProperty(String name, String value) {
        if (properties.get(name) != null)
            throw new ClusterRuntimeException("Duplicate property: " + name);
        properties.set(name, value);
    }

}
