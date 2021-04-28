package com.ctrip.framework.dal.cluster.client.shard.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ReadMasterStrategy implements ReadStrategy {
    // todo-lhj 策略内容实现

    protected HashMap<String, Set<HostSpec>> hostMap = new HashMap<>();

    @Override
    public void init(Set<HostSpec> hostSpecs) {
        Set<HostSpec> masters = new HashSet<>();
        Set<HostSpec> slaves = new HashSet<>();

        for (HostSpec hostSpec : hostSpecs) {
            if (hostSpec.isMaster())
                masters.add(hostSpec);
            else slaves.add(hostSpec);
        }

        hostMap.putIfAbsent(masterRole, masters);
        hostMap.putIfAbsent(slaveRole, slaves);
    }

    @Override
    public HostSpec pickRead(HashMap map) throws HostNotExpectedException {
        Set<HostSpec> masters = hostMap.get(masterRole);
        if (masters == null || masters.size() < 1)
            throw new HostNotExpectedException(NO_MASTER_AVAILABLE);
        return masters.iterator().next();
    }

    @Override
    public void onChange(Set<HostSpec> hostSpecs) {

    }

    @Override
    public void dispose() {

    }
}
