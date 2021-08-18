package com.ctrip.framework.dal.cluster.client.sharding.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.framework.dal.cluster.client.exception.DalMetadataException;

import java.util.*;

public class ReadCurrentZoneSlavesOnlyStrategy extends ReadSlavesFirstStrategy {
    protected String currentZone;
    protected Map<String, List<HostSpec>> zoneToHost = new HashMap<>();

    private final String HOST_SPEC_ERROR = " of %s zone msg lost";

    @Override
    public void init(Set<HostSpec> hostSpecs) {
        super.init(hostSpecs);
        for (HostSpec hostSpec : hostSpecs) {
            if (StringUtils.isTrimmedEmpty(hostSpec.zone()))
                throw new DalMetadataException(String.format(HOST_SPEC_ERROR, hostSpec.toString()));
            if (!zoneToHost.containsKey(hostSpec.zone())) {
                List<HostSpec> hosts = new ArrayList<>();
                hosts.add(hostSpec);
                zoneToHost.put(hostSpec.zone(), hosts);
            } else {
                zoneToHost.get(hostSpec.zone()).add(hostSpec);
            }
        }
    }

    @Override
    public HostSpec pickRead(Map<String, Object> map) throws HostNotExpectedException {
        if (map.get(routeStrategy) != null)
            return dalHintsRoute(map);

        return null;
    }

    @Override
    public void onChange(Set<HostSpec> hostSpecs) {

    }

    @Override
    public void dispose() {

    }
}
