package com.ctrip.platform.dal.cluster.multihost;

import com.ctrip.platform.dal.cluster.util.CaseInsensitiveProperties;

/**
 * @author c7ch23en
 */
public interface ClusterRouteStrategyConfig {

    String routeStrategyName();

    CaseInsensitiveProperties routeStrategyProperties();

}
