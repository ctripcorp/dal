package com.ctrip.framework.dal.cluster.client.shard.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.cluster.RouteStrategyEnum;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;
import com.ctrip.platform.dal.dao.DalHints;


import static com.ctrip.framework.dal.cluster.client.cluster.RouteStrategyEnum.READ_MASTER;

public class ReadMasterStrategy extends ReadSlavesFirstStrategy {

    @Override
    public HostSpec pickRead(DalHints dalHints) throws HostNotExpectedException {
        HostSpec hostSpec = hintsRoute(dalHints);
        if (hostSpec != null)
            return hostSpec;

        return pickMaster();
    }
}
