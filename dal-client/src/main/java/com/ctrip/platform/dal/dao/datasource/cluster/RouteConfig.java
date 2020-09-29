package com.ctrip.platform.dal.dao.datasource.cluster;

import java.util.List;
import java.util.Set;

/**
 * @author c7ch23en
 */
public interface RouteConfig {

    Set<Host> configuredHosts();

    boolean localAccessEnabled();

    List<Host> prioritizedHosts();

    List<Host> orderedFailoverHosts(Host primaryHost);

    long failoverTime();

    long blacklistTimeout();

}
