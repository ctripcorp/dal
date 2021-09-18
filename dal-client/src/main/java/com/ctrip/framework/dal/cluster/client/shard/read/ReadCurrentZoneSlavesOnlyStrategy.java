package com.ctrip.framework.dal.cluster.client.shard.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.exception.DalMetadataException;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.DalHints;

import java.util.*;

public class ReadCurrentZoneSlavesOnlyStrategy extends ReadSlavesFirstStrategy {
    protected String currentZone;
    protected Map<String, List<HostSpec>> zoneToHost = new HashMap<>();

    private final String HOST_SPEC_ERROR = " of %s zone msg lost";

    @Override
    public void init(Set<HostSpec> hostSpecs, CaseInsensitiveProperties strategyProperties) {
        super.init(hostSpecs, strategyProperties);
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
    public HostSpec pickRead(DalHints dalHints) throws HostNotExpectedException {
        if (dalHints.getRouteStrategy() != null)
            return dalHintsRoute(dalHints);

        return null;
    }

    @Override
    public void dispose() {

    }
}
