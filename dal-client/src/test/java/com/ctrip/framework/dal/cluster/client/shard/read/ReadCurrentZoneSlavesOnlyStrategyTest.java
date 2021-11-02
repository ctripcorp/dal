package com.ctrip.framework.dal.cluster.client.shard.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.exception.DalMetadataException;
import com.ctrip.platform.dal.dao.DalHints;
import junit.framework.Assert;
import org.junit.Test;

import static com.ctrip.framework.dal.cluster.client.shard.read.ReadCurrentZoneSlavesFirstStrategyTest.produceHostSpec;
import static org.junit.Assert.*;

public class ReadCurrentZoneSlavesOnlyStrategyTest {

    @Test
    public void pickCurrentZoneSlaveOnly() {
        ReadCurrentZoneSlavesOnlyStrategy strategy = new ReadCurrentZoneSlavesOnlyStrategy();
        strategy.init(ReadCurrentZoneSlavesFirstStrategyTest.produceHostSpec(), null);

        strategy.currentZone = "shaoy";
        HostSpec hostSpec = strategy.pickRead(new DalHints());
        assertEquals(strategy.currentZone, hostSpec.getTrimLowerCaseZone());

        strategy.currentZone = "";
        try{
            hostSpec = strategy.pickRead(new DalHints());
            fail();
        } catch (Throwable t) {
            assertEquals(true, t instanceof DalMetadataException);
            assertEquals(" has no database in ", t.getMessage());
        }

        strategy.currentZone = "shaxy";
        try{
            hostSpec = strategy.pickRead(new DalHints());
            fail();
        } catch (Throwable t) {
            assertEquals(true, t instanceof DalMetadataException);
            assertEquals(" has no database in " + strategy.currentZone, t.getMessage());
        }
    }
}