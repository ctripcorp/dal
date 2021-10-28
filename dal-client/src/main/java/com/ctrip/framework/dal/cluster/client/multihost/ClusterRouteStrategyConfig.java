package com.ctrip.framework.dal.cluster.client.multihost;

import com.ctrip.framework.dal.cluster.client.base.ComponentGenerator;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;

/**
 * @author c7ch23en
 */
public interface ClusterRouteStrategyConfig extends ComponentGenerator<RouteStrategy> {

    String CLUSTER_NAME = "clusterName";
    String DEFAULT_CLUSTER_NAME_VALUE = "unknown-cluster";

    String routeStrategyName();

    boolean multiMaster();

    CaseInsensitiveProperties routeStrategyProperties();

}
