package com.ctrip.framework.dal.cluster.client.shard.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.exception.DalMetadataException;
import com.ctrip.framework.dal.cluster.client.util.DalCollections;
import org.junit.Test;

import java.util.List;

import static com.ctrip.framework.dal.cluster.client.shard.read.ReadCurrentZoneSlavesFirstStrategyTest.*;
import static org.junit.Assert.*;

public class ReadMasterZoneSlavesOnlyStrategyTest {

    @Test
    public void init() {
        ReadMasterZoneSlavesOnlyStrategy strategy = new ReadMasterZoneSlavesOnlyStrategy();
        strategy.init(produceHostSpec(), null);

        List<HostSpec> list = null;
        int count = 0;
        for (String zone : strategy.zoneToHost.keySet()) {
            switch (zone) {
                case "shaoy":
                    count++;
                    list = strategy.zoneToHost.get(zone);
                    assertEquals(2, list.size());
                    assertEquals(true, list.containsAll(DalCollections.arrayList(HostSpec.of("ip0", 0, "shaoy"), HostSpec.of("ip1", 1, "shaoy"))));
                    break;
                case "sharb":
                    count++;
                    list = strategy.zoneToHost.get(zone);
                    assertEquals(1, list.size());
                    assertEquals(true, list.containsAll(DalCollections.arrayList(HostSpec.of("ip2", 2, "sharb", true))));
                    break;
                case "shafq":
                    count++;
                    list = strategy.zoneToHost.get(zone);
                    assertEquals(3, list.size());
                    assertEquals(true, list.containsAll(DalCollections.arrayList(HostSpec.of("ip3", 3, "shafq"), HostSpec.of("ip4", 4, "shafq"), HostSpec.of("ip5", 5, "shafq"))));
                    break;
            }
        }

        assertEquals(3, count);


        ReadMasterZoneSlavesOnlyStrategy strategy1 = new ReadMasterZoneSlavesOnlyStrategy();


        try {
            strategy1.init(noMasterHostSpecs(), null);
            fail();
        } catch (Throwable t) {
            assertTrue(t instanceof DalMetadataException);
        }
    }

    @Test
    public void pickMasterZoneSlaveOnly() {
        ReadMasterZoneSlavesOnlyStrategy strategy = new ReadMasterZoneSlavesOnlyStrategy();
        strategy.init(produceHostSpec(), null);
        try{
            strategy.pickMasterZoneSlaveOnly();
            fail();
        } catch (Throwable t) {
            assertTrue(t instanceof DalMetadataException);
        }

        ReadMasterZoneSlavesOnlyStrategy strategy1 = new ReadMasterZoneSlavesOnlyStrategy();
        strategy1.init(masterZoneHasSlave(), null);
        HostSpec hostSpec = strategy1.pickMasterZoneSlaveOnly();
        assertEquals(strategy1.masterZone, hostSpec.getTrimLowerCaseZone());
        assertFalse(hostSpec.isMaster());
    }
}