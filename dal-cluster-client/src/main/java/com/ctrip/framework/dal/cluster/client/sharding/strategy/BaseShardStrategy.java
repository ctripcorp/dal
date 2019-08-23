package com.ctrip.framework.dal.cluster.client.sharding.strategy;

import com.ctrip.framework.dal.cluster.client.config.ShardStrategyElement;

/**
 * @author c7ch23en
 */
public abstract class BaseShardStrategy extends ShardStrategyElement implements ShardStrategy {

    private static final String TABLE_SHARDING = "tableSharding";
    private static final String TABLE_SHARD_SEPARATOR = "tableShardSeparator";

    private static final boolean DEFAULT_TABLE_SHARDING = false;
    private static final String DEFAULT_TABLE_SHARD_SEPARATOR = "_";

    public BaseShardStrategy() {}

    @Override
    public boolean tableShardingEnabled(String tableName) {
        return getTableBooleanProperty(tableName, TABLE_SHARDING, DEFAULT_TABLE_SHARDING);
    }

    @Override
    public String getTableShardSeparator(String tableName) {
        return getTableProperty(tableName, TABLE_SHARD_SEPARATOR, DEFAULT_TABLE_SHARD_SEPARATOR);
    }

}
