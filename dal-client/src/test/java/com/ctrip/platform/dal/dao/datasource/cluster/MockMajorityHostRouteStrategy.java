package com.ctrip.platform.dal.dao.datasource.cluster;

import java.util.List;
import java.util.Properties;
import java.util.Set;

public class MockMajorityHostRouteStrategy extends MajorityHostRouteStrategy {
    private ConnectionValidator connectionValidator;
    private HostValidator hostValidator;
    private Set<HostSpec> configuredHosts;
    private ConnectionFactory connFactory;
    private Properties strategyOptions;
    private List<HostSpec> orderHosts;
    private String status; // birth --> init --> destroy

    private enum RouteStrategyStatus {
        birth, init, destroy;
    }



}
