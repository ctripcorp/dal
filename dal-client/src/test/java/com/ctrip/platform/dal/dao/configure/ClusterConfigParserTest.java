package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfigProvider;
import com.ctrip.framework.dal.cluster.client.config.DefaultLocalConfigProvider;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterConfigParserTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterConfigParserTest.class);

    @Test
    public void test() {
        ClusterConfigProvider provider = new DefaultLocalConfigProvider("TestCluster");
        // todo-lhj
        ClusterConfig config = provider.getClusterConfig(new DefaultDalConfigCustomizedOption());
        LOGGER.info(String.format("cluster config: %s", config.toString()));
    }

}
