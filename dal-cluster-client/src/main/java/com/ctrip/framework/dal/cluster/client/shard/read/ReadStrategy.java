package com.ctrip.framework.dal.cluster.client.shard.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;

import java.util.HashMap;
import java.util.Set;

public interface ReadStrategy {

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
    String NO_MASTER_AVAILABLE = "No master available";

    /**
     * All candidate hosts.
     * @param hostSpecs
     */
    void init(Set<HostSpec> hostSpecs);

    HostSpec pickRead(HashMap<String, Object> map) throws HostNotExpectedException;

    void onChange(Set<HostSpec> hostSpecs);

    void dispose();
}
