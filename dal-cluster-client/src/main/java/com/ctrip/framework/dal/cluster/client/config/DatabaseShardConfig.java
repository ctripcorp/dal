package com.ctrip.framework.dal.cluster.client.config;

import com.ctrip.framework.dal.cluster.client.shard.DatabaseShard;

/**
 * @author c7ch23en
 */
public interface DatabaseShardConfig {

    DatabaseShard generateDatabaseShard();

}
