package com.ctrip.platform.dal.cluster.shard;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.database.Database;
import com.ctrip.platform.dal.cluster.shard.read.RouteStrategy;

import java.util.List;

/**
 * @author c7ch23en
 */
public interface DatabaseShard {

    int getShardIndex();

    List<Database> getMasters();

    List<Database> getSlaves();

    RouteStrategy getRouteStrategy();

    Database parseFromHostSpec(HostSpec hostSpec);

}
