package com.ctrip.platform.dal.cluster.config;

public interface ClusterConfigValidator {

    void validateShardStrategies(String clusterName, String config);

    void validateIdGenerators(String clusterName, String config);

}
