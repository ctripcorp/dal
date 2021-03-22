package com.ctrip.platform.dal.dao.cluster;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.config.DalConfigCustomizedOption;

/**
 * @author c7ch23en
 */
public interface ClusterManager {

    Cluster getOrCreateCluster(String clusterName, DalConfigCustomizedOption customizedOption);

}
