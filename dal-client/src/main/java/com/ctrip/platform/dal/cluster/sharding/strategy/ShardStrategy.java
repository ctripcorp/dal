package com.ctrip.platform.dal.cluster.sharding.strategy;


import com.ctrip.platform.dal.cluster.sharding.context.DbShardContext;
import com.ctrip.platform.dal.cluster.sharding.context.TableShardContext;

import java.util.Set;

/**
 * @author c7ch23en
 */
public interface ShardStrategy {

    Integer getDbShard(String tableName, DbShardContext context);

    boolean tableShardingEnabled(String tableName);

    String getTableShard(String tableName, TableShardContext context);

    Set<String> getAllTableShards(String tableName);

    String getTableShardSeparator(String tableName);

    Set<String> getAppliedTables();

}
