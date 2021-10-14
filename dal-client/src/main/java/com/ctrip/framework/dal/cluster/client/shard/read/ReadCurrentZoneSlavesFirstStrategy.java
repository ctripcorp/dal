package com.ctrip.framework.dal.cluster.client.shard.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.cluster.RouteStrategyEnum;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.DalHints;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

import static com.ctrip.framework.dal.cluster.client.cluster.RouteStrategyEnum.READ_CURRENT_ZONE_SLAVES_FIRST;

public class ReadCurrentZoneSlavesFirstStrategy extends ReadCurrentZoneSlavesOnlyStrategy {

    @Override
    public void init(Set<HostSpec> hostSpecs, CaseInsensitiveProperties strategyProperties) {
        super.init(hostSpecs, strategyProperties);
    }

    @Override
    public HostSpec pickRead(DalHints dalHints) throws HostNotExpectedException {
        HostSpec hostSpec = hintsRoute(dalHints);
        if (hostSpec != null)
            return hostSpec;

        hostSpec = pickCurrentZoneSlave();
        return hostSpec == null ? pickSlaveFirst() : hostSpec;
    }

    protected HostSpec pickCurrentZoneSlave() {
        List<HostSpec> currentZoneHost = zoneToHost.get(currentZone.trim());
        if (CollectionUtils.isEmpty(currentZoneHost))
            return null;

        return choseByRandom(currentZoneHost);
    }

    @Override
    public void dispose() {

    }
}
