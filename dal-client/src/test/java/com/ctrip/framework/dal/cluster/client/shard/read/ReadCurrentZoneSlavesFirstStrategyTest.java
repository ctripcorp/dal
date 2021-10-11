package com.ctrip.framework.dal.cluster.client.shard.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.util.DalCollections;
import com.ctrip.platform.dal.dao.DalHints;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ReadCurrentZoneSlavesFirstStrategyTest {

    @Test
    public void init() {
        ReadCurrentZoneSlavesFirstStrategy strategy = new ReadCurrentZoneSlavesFirstStrategy();


        strategy.init(produceHostSpec(), null);

        List<HostSpec> list = null;
        for (String zone : strategy.zoneToHost.keySet()) {
            switch (zone) {
                case "shaoy":
                    list = strategy.zoneToHost.get(zone);
                    assertEquals(2, list.size());
                    assertEquals(true, list.containsAll(DalCollections.arrayList(HostSpec.of("ip0", 0, "shaoy"), HostSpec.of("ip1", 1, "shaoy"))));
                    break;
                case "sharb":
                    list = strategy.zoneToHost.get(zone);
                    assertEquals(1, list.size());
                    assertEquals(true, list.containsAll(DalCollections.arrayList(HostSpec.of("ip2", 2, "sharb", true))));
                    break;
                case "shafq":
                    list = strategy.zoneToHost.get(zone);
                    assertEquals(3, list.size());
                    assertEquals(true, list.containsAll(DalCollections.arrayList(HostSpec.of("ip3", 3, "shafq"), HostSpec.of("ip4", 4, "shafq"), HostSpec.of("ip5", 5, "shafq"))));
                    break;
            }
        }
    }

    private Set<HostSpec> produceHostSpec() {
        HashSet<HostSpec> set = new HashSet<>();
        set.add(HostSpec.of("ip0", 0, " shaoy"));
        set.add(HostSpec.of("ip1", 1, "shaoy "));
        set.add(HostSpec.of("ip2", 2, " sharb ", true));
        set.add(HostSpec.of("ip3", 3, "  shafq  "));
        set.add(HostSpec.of("ip4", 4, "shafq"));
        set.add(HostSpec.of("ip5", 5, "shafq"));
        return set;
    }


    @Test
    public void pickRead() {
        ReadCurrentZoneSlavesFirstStrategy strategy = new ReadCurrentZoneSlavesFirstStrategy();
        strategy.init(produceHostSpec(), null);
        strategy.currentZone = "shaoy";

        HostSpec hostSpec = strategy.pickRead(new DalHints());
        assertEquals("SHAOY", hostSpec.zone());

        strategy.currentZone = "sharb";
        assertEquals("SHARB", hostSpec.zone());
    }


}