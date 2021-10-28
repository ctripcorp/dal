package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.mgr;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.ShardMetaGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.MultiMasterStrategy.ZONES_PRIORITY;


/**
 * @Author limingdong
 * @create 2021/8/26
 */
public class MGRStrategyTest extends ShardMetaGenerator {

    private String VALUE;

    @Before
    public void setUp() throws Exception {
        VALUE = getRequestZone() + "," + SHARB + "," + SHAOY;
        caseInsensitiveProperties.set(ZONES_PRIORITY, VALUE);
        super.setUp();
    }

    @Test
    public void pickNode() {
        HostSpec hostSpec1 = routeStrategy.pickNode(dalHints);
        Assert.assertEquals(getRequestZone(), hostSpec1.zone());  // pick from first

        HostSpec hostSpec2 = routeStrategy.pickNode(dalHints);
        Assert.assertEquals(getRequestZone(), hostSpec2.zone());  // pick from first

        Assert.assertEquals(hostSpec1, hostSpec2);

        routeStrategy.dispose();
    }

    protected String getRequestZone() {
        return SHAXY;
    }

    protected RouteStrategy getRouteStrategy() {
        return new MGRStrategy();
    }
}