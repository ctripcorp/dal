package com.ctrip.platform.dal.cluster.shard.read;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.exception.HostNotExpectedException;

import java.util.Map;
import java.util.Set;

import static com.ctrip.platform.dal.dao.DalHintEnum.routeStrategy;

public class ReadMasterStrategy extends ReadSlavesFirstStrategy {

    @Override
    public void init(Set<HostSpec> hostSpecs) {
        super.init(hostSpecs);
    }

    @Override
    public HostSpec pickRead(Map<String, Object> map) throws HostNotExpectedException {
        if (map.get(routeStrategy) != null)
            return dalHintsRoute(map);

        if ((boolean)map.get(slaveOnly) && (boolean)map.get(isPro))
            return slaveOnly();

        // if not pro: slaveOnly will act as ReadSlavesFirstStrategy
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
