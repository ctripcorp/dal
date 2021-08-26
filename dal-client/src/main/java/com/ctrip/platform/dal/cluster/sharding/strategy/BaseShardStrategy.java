package com.ctrip.platform.dal.cluster.sharding.strategy;


import com.ctrip.platform.dal.cluster.config.ShardStrategyElement;

import java.util.HashSet;
import java.util.Set;

/**
 * @author c7ch23en
 */
public abstract class BaseShardStrategy extends ShardStrategyElement implements ShardStrategy {

    protected static final String TABLE_SHARDING = "tableSharding";
    protected static final String TABLE_SHARD_SEPARATOR = "tableShardSeparator";
    protected static final String DB_SHARD_OFFSET = "dbShardOffset";
    protected static final String TABLE_SHARD_OFFSET = "tableShardOffset";

    protected static final boolean DEFAULT_TABLE_SHARDING = false;
    protected static final String DEFAULT_TABLE_SHARD_SEPARATOR = "";
    protected static final int DEFAULT_DB_SHARD_OFFSET = 0;
    protected static final int DEFAULT_TABLE_SHARD_OFFSET = 0;

    protected BaseShardStrategy() {}

    @Override
    public Set<String> getAllTableShards(String tableName) {
        Set<String> allShards = calcAllTableShards(tableName);
        Set<String> offsetShards = new HashSet<>();
        for (String shard : allShards)
            offsetShards.add(offsetTableShard(tableName, shard));
        return offsetShards;
    }

    @Override
    public boolean tableShardingEnabled(String tableName) {
        return getTableBooleanProperty(tableName, TABLE_SHARDING, DEFAULT_TABLE_SHARDING);
    }

    @Override
    public String getTableShardSeparator(String tableName) {
        return getTableProperty(tableName, TABLE_SHARD_SEPARATOR, DEFAULT_TABLE_SHARD_SEPARATOR);
    }

    protected int getDbShardOffset(String tableName) {
        return getTableIntProperty(tableName, DB_SHARD_OFFSET, DEFAULT_DB_SHARD_OFFSET);
    }

    protected int getTableShardOffset(String tableName) {
        return getTableIntProperty(tableName, TABLE_SHARD_OFFSET, DEFAULT_TABLE_SHARD_OFFSET);
    }

    protected Set<String> calcAllTableShards(String tableName) {
        throw new UnsupportedOperationException(String.format("could not calculate all table shards for table '%s'", tableName));
    }

    protected Integer offsetDbShard(String tableName, Integer shard) {
        return shard != null ? (shard + getDbShardOffset(tableName)) : null;
    }

    protected String offsetTableShard(String tableName, String shard) {
        if (shard == null)
            return null;
        int offset = getTableShardOffset(tableName);
        if (offset == 0)
            return shard;
        return String.valueOf(Integer.parseInt(shard) + offset);
    }

}
