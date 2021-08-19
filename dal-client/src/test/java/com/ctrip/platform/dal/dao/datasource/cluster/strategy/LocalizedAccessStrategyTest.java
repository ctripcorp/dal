package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

/**
 * @Author limingdong
 * @create 2021/8/19
 */
public class LocalizedAccessStrategyTest extends ShardMetaGenerator {

    private LocalizedAccessStrategy roundRobinAccessStrategy;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        roundRobinAccessStrategy = new LocalizedAccessStrategy();
        roundRobinAccessStrategy.initialize(shardMeta, connectionFactory, caseInsensitiveProperties);
    }

    @Test
    public void pickConnection() throws SQLException {
        HostConnection hostConnection = roundRobinAccessStrategy.pickConnection(requestContext);
        HostSpec hostSpec1 = hostConnection.getHost();
        Assert.assertEquals(getRequestZone(), hostSpec1.zone());  // pick from local

        hostConnection = roundRobinAccessStrategy.pickConnection(requestContext);
        HostSpec hostSpec2 = hostConnection.getHost();
        Assert.assertEquals(getRequestZone(), hostSpec2.zone());  // pick from local

        Assert.assertNotEquals(hostSpec1, hostSpec2);

        roundRobinAccessStrategy.destroy();
    }

    protected String getRequestZone() {
        return SHARB;
    }
}