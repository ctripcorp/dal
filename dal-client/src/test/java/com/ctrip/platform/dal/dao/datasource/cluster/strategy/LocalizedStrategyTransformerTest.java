package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

/**
 * @Author limingdong
 * @create 2021/8/18
 */
public class LocalizedStrategyTransformerTest extends ShardMetaGenerator {

    private LocalizedStrategyTransformer strategyTransformer;

    private StrategyContext strategyContext;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        strategyTransformer = new LocalizedStrategyTransformer();
        strategyContext = new ZoneDividedStrategyContext(shardMeta, connectionFactory, caseInsensitiveProperties);
    }

    @Test
    public void visit() throws SQLException {
        LocalizedAccessStrategy localizedAccessStrategy = (LocalizedAccessStrategy) strategyContext.accept(strategyTransformer);
        Assert.assertTrue(!localizedAccessStrategy.isEmpty());

        HostConnection hostConnection = localizedAccessStrategy.pickConnection(requestContext);
        HostSpec hostSpec1 = hostConnection.getHost();
        Assert.assertEquals(getRequestZone(), hostSpec1.zone());  // pick from local

        hostConnection = localizedAccessStrategy.pickConnection(requestContext);
        HostSpec hostSpec2 = hostConnection.getHost();
        Assert.assertEquals(getRequestZone(), hostSpec2.zone());  // pick from local

        HostSpec hostSpec_1 = new HostSpec(SHAOY_IP1, SHAOY_PORT1, SHAOY, true);
        HostSpec hostSpec_2 = new HostSpec(SHAOY_IP2, SHAOY_PORT2, SHAOY, true);

        boolean same = (hostSpec1.equals(hostSpec_1) && hostSpec2.equals(hostSpec_2)) || (hostSpec1.equals(hostSpec_2) && hostSpec2.equals(hostSpec_1));
        Assert.assertTrue(same);

        localizedAccessStrategy.destroy();
    }
}