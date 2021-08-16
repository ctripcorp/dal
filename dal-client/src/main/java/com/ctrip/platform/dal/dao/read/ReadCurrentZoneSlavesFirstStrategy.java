package com.ctrip.platform.dal.dao.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;

import java.util.*;

public class ReadCurrentZoneSlavesFirstStrategy extends ReadCurrentZoneSlavesOnlyStrategy {

    @Override
    public void init(Set<HostSpec> hostSpecs) {
        super.init(hostSpecs);
    }

    @Override
    public HostSpec pickRead(Map<String, Object> map) throws HostNotExpectedException {
        if (map.containsKey(routeStrategy))
            return dalHintsRoute(map);

        if ((boolean)map.get(slaveOnly) && (boolean)map.get(isPro))
            return slaveOnly();

        return null;
    }

    @Override
    protected HostSpec slaveOnly() {

        return super.slaveOnly();
    }

    private HostSpec pickCurrentZone() {
        if (zoneToHost.containsKey(currentZone) && zoneToHost.get(currentZone).size() > 0) {
            return choseByRandom(zoneToHost.get(currentZone));
        }

        return null;
    }

    @Override
    public void onChange(Set<HostSpec> hostSpecs) {

    }

    @Override
    public void dispose() {

    }
}
