package com.ctrip.framework.dal.cluster.client.shard.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.cluster.RouteStrategyEnum;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;
import com.ctrip.platform.dal.dao.DalHints;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static com.ctrip.framework.dal.cluster.client.shard.read.ReadCurrentZoneSlavesFirstStrategyTest.*;
import static org.junit.Assert.*;

public class ReadSlavesFirstStrategyTest {

    private static ReadSlavesFirstStrategy strategy;
    private static Set<HostSpec> set = produceHostSpec();

    @Before
    public void initStrategy() {
        strategy = new ReadSlavesFirstStrategy();
        strategy.init(set, null);
    }

    @Test
    public void init() {
        assertEquals(1, strategy.hostMap.get("master").size());
        assertEquals(set.size() - 1, strategy.hostMap.get("slave").size());
    }

    @Test
    public void pickSlaveFirst() {
        assertEquals(false, strategy.pickSlaveFirst().isMaster());

        ReadSlavesFirstStrategy strategy1 = new ReadSlavesFirstStrategy();
        strategy1.init(noSlaveHostSpecs(), null);
        assertEquals(true, strategy1.pickSlaveFirst().isMaster());
    }

    @Test
    public void hintsRoute() {
        strategy.init(set, null);

        DalHints dalHints = new DalHints().routeStrategy(RouteStrategyEnum.READ_MASTER);
        assertEquals("sharb", strategy.hintsRoute(dalHints).getTrimLowerCaseZone());

        DalHints dalHints1 = new DalHints().masterOnly();
        assertEquals(true, strategy.hintsRoute(dalHints1).isMaster());

        ReadMasterStrategy readMasterStrategy = new ReadMasterStrategy();
        readMasterStrategy.init(set, null);
        DalHints dalHints2 = new DalHints().slaveOnly();
        assertEquals(false, readMasterStrategy.hintsRoute(dalHints2).isMaster());
    }

    @Test
    public void pickMaster() {
        assertEquals(true, strategy.pickMaster().isMaster());

        try {
            strategy.init(noMasterHostSpecs(), null);
            strategy.pickMaster();
        } catch (Throwable t) {
            assertEquals(true, t instanceof HostNotExpectedException);
        }
    }

    @Test
    public void slaveOnly() {
        strategy.init(set, null);
        assertEquals(false, strategy.slaveOnly().isMaster());

        try {
            strategy.init(noSlaveHostSpecs(), null);
            strategy.slaveOnly();
        } catch (Throwable t) {
            assertEquals(true, t instanceof HostNotExpectedException);
        }
    }
}