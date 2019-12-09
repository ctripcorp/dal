package com.ctrip.framework.dal.cluster.client.cluster;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfigImpl;
import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;

public class DefaultDrcCluster extends DefaultCluster implements DrcCluster {

    private LocalizationConfig localizationConfig;

    public DefaultDrcCluster(ClusterConfigImpl clusterConfig, LocalizationConfig localizationConfig) {
        super(clusterConfig);
        this.localizationConfig = localizationConfig;
    }

    @Override
    public ClusterType getClusterType() {
        return ClusterType.DRC;
    }

    @Override
    public LocalizationConfig getLocalizationConfig() {
        return localizationConfig;
    }

}
