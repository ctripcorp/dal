package com.ctrip.framework.dal.cluster.client.cluster;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfigImpl;
import com.ctrip.framework.dal.cluster.client.exception.ClusterConfigException;

public class DefaultDrcCluster extends DefaultCluster implements DrcCluster {

    private int unitStrategyId;

    public DefaultDrcCluster(ClusterConfigImpl clusterConfig) {
        super(clusterConfig);
        Integer unitStrategyId = clusterConfig.getUnitStrategyId();
        if (unitStrategyId == null)
            throw new ClusterConfigException("unitStrategyId is necessary for drc cluster");
        this.unitStrategyId = unitStrategyId;
    }

    @Override
    public ClusterType getClusterType() {
        return ClusterType.DRC;
    }

    @Override
    public int getUnitStrategyId() {
        return unitStrategyId;
    }

}
