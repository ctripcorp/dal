package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfigParser;
import com.ctrip.framework.dal.cluster.client.config.DalConfigCustomizedOption;
import com.ctrip.framework.dal.cluster.client.config.DefaultLocalConfigProvider;

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
