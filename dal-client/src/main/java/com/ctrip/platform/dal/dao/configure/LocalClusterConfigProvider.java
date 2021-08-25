package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.cluster.config.ClusterConfig;
import com.ctrip.platform.dal.cluster.config.ClusterConfigParser;
import com.ctrip.platform.dal.cluster.config.DalConfigCustomizedOption;
import com.ctrip.platform.dal.cluster.config.DefaultLocalConfigProvider;

/**
 * @author c7ch23en
 */
public class LocalClusterConfigProvider extends AbstractClusterConfigProvider implements ClusterConfigProvider {

    public LocalClusterConfigProvider() {}

    public LocalClusterConfigProvider(ClusterConfigParser parser) {
        super(parser);
    }

    @Override
    public ClusterConfig getClusterConfig(String clusterName, DalConfigCustomizedOption customizedOption) {
        DefaultLocalConfigProvider configProvider = new DefaultLocalConfigProvider(clusterName, getParser());
        return configProvider.getClusterConfig(customizedOption);
    }

}
