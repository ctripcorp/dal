package com.ctrip.platform.dal.dao.cluster;

import com.ctrip.framework.dal.cluster.client.Cluster;

/**
 * @author c7ch23en
 */
public interface ClusterManager {

    Cluster getOrCreateCluster(String clusterName);

}
