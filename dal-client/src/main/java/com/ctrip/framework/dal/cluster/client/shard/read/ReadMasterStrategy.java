package com.ctrip.framework.dal.cluster.client.shard.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;

import java.util.Map;
import java.util.Set;

import static com.ctrip.platform.dal.dao.DalHintEnum.routeStrategy;

public class ReadMasterStrategy extends ReadSlavesFirstStrategy {

    @Override
    public void init(Set<HostSpec> hostSpecs, CaseInsensitiveProperties strategyProperties) {
        super.init(hostSpecs, strategyProperties);
    }

    @Override
    public HostSpec pickRead(DalHints dalHints) throws HostNotExpectedException {
        if (dalHints.getRouteStrategy() != null)
            return dalHintsRoute(dalHints);

        if (dalHints.is(DalHintEnum.slaveOnly) && envUtils.isProd())
            return slaveOnly();

        // if not pro: slaveOnly will act as ReadSlavesFirstStrategy
        if (dalHints.is(DalHintEnum.slaveOnly))
            return super.pickRead(dalHints);

        return pickMaster();
    }

    @Override
    public void dispose() {

    }
}
