package com.ctrip.platform.dal.dao.datasource.cluster;

import java.util.List;
import java.util.Set;

/**
 * @author c7ch23en
 */
public interface RouteOptions {

    Set<HostSpec> configuredHosts();

    List<HostSpec> orderedMasters(String clientZone);

    List<HostSpec> orderedSlaves(String clientZone);

    long failoverTime();

    long blacklistTimeout();

}
