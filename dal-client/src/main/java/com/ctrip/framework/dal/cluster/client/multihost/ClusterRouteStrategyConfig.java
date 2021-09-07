package com.ctrip.framework.dal.cluster.client.multihost;

import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;

/**
 * @author c7ch23en
 */
public interface ClusterRouteStrategyConfig {

    String CLUSTER_NAME = "clusterName";

    String DEFAULT_CLUSTER_NAME_VALUE = "unknown-cluster";

    String routeStrategyName();

    boolean multiMaster();

    CaseInsensitiveProperties routeStrategyProperties();

}
