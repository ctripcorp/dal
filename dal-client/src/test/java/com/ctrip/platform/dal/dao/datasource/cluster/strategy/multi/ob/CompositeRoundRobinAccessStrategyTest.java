package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.ShardMetaGenerator;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob.ZoneDividedStrategyContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Map;

/**
 * @Author limingdong
 * @create 2021/8/18
 */
public class CompositeRoundRobinAccessStrategyTest extends ShardMetaGenerator {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected RouteStrategy getRouteStrategy() {
        ZoneDividedStrategyContext shardMetaDivider = new ZoneDividedStrategyContext(shardMeta.configuredHosts(), caseInsensitiveProperties, hostValidator, getRequestZone());
        return shardMetaDivider.accept(strategyTransformer);
    }

    @After
    public void tearDown() throws Exception {
        routeStrategy.dispose();
    }

    @Test
    public void pickNode() throws SQLException {
        HostSpec hostSpec = routeStrategy.pickNode(dalHints);
        Assert.assertEquals(getRequestZone(), hostSpec.zone());  // pick from local

        HostSpec hostSpec1 = routeStrategy.pickNode(dalHints);
        Assert.assertEquals(getRequestZone(), hostSpec1.zone());  // pick from local

        // round robin, so not equal
        Assert.assertNotEquals(hostSpec, hostSpec1);

        // test request zone is down
        if (routeStrategy.isWrapperFor(Map.class)) {
            RouteStrategy localRouteStrategy = (RouteStrategy) routeStrategy.unwrap(Map.class).remove(SHAXY);
            Assert.assertNotNull(localRouteStrategy);
        }

        hostSpec = routeStrategy.pickNode(dalHints);
        Assert.assertNotEquals(getRequestZone(), hostSpec.zone());  // not pick from local
    }

    @Test(expected = UnsupportedOperationException.class)
    public void init() {
        routeStrategy.init(shardMeta.configuredHosts(), caseInsensitiveProperties);
    }

    @Override
    protected String getRequestZone() {
        return SHAXY;
    }
}