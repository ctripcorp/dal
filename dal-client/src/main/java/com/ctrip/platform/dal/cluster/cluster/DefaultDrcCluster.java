package com.ctrip.platform.dal.cluster.cluster;

import com.ctrip.platform.dal.cluster.config.ClusterConfigImpl;
import com.ctrip.platform.dal.cluster.exception.ClusterConfigException;

public class DefaultDrcCluster extends DefaultCluster implements DrcCluster {

    public DefaultDrcCluster(ClusterConfigImpl clusterConfig) {
        super(clusterConfig);
    }

    @Override
    public void validate() {
        if (getLocalizationConfig() == null || getLocalizationConfig().getUnitStrategyId() == null)
            throw new ClusterConfigException("Missing ucs strategy for drc cluster: " + getClusterName());
    }

    @Override
    public ClusterType getClusterType() {
        return ClusterType.DRC;
    }

}
