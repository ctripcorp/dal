package com.ctrip.framework.dal.cluster.client.multihost;

import java.util.Properties;

/**
 * @author c7ch23en
 */
public interface ClusterRouteStrategyConfig {

    String routeStrategyName();

    Properties routeStrategyProperties();

}
