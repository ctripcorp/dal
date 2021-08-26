package com.ctrip.platform.dal.cluster.cluster;

import com.ctrip.platform.dal.cluster.config.ClusterConfig;
import com.ctrip.platform.dal.cluster.config.ClusterConfigProvider;
import com.ctrip.platform.dal.cluster.config.DefaultLocalConfigProvider;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author c7ch23en
 */
public class DefaultLocalConfigProviderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLocalConfigProviderTest.class);

    @Test
    public void test() {
        ClusterConfigProvider provider = new DefaultLocalConfigProvider("demo-cluster");
        // todo-lhj xiu dan ce
        ClusterConfig config = provider.getClusterConfig(DefaultClusterTest.mockCustomizedOption());
        LOGGER.info(String.format("cluster config: %s", config.toString()));
    }

    @Test
    public void testParseInt() {
        Assert.assertEquals(-1, Integer.parseInt("-1"));
    }

}
