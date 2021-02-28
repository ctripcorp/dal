package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.dao.helper.Sorter;
import com.ctrip.platform.dal.dao.helper.StringValueComparator;

import java.util.*;

/**
 * @author c7ch23en
 */
public class ZonedHostSorter implements Sorter<HostSpec> {

    private final List<String> zoneOrder;
    private final Comparator<HostSpec> comparator = new StringValueComparator<>();

    public ZonedHostSorter(List<String> zoneOrder) {
        this.zoneOrder = zoneOrder != null ? zoneOrder : new ArrayList<>(0);
    }

    public ZonedHostSorter(String... zoneOrder) {
        this(Arrays.asList(zoneOrder));
    }

    @Override
    public List<HostSpec> sort(Set<HostSpec> raw) {
        Map<String, TreeSet<HostSpec>> map = new HashMap<>(zoneOrder.size());
        raw.forEach(host -> {
            if (host.zone() != null) {
                TreeSet<HostSpec> set = map.computeIfAbsent(host.zone(), k -> new TreeSet<>(comparator));
                set.add(host);
            }
        });
        List<HostSpec> sorted = new LinkedList<>();
        zoneOrder.forEach(zone -> {
            TreeSet<HostSpec> set = map.remove(zone.toUpperCase());
            if (set != null)
                sorted.addAll(set);
        });
        return sorted;
    }

}
