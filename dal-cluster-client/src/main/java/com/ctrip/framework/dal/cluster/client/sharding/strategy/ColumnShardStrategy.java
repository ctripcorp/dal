package com.ctrip.framework.dal.cluster.client.sharding.strategy;

import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;
import com.ctrip.framework.dal.cluster.client.sharding.context.DbShardContext;
import com.ctrip.framework.dal.cluster.client.sharding.context.ShardData;
import com.ctrip.framework.dal.cluster.client.sharding.context.TableShardContext;

import java.util.List;

/**
 * @author c7ch23en
 */
public abstract class ColumnShardStrategy extends BaseShardStrategy implements ShardStrategy {

    protected static final String DB_SHARD_COLUMN = "dbShardColumn";
    protected static final String TABLE_SHARD_COLUMN = "tableShardColumn";

    // compatible with strategy of dal 1.x
    protected static final String DB_SHARD_COLUMN_CANDIDATES = "dbShardColumnCandidates";
    protected static final String COMMA_SPLITTER = ",";

    protected ColumnShardStrategy() {}

    protected abstract Integer calcDbShard(String tableName, Object shardValue);

    protected abstract String calcTableShard(String tableName, Object shardValue);

    @Override
    public Integer getDbShard(String tableName, DbShardContext context) {
        Integer shard = context.getShardId();
        if (shard != null)
            return shard;

        Object shardValue = context.getShardValue();
        if (shardValue != null)
            shard = calcDbShard(tableName, shardValue);
        if (shard != null)
            return offsetDbShard(tableName, shard);

        shardValue = getDbShardValue(tableName, context.getShardColValues());
        if (shardValue != null)
            shard = calcDbShard(tableName, shardValue);
        if (shard != null)
            return offsetDbShard(tableName, shard);

        for (ShardData shardData : context.getShardDataCandidates()) {
            shardValue = getDbShardValue(tableName, shardData);
            if (shardValue != null) {
                shard = calcDbShard(tableName, shardValue);
                if (shard != null)
                    return offsetDbShard(tableName, shard);
            }
        }

        return shard;
    }

    @Override
    public String getTableShard(String tableName, TableShardContext context) {
        String shard = context.getShardId();
        if (shard != null)
            return shard;

        Object shardValue = context.getShardValue();
        if (shardValue != null)
            shard = calcTableShard(tableName, shardValue);
        if (shard != null)
            return offsetTableShard(tableName, shard);

        shardValue = getTableShardValue(tableName, context.getShardColValues());
        if (shardValue != null)
            shard = calcTableShard(tableName, shardValue);
        if (shard != null)
            return offsetTableShard(tableName, shard);

        for (ShardData shardData : context.getShardDataCandidates()) {
            shardValue = getTableShardValue(tableName, shardData);
            if (shardValue != null) {
                shard = calcTableShard(tableName, shardValue);
                if (shard != null)
                    return offsetTableShard(tableName, shard);
            }
        }

        return null;
    }

    protected Object getDbShardValue(String tableName, ShardData shardData) {
        if (shardData == null)
            return null;
        String dbShardColumn = getDbShardColumn(tableName);
        if (tableName == null)
            return getDbShardValueForNullTable(shardData, dbShardColumn, getDbShardColumnCandidates());
        else {
            if (dbShardColumn == null)
                throw new ClusterRuntimeException(String.format("db shard column undefined for table '%s'", tableName));
            return shardData.getValue(dbShardColumn);
        }
    }

    // compatible with strategy of dal 1.x
    protected Object getDbShardValueForNullTable(ShardData shardData,
                                                 String dbShardColumn, String[] dbShardColumnCandidates) {
        if (shardData == null)
            return null;
        if (dbShardColumn != null) {
            Object value = shardData.getValue(dbShardColumn);
            if (value != null)
                return value;
        }
        if (dbShardColumnCandidates != null)
            for (String column : dbShardColumnCandidates) {
                Object value = shardData.getValue(column);
                if (value != null)
                    return value;
            }
        return null;
    }

    protected Object getTableShardValue(String tableName, ShardData shardData) {
        if (shardData == null)
            return null;
        String tableShardColumn = getTableShardColumn(tableName);
        if (tableShardColumn == null)
            throw new ClusterRuntimeException(String.format("table shard column undefined for table '%s'", tableName));
        return shardData.getValue(tableShardColumn);
    }

    protected String getDbShardColumn(String tableName) {
        return getTableProperty(tableName, DB_SHARD_COLUMN);
    }

    // compatible with strategy of dal 1.x
    protected String[] getDbShardColumnCandidates() {
        String value = getProperty(DB_SHARD_COLUMN_CANDIDATES);
        return value != null ? value.split(COMMA_SPLITTER) : null;
    }

    protected String getTableShardColumn(String tableName) {
        return getTableProperty(tableName, TABLE_SHARD_COLUMN);
    }

}
