package com.ctrip.datasource.configure;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfigParser;
import com.ctrip.platform.dal.dao.configure.ClusterConfigProvider;
import com.ctrip.platform.dal.dao.configure.LocalClusterConfigProvider;

/**
 * @author c7ch23en
 */
public class CtripLocalClusterConfigProvider extends LocalClusterConfigProvider implements ClusterConfigProvider {

    public CtripLocalClusterConfigProvider() {}

    public CtripLocalClusterConfigProvider(ClusterConfigParser parser) {
        super(parser);
    }

}
