package com.ctrip.platform.dal.dao.configure;


import com.ctrip.platform.dal.cluster.config.ClusterConfig;
import com.ctrip.platform.dal.cluster.config.DalConfigCustomizedOption;

/**
 * @author c7ch23en
 */
public interface ClusterConfigProvider {

    ClusterConfig getClusterConfig(String clusterName, DalConfigCustomizedOption customizedOption);

}
