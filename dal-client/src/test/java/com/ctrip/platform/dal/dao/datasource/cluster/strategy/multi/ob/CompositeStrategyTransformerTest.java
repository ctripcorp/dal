package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.ShardMetaGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author limingdong
 * @create 2021/8/18
 */
public class CompositeStrategyTransformerTest extends ShardMetaGenerator {

    private CompositeStrategyTransformer strategyTransformer;

    private StrategyContext strategyContext;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        strategyTransformer = new CompositeStrategyTransformer();
        strategyContext = new ZoneDividedStrategyContext(shardMeta.configuredHosts(), caseInsensitiveProperties, hostValidator, getRequestZone());
    }

    @Override
    protected RouteStrategy getRouteStrategy() {
        return null;
    }

    @Test
    public void visit() {
        CompositeRoundRobinStrategy localizedAccessStrategy = (CompositeRoundRobinStrategy) strategyContext.accept(strategyTransformer);
        Assert.assertTrue(!localizedAccessStrategy.isEmpty());

        HostSpec hostSpec1 = localizedAccessStrategy.pickNode(dalHints);
        Assert.assertEquals(getRequestZone(), hostSpec1.zone());  // pick from local

        HostSpec hostSpec2 = localizedAccessStrategy.pickNode(dalHints);
        Assert.assertEquals(getRequestZone(), hostSpec2.zone());  // pick from local

        HostSpec hostSpec_1 = new HostSpec(SHAOY_IP1, SHAOY_PORT1, SHAOY, true);
        HostSpec hostSpec_2 = new HostSpec(SHAOY_IP2, SHAOY_PORT2, SHAOY, true);

        boolean same = (hostSpec1.equals(hostSpec_1) && hostSpec2.equals(hostSpec_2)) || (hostSpec1.equals(hostSpec_2) && hostSpec2.equals(hostSpec_1));
        Assert.assertTrue(same);

        localizedAccessStrategy.dispose();
    }
}