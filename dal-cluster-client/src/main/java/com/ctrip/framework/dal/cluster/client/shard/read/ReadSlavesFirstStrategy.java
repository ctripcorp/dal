package com.ctrip.framework.dal.cluster.client.shard.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;

import java.util.HashMap;
import java.util.Set;

public class ReadSlavesFirstStrategy extends ReadMasterStrategy {

    @Override
    public void init(Set<HostSpec> hostSpecs) {
        super.init(hostSpecs);
    }

    @Override
    public HostSpec pickRead(HashMap map) throws HostNotExpectedException {
        if ((boolean)map.get(slaveOnly))
            return slaveOnly();

        Set<HostSpec> slaves = hostMap.get(slaveRole);
        if (slaves == null || slaves.isEmpty())
            return super.pickRead(map);

        return loadBalancePickHost(slaves);
    }

    @Override
    public void onChange(Set<HostSpec> hostSpecs) {

    }

    @Override
    public void dispose() {

    }
}
