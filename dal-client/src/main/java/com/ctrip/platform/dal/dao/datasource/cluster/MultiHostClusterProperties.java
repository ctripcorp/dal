package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.multihost.ClusterRouteStrategyConfig;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.MultiHostStrategy;

/**
 * @author c7ch23en
 */
public interface MultiHostClusterProperties extends ClusterRouteStrategyConfig {

    MultiHostStrategy getMultiHostStrategy();
}
