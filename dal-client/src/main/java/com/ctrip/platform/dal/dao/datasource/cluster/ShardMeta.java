package com.ctrip.platform.dal.dao.datasource.cluster;


import com.ctrip.platform.dal.cluster.base.HostSpec;

import java.util.Set;

/**
 * @author c7ch23en
 */
public interface ShardMeta extends ClusterMeta {

    int shardIndex();

    Set<HostSpec> configuredHosts();

}
