package com.ctrip.framework.dal.cluster.client.config;

public interface ClusterConfigValidator {

    void validateShardStrategies(String clusterName, String config);

    void validateIdGenerators(String clusterName, String config);

}
