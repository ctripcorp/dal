package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.exception.HostNotExpectedException;
import com.ctrip.platform.dal.dao.datasource.cluster.DefaultHostConnection;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.RoundRobinStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import static com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.MultiMasterStrategy.BLACKLIST_TIMEOUT_MS;

/**
 * @Author limingdong
 * @create 2021/8/18
 */
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class RoundRobinAccessStrategyTest extends ShardMetaGenerator {

    private long custom_black_list_timeout = 10;

    @Before
    public void setUp() throws Exception {
        caseInsensitiveProperties.set(BLACKLIST_TIMEOUT_MS, String.valueOf(custom_black_list_timeout));
        super.setUp();
    }

    @Override
    protected RouteStrategy getRouteStrategy() {
        return new RoundRobinStrategy();
    }

    @Test
    public void pickNode_1() {
        routeStrategy.init(shardMeta.configuredHosts(), caseInsensitiveProperties);

        HostSpec hostSpec = routeStrategy.pickNode(dalHints);
        Assert.assertEquals(getRequestZone(), hostSpec.zone());  // pick from local

        HostSpec hostSpec1 = routeStrategy.pickNode(dalHints);
        Assert.assertEquals(getRequestZone(), hostSpec1.zone());  // pick from local

        HostSpec hostSpec2  = routeStrategy.pickNode(dalHints);
        Assert.assertEquals(hostSpec, hostSpec2);  // pick from local

        HostSpec hostSpec3  = routeStrategy.pickNode(dalHints);
        Assert.assertEquals(hostSpec1, hostSpec3);  // pick from local

        routeStrategy.dispose();
    }

    @Test(expected = HostNotExpectedException.class)
    public void pickNode_2_NoHostsException() {

        RouteStrategy strategy = getRouteStrategy();
        strategy.init(new HashSet<>(), caseInsensitiveProperties);

        strategy.pickNode(dalHints);

        strategy.dispose();
    }

    @Test
    public void pickNode_3_exceptionNode() throws InterruptedException {

        RouteStrategy strategy = getRouteStrategy();
        strategy.init(shardMeta.configuredHosts(), caseInsensitiveProperties);
        routeStrategy.interceptException(new SQLException("test"), new DefaultHostConnection(null, HostSpecOY_1));

        HostSpec hostSpec1 = strategy.pickNode(dalHints);
        HostSpec hostSpec2 = strategy.pickNode(dalHints);

        Assert.assertEquals(HostSpecOY_2, hostSpec1);
        Assert.assertEquals(HostSpecOY_2, hostSpec2);

        TimeUnit.MILLISECONDS.sleep(custom_black_list_timeout + 1);

        // remove from black list
        hostSpec1 = strategy.pickNode(dalHints);
        hostSpec2 = strategy.pickNode(dalHints);

        Assert.assertNotEquals(hostSpec1, hostSpec2);
        strategy.dispose();
    }

    @Override
    protected void addRB() {
    }

    @Override
    protected void addXY() {
    }

}