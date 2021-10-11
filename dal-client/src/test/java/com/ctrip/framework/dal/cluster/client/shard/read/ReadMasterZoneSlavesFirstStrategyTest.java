package com.ctrip.framework.dal.cluster.client.shard.read;

import com.ctrip.framework.dal.cluster.client.exception.DalMetadataException;
import org.junit.Assert;
import org.junit.Test;

import static com.ctrip.framework.dal.cluster.client.shard.read.ReadCurrentZoneSlavesFirstStrategyTest.*;
import static org.junit.Assert.*;

public class ReadMasterZoneSlavesFirstStrategyTest {

    @Test
    public void pickMasterZoneSlave() {
        ReadMasterZoneSlavesFirstStrategy strategy  = new ReadMasterZoneSlavesFirstStrategy();
        strategy.init(masterZoneHasSlave(), null);

        assertEquals(strategy.masterZone, strategy.pickMasterZoneSlave().getTrimLowerCaseZone());
        assertFalse(strategy.pickMasterZoneSlave().isMaster());

        try{
            ReadMasterZoneSlavesFirstStrategy strategy1  = new ReadMasterZoneSlavesFirstStrategy();
            strategy1.init(noMasterHostSpecs(), null);
            fail();
        } catch (Throwable t) {
            assertTrue(t instanceof DalMetadataException);
        }

        ReadMasterZoneSlavesFirstStrategy strategy2  = new ReadMasterZoneSlavesFirstStrategy();
        strategy2.init(produceHostSpec(), null);

        assertNull(strategy2.pickMasterZoneSlave());
    }
}