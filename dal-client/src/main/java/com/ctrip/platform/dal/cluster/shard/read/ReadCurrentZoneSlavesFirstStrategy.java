package com.ctrip.platform.dal.cluster.shard.read;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.exception.HostNotExpectedException;

import java.util.Map;
import java.util.Set;

public class ReadCurrentZoneSlavesFirstStrategy extends ReadSlavesFirstStrategy {
    @Override
    public void init(Set<HostSpec> hostSpecs) {

    }

    @Override
    public HostSpec pickRead(Map<String, Object> map) throws HostNotExpectedException {
        return null;
    }

    @Override
    public void onChange(Set<HostSpec> hostSpecs) {

    }

    @Override
    public void dispose() {

    }
}
