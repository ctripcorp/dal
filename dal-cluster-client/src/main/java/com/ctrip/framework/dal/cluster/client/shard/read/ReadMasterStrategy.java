package com.ctrip.framework.dal.cluster.client.shard.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;

import java.util.Map;
import java.util.Set;

public class ReadMasterStrategy extends ReadSlavesFirstStrategy {

    @Override
    public void init(Set<HostSpec> hostSpecs) {
        super.init(hostSpecs);
    }

    @Override
    public HostSpec pickRead(Map<String, Object> map) throws HostNotExpectedException {
        if ((boolean)map.get(slaveOnly) && (boolean)map.get(isPro))
            return slaveOnly();

        if ((boolean)map.get(slaveOnly))
            return super.pickRead(map);

        return pickMaster();
    }

    @Override
    public void onChange(Set<HostSpec> hostSpecs) {

    }

    @Override
    public void dispose() {

    }
}
