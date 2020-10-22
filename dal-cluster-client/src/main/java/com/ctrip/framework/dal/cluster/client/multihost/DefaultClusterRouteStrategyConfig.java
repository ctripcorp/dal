package com.ctrip.framework.dal.cluster.client.multihost;

import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;

import java.util.Properties;

/**
 * @author c7ch23en
 */
public class DefaultClusterRouteStrategyConfig implements ClusterRouteStrategyConfig {

    private final String strategyName;
    private final Properties properties = new Properties();

    public DefaultClusterRouteStrategyConfig(String strategyName) {
        this.strategyName = strategyName;
    }

    @Override
    public String routeStrategyName() {
        return strategyName;
    }

    @Override
    public Properties routeStrategyProperties() {
        return properties;
    }

    public void setProperty(String name, String value) {
        if (properties.getProperty(name) != null)
            throw new ClusterRuntimeException("Duplicate property: " + name);
        properties.setProperty(name, value);
    }

}
