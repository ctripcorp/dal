package com.ctrip.framework.dal.cluster.client.config;

/**
 * @author c7ch23en
 */
public interface ClusterConfigProvider {

    ClusterConfig getClusterConfig(DalConfigCustomizedOption customizedOption);

}
