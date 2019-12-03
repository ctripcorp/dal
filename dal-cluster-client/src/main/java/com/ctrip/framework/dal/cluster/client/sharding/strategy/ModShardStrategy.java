package com.ctrip.framework.dal.cluster.client.sharding.strategy;

import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class ModShardStrategy extends ColumnShardStrategy implements ShardStrategy {

    private static final String DB_SHARD_MOD = "dbShardMod";
    private static final String TABLE_SHARD_MOD = "tableShardMod";

    public ModShardStrategy() {}

    @Override
    protected Integer calcDbShard(String tableName, Object shardValue) {
        Integer mod = getTableIntProperty(tableName, DB_SHARD_MOD);
        if (mod == null)
            throw new ClusterRuntimeException(String.format("db shard mod undefined for table '%s'", tableName));
        return getModResult(mod, shardValue);
    }

    @Override
    protected String calcTableShard(String tableName, Object shardValue) {
        Integer mod = getTableIntProperty(tableName, TABLE_SHARD_MOD);
        if (mod == null)
            throw new ClusterRuntimeException(String.format("table shard mod undefined for table '%s'", tableName));
        return String.valueOf(getModResult(mod, shardValue));
    }

    @Override
    protected Set<String> calcAllTableShards(String tableName) {
        Integer mod = getTableIntProperty(tableName, TABLE_SHARD_MOD);
        if (mod == null)
            throw new ClusterRuntimeException(String.format("table shard mod undefined for table '%s'", tableName));
        Set<String> allShards = new HashSet<>();
        for (int i = 0; i < mod; i++)
            allShards.add(String.valueOf(i));
        return allShards;
    }

    protected int getModResult(int mod, Object shardValue) {
        long longValue = getLongValue(shardValue);
        return (int) (longValue % mod);
    }

    protected Long getLongValue(Object value) {
        if (value instanceof Long)
            return (Long) value;
        if (value instanceof Number)
            return ((Number) value).longValue();
        if (value instanceof String)
            return Long.parseLong((String) value);
        throw new ClusterRuntimeException(String.format("value [%s] cannot be parsed as a valid number", value.toString()));
    }

}
