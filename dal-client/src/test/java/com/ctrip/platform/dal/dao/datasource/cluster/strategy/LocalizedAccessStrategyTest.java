package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

/**
 * @Author limingdong
 * @create 2021/8/18
 */
public class LocalizedAccessStrategyTest extends ShardMetaGenerator {

    private CompositeRoundRobinAccessStrategy localizedAccessStrategy;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        ZoneDividedStrategyContext shardMetaDivider = new ZoneDividedStrategyContext(shardMeta, connectionFactory, caseInsensitiveProperties, hostValidator);
        localizedAccessStrategy = (CompositeRoundRobinAccessStrategy) shardMetaDivider.accept(strategyTransformer);
    }

    @After
    public void tearDown() throws Exception {
        localizedAccessStrategy.destroy();
    }

    @Test
    public void pickConnection() throws SQLException {
        HostConnection hostConnection = localizedAccessStrategy.pickConnection(requestContext);
        HostSpec hostSpec = hostConnection.getHost();
        Assert.assertEquals(getRequestZone(), hostSpec.zone());  // pick from local

        hostConnection = localizedAccessStrategy.pickConnection(requestContext);
        HostSpec hostSpec1 = hostConnection.getHost();
        Assert.assertEquals(getRequestZone(), hostSpec1.zone());  // pick from local

        // round robin, so not equal
        Assert.assertNotEquals(hostSpec, hostSpec1);

        // test request zone is down
        MultiHostStrategy multiHostStrategy = localizedAccessStrategy.remove(SHAXY);
        Assert.assertNotNull(multiHostStrategy);

        hostConnection = localizedAccessStrategy.pickConnection(requestContext);
        hostSpec = hostConnection.getHost();
        Assert.assertNotEquals(getRequestZone(), hostSpec.zone());  // not pick from local
    }

    @Test(expected = UnsupportedOperationException.class)
    public void initialize() {
        localizedAccessStrategy.initialize(shardMeta, connectionFactory, caseInsensitiveProperties);
    }

    @Override
    protected String getRequestZone() {
        return SHAXY;
    }
}