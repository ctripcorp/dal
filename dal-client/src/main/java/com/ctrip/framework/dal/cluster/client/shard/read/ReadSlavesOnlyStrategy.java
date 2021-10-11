package com.ctrip.framework.dal.cluster.client.shard.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.base.NullHostSpec;
import com.ctrip.framework.dal.cluster.client.cluster.RouteStrategyEnum;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;
import com.ctrip.platform.dal.dao.DalHints;

import java.util.Map;
import java.util.Set;

import static com.ctrip.framework.dal.cluster.client.cluster.RouteStrategyEnum.READ_SLAVES_FIRST;
import static com.ctrip.framework.dal.cluster.client.cluster.RouteStrategyEnum.READ_SLAVES_ONLY;
import static com.ctrip.platform.dal.dao.DalHintEnum.routeStrategy;

public class ReadSlavesOnlyStrategy extends ReadSlavesFirstStrategy {
    protected RouteStrategyEnum readStrategyEnum = READ_SLAVES_ONLY;

    @Override
    public HostSpec pickRead(DalHints dalHints) throws HostNotExpectedException {
        HostSpec hostSpec = hintsRoute(dalHints);
        if (!(hostSpec instanceof NullHostSpec))
            return hostSpec;

        return slaveOnly();
    }
}
