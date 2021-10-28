package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.ShardMetaGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author limingdong
 * @create 2021/8/26
 */
public class OBStrategyTest extends ShardMetaGenerator {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        ((OBStrategy)routeStrategy).setZone(getRequestZone());
    }

    @Override
    protected RouteStrategy getRouteStrategy() {
        return new OBStrategy();
    }

    @Test
    public void pickNode() {
        HostSpec hostSpec1 = routeStrategy.pickNode(dalHints);
        Assert.assertEquals(getRequestZone(), hostSpec1.zone());  // pick from local

        HostSpec hostSpec2 = routeStrategy.pickNode(dalHints);
        Assert.assertEquals(getRequestZone(), hostSpec2.zone());  // pick from local

        Assert.assertNotEquals(hostSpec1, hostSpec2);

        routeStrategy.dispose();
    }

    protected String getRequestZone() {
        return SHARB;
    }
}