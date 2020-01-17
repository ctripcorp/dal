package com.ctrip.framework.dal.cluster.client.sharding.strategy;

import com.ctrip.framework.dal.cluster.client.config.ShardStrategyElement;
import com.ctrip.framework.dal.cluster.client.sharding.context.DbShardContext;
import com.ctrip.framework.dal.cluster.client.sharding.context.TableShardContext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author c7ch23en
 */
public abstract class BaseShardStrategy extends ShardStrategyElement implements ShardStrategy {

    private static final String TABLE_SHARDING = "tableSharding";
    private static final String TABLE_SHARD_SEPARATOR = "tableShardSeparator";
    private static final String DB_SHARD_OFFSET = "dbShardOffset";
    private static final String TABLE_SHARD_OFFSET = "tableShardOffset";

    private static final boolean DEFAULT_TABLE_SHARDING = false;
    private static final String DEFAULT_TABLE_SHARD_SEPARATOR = "";
    private static final int DEFAULT_DB_SHARD_OFFSET = 0;
    private static final int DEFAULT_TABLE_SHARD_OFFSET = 0;

    protected BaseShardStrategy() {}

    protected abstract Integer calcDbShard(String tableName, DbShardContext context);

    protected abstract String calcTableShard(String tableName, TableShardContext context);

    @Override
    public Integer getDbShard(String tableName, DbShardContext context) {
        Integer shard = calcDbShard(tableName, context);
        return shard != null ? (shard + getDbShardOffset(tableName)) : null;
    }

    @Override
    public String getTableShard(String tableName, TableShardContext context) {
        String shard = calcTableShard(tableName, context);
        return shard != null ? offsetTableShard(tableName, shard) : null;
    }

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
        throw new UnsupportedOperationException(String.format("Could not calculate all table shards for table '%s'", tableName));
    }

    private String offsetTableShard(String tableName, String shard) {
        int offset = getTableShardOffset(tableName);
        if (offset == 0)
            return shard;
        return String.valueOf(Integer.parseInt(shard) + offset);
    }

}
