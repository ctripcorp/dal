package com.ctrip.platform.dal.cluster.sharding.strategy;


import com.ctrip.platform.dal.cluster.exception.ClusterRuntimeException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class ModShardStrategy extends ColumnShardStrategy implements ShardStrategy {

    protected static final String DB_SHARD_MOD = "dbShardMod";
    protected static final String TABLE_SHARD_MOD = "tableShardMod";

    protected static final String STRING_TYPE_HANDLER = "stringTypeHandler";
    protected static final String STRING_TYPE_HANDLER_HASH = "hash";

    public ModShardStrategy() {}

    @Override
    protected Integer calcDbShard(String tableName, Object shardValue) {
        Integer mod = getDbShardMod(tableName);
        if (mod == null)
            throw new ClusterRuntimeException(String.format("db shard mod undefined for table '%s'", tableName));
        return getModResult(mod, shardValue);
    }

    @Override
    protected String calcTableShard(String tableName, Object shardValue) {
        Integer mod = getTableShardMod(tableName);
        if (mod == null)
            throw new ClusterRuntimeException(String.format("table shard mod undefined for table '%s'", tableName));
        return String.valueOf(getModResult(mod, shardValue));
    }

    @Override
    protected Set<String> calcAllTableShards(String tableName) {
        Integer mod = getTableShardMod(tableName);
        if (mod == null)
            throw new ClusterRuntimeException(String.format("table shard mod undefined for table '%s'", tableName));
        Set<String> allShards = new HashSet<>();
        for (int i = 0; i < mod; i++)
            allShards.add(String.valueOf(i));
        return allShards;
    }

    protected Integer getDbShardMod(String tableName) {
        return getTableIntProperty(tableName, DB_SHARD_MOD);
    }

    protected Integer getTableShardMod(String tableName) {
        return getTableIntProperty(tableName, TABLE_SHARD_MOD);
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
        if (value instanceof String) {
            return parseLong((String) value);
        }
        throw new ClusterRuntimeException(String.format("value [%s] cannot be parsed as a valid number", value.toString()));
    }

    protected long parseLong(String value) {
        String stringTypeHandler = getProperty(STRING_TYPE_HANDLER);
        if (STRING_TYPE_HANDLER_HASH.equalsIgnoreCase(stringTypeHandler))
            return hash(value);
        else
            return Long.parseLong(value);
    }

    protected long hash(String value) {
        return value.hashCode() & Integer.MAX_VALUE;
    }

}
