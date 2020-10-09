package com.ctrip.platform.dal.dao.datasource.cluster;

import java.util.List;

/**
 * @author c7ch23en
 */
public interface MultiHostClusterOptions {

    String routeStrategy();

    boolean isLocalAccessMode();

    List<String> zoneOrder();

    long failoverTime();

    long blacklistTimeout();

}
