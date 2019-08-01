package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;

/**
 * @author c7ch23en
 */
public interface ClusterConfigProvider {

    ClusterConfig getClusterConfig(String clusterName);

}
