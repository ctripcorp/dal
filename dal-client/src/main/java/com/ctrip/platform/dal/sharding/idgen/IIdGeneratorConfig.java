package com.ctrip.platform.dal.sharding.idgen;


import com.ctrip.platform.dal.cluster.sharding.idgen.ClusterIdGeneratorConfig;

public interface IIdGeneratorConfig extends ClusterIdGeneratorConfig {

    IdGenerator getIdGenerator(String name);

}
