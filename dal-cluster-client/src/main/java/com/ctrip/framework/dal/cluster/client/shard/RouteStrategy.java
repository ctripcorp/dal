package com.ctrip.framework.dal.cluster.client.shard;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;

import java.util.Map;
import java.util.Set;

public interface RouteStrategy {

    boolean enable = false;
    boolean readOnly = false;
    boolean failoverToMaster = true;
    // todo-lhj 默认值设置定义
    int slavesFailOverDelayTimeMS = 1000;

    // constant
    String masterRole = "master";
    String slaveRole = "slave";
    String slaveOnly = "slaveOnly";
    String isPro = "isPro";
    String routeStrategy = "routeStrategy";

    // message
    String NO_HOST_AVAILABLE = "No %s available";

    /**
     * All candidate hosts.
     * @param hostSpecs
     */
    void init(Set<HostSpec> hostSpecs);

    HostSpec pickRead(Map<String, Object> map) throws HostNotExpectedException;

    void onChange(Set<HostSpec> hostSpecs);

    void dispose();
}
