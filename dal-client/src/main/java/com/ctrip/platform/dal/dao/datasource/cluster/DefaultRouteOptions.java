package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.dao.helper.Sorter;

import java.util.*;

/**
 * @author c7ch23en
 */
public class DefaultRouteOptions implements RouteOptions {

    private final Set<HostSpec> configuredHosts;
    private final List<HostSpec> orderedHosts;
    private final MultiHostClusterProperties clusterProperties;
    private final Sorter<HostSpec> hostSorter;

    public DefaultRouteOptions(Set<HostSpec> configuredHosts, MultiHostClusterProperties clusterProperties) {
        this.configuredHosts = new HashSet<>(configuredHosts);
        this.hostSorter = new ZonedHostSorter(clusterProperties.zoneOrder());
        this.orderedHosts = this.hostSorter.sort(configuredHosts);
        this.clusterProperties = clusterProperties;
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
        return new ArrayList<>(0);
    }

    @Override
    public long failoverTime() {
        return clusterProperties.failoverTime();
    }

    @Override
    public long blacklistTimeout() {
        return clusterProperties.blacklistTimeout();
    }

}
