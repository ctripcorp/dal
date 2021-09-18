package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob.CompositeRoundRobinStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob.ZoneDividedStrategyContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author limingdong
 * @create 2021/8/18
 */
public class ShardMetaDividerTest extends ShardMetaGenerator {

    private ZoneDividedStrategyContext strategyContext;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        strategyContext = new ZoneDividedStrategyContext(shardMeta.configuredHosts(), caseInsensitiveProperties, hostValidator);
    }

    @Override
    protected RouteStrategy getRouteStrategy() {
        return null;
    }

    @Test
    public void divide() {
        CompositeRoundRobinStrategy localizedAccessStrategy = (CompositeRoundRobinStrategy) strategyContext.accept(strategyTransformer);
        Assert.assertEquals(3, localizedAccessStrategy.size());
    }

}