package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author c7ch23en
 */
public class ZonedHostSorterTest {

    @Test
    public void testSort() {
        ZonedHostSorter sorter = new ZonedHostSorter("0", "a", "b", "c");
        HostSpec host1 = HostSpec.of("ip1", 0, "c");
        HostSpec host2 = HostSpec.of("ip2", 0, "b");
        HostSpec host3 = HostSpec.of("ip3", 0, "b");
        HostSpec host4 = HostSpec.of("ip4", 0, "a");
        HostSpec host5 = HostSpec.of("ip5", 0, "x");
        List<HostSpec> sorted = sorter.sort(new HashSet<>(Arrays.asList(host1, host2, host3, host4, host5)));
        Assert.assertEquals(4, sorted.size());
        Assert.assertEquals("ip4", sorted.get(0).host());
        Assert.assertEquals("ip2", sorted.get(1).host());
        Assert.assertEquals("ip3", sorted.get(2).host());
        Assert.assertEquals("ip1", sorted.get(3).host());
    }

}
