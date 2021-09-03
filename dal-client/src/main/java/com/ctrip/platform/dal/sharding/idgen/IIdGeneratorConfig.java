package com.ctrip.platform.dal.sharding.idgen;


import com.ctrip.framework.dal.cluster.client.sharding.idgen.ClusterIdGeneratorConfig;

public interface IIdGeneratorConfig extends ClusterIdGeneratorConfig {

    IdGenerator getIdGenerator(String name);

}
