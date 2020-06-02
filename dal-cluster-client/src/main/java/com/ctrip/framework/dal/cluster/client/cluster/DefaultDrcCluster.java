package com.ctrip.framework.dal.cluster.client.cluster;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfigImpl;
import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.dal.cluster.client.exception.ClusterConfigException;

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
