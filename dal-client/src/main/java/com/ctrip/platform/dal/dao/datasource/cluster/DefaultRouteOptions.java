package com.ctrip.platform.dal.dao.datasource.cluster;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class DefaultRouteOptions implements RouteOptions {

    private final Set<HostSpec> configuredHosts;
    private final List<HostSpec> orderedHosts;
    private final long failoverTime;
    private final long blacklistTimeout;

    public DefaultRouteOptions(Set<HostSpec> configuredHosts, MultiHostClusterOptions clusterOptions) {
        this.configuredHosts = new HashSet<>(configuredHosts);
        this.orderedHosts = orderHosts(configuredHosts, clusterOptions.zoneOrder());
        this.failoverTime = clusterOptions.failoverTime();
        this.blacklistTimeout = clusterOptions.blacklistTimeout();
    }

    private List<HostSpec> orderHosts(Set<HostSpec> configuredHosts, List<String> zoneOrder) {
        List<HostSpec> orderedHosts = new LinkedList<>();
        zoneOrder.forEach(zone -> {
            for (HostSpec host : configuredHosts)
                if (zone.equalsIgnoreCase(host.zone())) {
                    orderedHosts.add(host);
                    break;
                }
        });
        return orderedHosts;
    }

    @Override
    public Set<HostSpec> configuredHosts() {
        return configuredHosts;
    }

    @Override
    public List<HostSpec> orderedMasters(String clientZone) {
        return orderedHosts;
    }

    @Override
    public List<HostSpec> orderedSlaves(String clientZone) {
        return null;
    }

    @Override
    public long failoverTime() {
        return failoverTime;
    }

    @Override
    public long blacklistTimeout() {
        return blacklistTimeout;
    }

}
