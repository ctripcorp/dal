package com.ctrip.framework.dal.cluster.client.sharding.strategy;

import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;
import com.ctrip.framework.dal.cluster.client.sharding.context.DbShardContext;
import com.ctrip.framework.dal.cluster.client.sharding.context.ShardData;
import com.ctrip.framework.dal.cluster.client.sharding.context.TableShardContext;

/**
 * @author c7ch23en
 */
public abstract class ColumnShardStrategy extends BaseShardStrategy implements ShardStrategy {

    private static final String DB_SHARD_COLUMN = "dbShardColumn";
    private static final String TABLE_SHARD_COLUMN = "tableShardColumn";

    public ColumnShardStrategy() {}

    public abstract Integer calcDbShard(String tableName, Object shardValue);

    public abstract String calcTableShard(String tableName, Object shardValue);

    @Override
    public Integer getDbShard(String tableName, DbShardContext context) {
        Integer shard = context.getShardId();
        if (shard != null)
            return shard;

        Object shardValue = context.getShardValue();
        if (shardValue != null)
            return calcDbShard(tableName, shardValue);

        shardValue = getDbShardValue(tableName, context.getShardColValues());
        if (shardValue != null)
            shard = calcDbShard(tableName, shardValue);
        if (shard != null)
            return shard;

        shardValue = getDbShardValue(tableName, context.getShardData());
        if (shardValue != null)
            shard = calcDbShard(tableName, shardValue);
        return shard;
    }

    @Override
    public String getTableShard(String tableName, TableShardContext context) {
        String shard = context.getShardId();
        if (shard != null)
            return shard;

        Object shardValue = context.getShardValue();
        if (shardValue != null)
            return calcTableShard(tableName, shardValue);

        shardValue = getTableShardValue(tableName, context.getShardColValues());
        if (shardValue != null)
            shard = calcTableShard(tableName, shardValue);
        if (shard != null)
            return shard;

        shardValue = getTableShardValue(tableName, context.getShardData());
        if (shardValue != null)
            shard = calcTableShard(tableName, shardValue);
        return shard;
    }

    private Object getDbShardValue(String tableName, ShardData shardData) {
        if (shardData == null)
            return null;
        String dbShardColumn = getTableProperty(tableName, DB_SHARD_COLUMN);
        if (dbShardColumn == null)
            throw new ClusterRuntimeException(String.format("db shard column undefined for table '%s'", tableName));
        return shardData.getValue(dbShardColumn);
    }

    private Object getTableShardValue(String tableName, ShardData shardData) {
        if (shardData == null)
            return null;
        String tableShardColumn = getTableProperty(tableName, TABLE_SHARD_COLUMN);
        if (tableShardColumn == null)
            throw new ClusterRuntimeException(String.format("table shard column undefined for table '%s'", tableName));
        return shardData.getValue(tableShardColumn);
    }

}
