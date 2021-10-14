package com.ctrip.framework.dal.cluster.client.shard.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.cluster.RouteStrategyEnum;
import com.ctrip.framework.dal.cluster.client.exception.DalMetadataException;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.ctrip.framework.dal.cluster.client.cluster.RouteStrategyEnum.READ_CURRENT_ZONE_SLAVES_ONLY;

public class ReadCurrentZoneSlavesOnlyStrategy extends ReadSlavesFirstStrategy {
    protected String currentZone = envUtils.getZone();
    protected Map<String, List<HostSpec>> zoneToHost = new HashMap<>();

    private final static String ZONE_MSG_LOST = " can't get zone msg";
    private final static String NO_DATABASE_IN_CURRENT_ZONE = " has no database in %s";

    @Override
    public void init(Set<HostSpec> hostSpecs, CaseInsensitiveProperties strategyProperties) {
        super.init(hostSpecs, strategyProperties);
        for (HostSpec hostSpec : hostSpecs) {
            if (!zoneToHost.containsKey(hostSpec.getTrimLowerCaseZone())) {
                List<HostSpec> hosts = new ArrayList<>();
                hosts.add(hostSpec);
                zoneToHost.put(hostSpec.getTrimLowerCaseZone(), hosts);
            } else {
                zoneToHost.get(hostSpec.getTrimLowerCaseZone()).add(hostSpec);
            }
        }

        if (StringUtils.isTrimmedEmpty(currentZone))
            throw new DalMetadataException(ZONE_MSG_LOST);
    }

    @Override
    public HostSpec pickRead(DalHints dalHints) throws HostNotExpectedException {
        HostSpec hostSpec = hintsRoute(dalHints);
        if (hostSpec != null)
            return hostSpec;

        return pickCurrentZoneSlaveOnly();
    }

    protected HostSpec pickCurrentZoneSlaveOnly() {
        List<HostSpec> currentZoneHost = zoneToHost.get(currentZone.trim());
        if (CollectionUtils.isEmpty(currentZoneHost))
            throw new DalMetadataException(String.format(NO_DATABASE_IN_CURRENT_ZONE, currentZone));
        return choseByRandom(currentZoneHost);
    }
}
