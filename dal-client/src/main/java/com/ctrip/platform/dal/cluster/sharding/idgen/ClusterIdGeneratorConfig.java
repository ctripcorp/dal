package com.ctrip.platform.dal.cluster.sharding.idgen;

public interface ClusterIdGeneratorConfig {

    ClusterIdGenerator getIdGenerator(String name);

    String getSequenceDbName();

    int warmUp();

}
