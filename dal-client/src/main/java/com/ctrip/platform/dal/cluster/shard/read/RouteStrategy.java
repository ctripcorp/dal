package com.ctrip.platform.dal.cluster.shard.read;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.exception.HostNotExpectedException;

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
