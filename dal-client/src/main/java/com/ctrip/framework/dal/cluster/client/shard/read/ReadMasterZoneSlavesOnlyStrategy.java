package com.ctrip.framework.dal.cluster.client.shard.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.cluster.RouteStrategyEnum;
import com.ctrip.framework.dal.cluster.client.exception.DalMetadataException;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.DalHints;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.ctrip.framework.dal.cluster.client.cluster.RouteStrategyEnum.READ_MASTER_ZONE_SLAVES_ONLY;

public class ReadMasterZoneSlavesOnlyStrategy extends ReadSlavesFirstStrategy {
    protected RouteStrategyEnum readStrategyEnum = READ_MASTER_ZONE_SLAVES_ONLY;
    protected Map<String, List<HostSpec>> zoneToHost = new HashMap<>();
    protected String masterZone;


    private final static String HOST_SPEC_ERROR = " of %s zone msg lost";
    private final static String ZONE_MSG_LOST = "'s master zone msg lost";
    private final static String NO_DATABASE_IN_MASTER_ZONE = " has no database in %s";

    @Override
    public void init(Set<HostSpec> hostSpecs, CaseInsensitiveProperties strategyProperties) {
        super.init(hostSpecs, strategyProperties);
        for (HostSpec hostSpec : hostSpecs) {
            if (hostSpec.isMaster()){
                masterZone = hostSpec.getTrimLowerCaseZone();
                continue;
            }

            if (!zoneToHost.containsKey(hostSpec.getTrimLowerCaseZone())) {
                List<HostSpec> hosts = new ArrayList<>();
                hosts.add(hostSpec);
                zoneToHost.put(hostSpec.getTrimLowerCaseZone(), hosts);
            } else {
                zoneToHost.get(hostSpec.getTrimLowerCaseZone()).add(hostSpec);
            }
        }

        if (StringUtils.isTrimmedEmpty(masterZone))
            throw new DalMetadataException(ZONE_MSG_LOST);
    }

    @Override
    public HostSpec pickRead(DalHints dalHints) throws HostNotExpectedException {
        HostSpec hostSpec = hintsRoute(dalHints);
        if (hostSpec != null)
            return hostSpec;

        return pickMasterZoneSlaveOnly();
    }

    protected HostSpec pickMasterZoneSlaveOnly() {
        List<HostSpec> masterZoneHost = zoneToHost.get(masterZone);
        if (CollectionUtils.isEmpty(masterZoneHost))
            throw new DalMetadataException(String.format(NO_DATABASE_IN_MASTER_ZONE, masterZone));

        return choseByRandom(masterZoneHost);
    }
}
