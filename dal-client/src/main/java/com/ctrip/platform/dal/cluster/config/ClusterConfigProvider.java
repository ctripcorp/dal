package com.ctrip.platform.dal.cluster.config;

/**
 * @author c7ch23en
 */
public interface ClusterConfigProvider {

    ClusterConfig getClusterConfig();

    ClusterConfig getClusterConfig(DalConfigCustomizedOption customizedOption);

}
