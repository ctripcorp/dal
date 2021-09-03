package com.ctrip.framework.dal.cluster.client.sharding.idgen;

public interface ClusterIdGeneratorConfig {

    ClusterIdGenerator getIdGenerator(String name);

    String getSequenceDbName();

    int warmUp();

}
