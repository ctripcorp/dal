package com.ctrip.framework.dal.cluster.client.multihost;

import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;

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
    public CaseInsensitiveProperties routeStrategyProperties() {
        return properties;
    }

    public void setProperty(String name, String value) {
        if (properties.get(name) != null)
            throw new ClusterRuntimeException("Duplicate property: " + name);
        properties.set(name, value);
    }

}
