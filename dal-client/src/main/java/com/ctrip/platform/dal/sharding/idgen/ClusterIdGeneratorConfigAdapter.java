package com.ctrip.platform.dal.sharding.idgen;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.sharding.idgen.ClusterIdGenerator;
import com.ctrip.framework.dal.cluster.client.sharding.idgen.ClusterIdGeneratorConfig;

public class ClusterIdGeneratorConfigAdapter implements IIdGeneratorConfig {

    private Cluster cluster;

    public ClusterIdGeneratorConfigAdapter(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public IdGenerator getIdGenerator(String name) {
        ClusterIdGeneratorConfig idGeneratorConfig = getIdGeneratorConfig();
        if (idGeneratorConfig == null)
            return null;
        ClusterIdGenerator idGenerator = idGeneratorConfig.getIdGenerator(name);
        if (idGenerator instanceof IdGenerator)
            return (IdGenerator) idGenerator;
        return null;
    }

    @Override
    public String getSequenceDbName() {
        ClusterIdGeneratorConfig idGeneratorConfig = getIdGeneratorConfig();
        if (idGeneratorConfig == null)
            return null;
        return idGeneratorConfig.getSequenceDbName();
    }

    @Override
    public int warmUp() {
        ClusterIdGeneratorConfig idGeneratorConfig = getIdGeneratorConfig();
        if (idGeneratorConfig == null)
            return 0;
        return idGeneratorConfig.warmUp();
    }

    private ClusterIdGeneratorConfig getIdGeneratorConfig() {
        return cluster.getIdGeneratorConfig();
    }

}
