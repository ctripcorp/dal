package com.ctrip.platform.dal.dao.cluster;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.cluster.config.DalConfigCustomizedOption;

/**
 * @author c7ch23en
 */
public interface ClusterManager {

    Cluster getOrCreateCluster(String clusterName, DalConfigCustomizedOption customizedOption);

}
