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
public class OrderedAccessStrategyTest extends ShardMetaGenerator {

    private static final String KEY_NAME = "zonesPriority";

    private String VALUE;

    private OrderedAccessStrategy orderedAccessStrategy;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        VALUE = getRequestZone() + "," + SHARB + "," + SHAOY;
        caseInsensitiveProperties.set(KEY_NAME, VALUE);

        orderedAccessStrategy = new OrderedAccessStrategy();
        orderedAccessStrategy.initialize(shardMeta, connectionFactory, caseInsensitiveProperties);
    }

    @Test
    public void tryPickConnection() throws SQLException {
        HostConnection hostConnection = orderedAccessStrategy.pickConnection(requestContext);
        HostSpec hostSpec1 = hostConnection.getHost();
        Assert.assertEquals(getRequestZone(), hostSpec1.zone());  // pick from first

        hostConnection = orderedAccessStrategy.pickConnection(requestContext);
        HostSpec hostSpec2 = hostConnection.getHost();
        Assert.assertEquals(getRequestZone(), hostSpec2.zone());  // pick from first

        Assert.assertEquals(hostSpec1, hostSpec2);

        orderedAccessStrategy.destroy();
    }

    protected String getRequestZone() {
        return SHAXY;
    }
}