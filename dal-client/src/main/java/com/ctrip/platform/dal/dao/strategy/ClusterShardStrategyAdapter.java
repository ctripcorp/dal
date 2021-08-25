package com.ctrip.platform.dal.dao.strategy;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;
import com.ctrip.framework.dal.cluster.client.sharding.context.*;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.helper.RequestContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class ClusterShardStrategyAdapter implements DalShardingStrategy {

    private Cluster cluster;

    public ClusterShardStrategyAdapter(Cluster cluster) {
        this.cluster = cluster;
    }

    public boolean isShardingByTable(String tableName) {
        return cluster.tableShardingEnabled(tableName);
    }

    public String getTableShardSeparator(String tableName) {
        return cluster.getTableShardSeparator(tableName);
    }

    @Override
    public boolean isShardingByDb() {
        return cluster.dbShardingEnabled();
    }

    @Override
    public String locateDbShard(DalConfigure configure, String logicDbName, DalHints hints) {
        String tableName = null;
        RequestContext reqCtx = hints.getRequestContext();
        if (reqCtx != null)
            tableName = reqCtx.getLogicTableName();
        DbShardContext ctx = createDbShardContext(logicDbName, hints);
        Integer shard = cluster.getDbShard(tableName, ctx);
        return shard != null ? String.valueOf(shard) : null;
    }

    @Override
    public String locateTableShard(DalConfigure configure, String logicDbName, String tableName, DalHints hints) {
        TableShardContext ctx = createTableShardContext(logicDbName, hints);
        return cluster.getTableShard(tableName, ctx);
    }

    @Override
    public void initialize(Map<String, String> settings) {}

    @Override
    public boolean isMaster(DalConfigure configure, String logicDbName, DalHints hints) {
        return false;
    }

    @Override
    public boolean isShardingByTable() {
        throw new UnsupportedOperationException("table name undefined");
    }

    @Override
    public boolean isShardingEnable(String tableName) {
        return isShardingByTable(tableName);
    }

    @Override
    public String getTableShardSeparator() {
        throw new UnsupportedOperationException("table name undefined");
    }

    private DbShardContext createDbShardContext(String logicDbName, DalHints hints) {
        DbShardContext ctx = new DbShardContext(logicDbName);
        String shardId = hints.getShardId();
        if (shardId != null)
            ctx.setShardId(Integer.parseInt(shardId));
        ctx.setShardValue(hints.get(DalHintEnum.shardValue));
        applyCommonHints(ctx, hints);
        return ctx;
    }

    private TableShardContext createTableShardContext(String logicDbName, DalHints hints) {
        TableShardContext ctx = new TableShardContext(logicDbName);
        ctx.setShardId(hints.getTableShardId());
        ctx.setShardValue(hints.get(DalHintEnum.tableShardValue));
        applyCommonHints(ctx, hints);
        return ctx;
    }

    private void applyCommonHints(ShardContext ctx, DalHints hints) {
        Map<String, Object> shardColValues = (Map<String, Object>) hints.get(DalHintEnum.shardColValues);
        if (shardColValues != null)
            ctx.setShardColValues(new MappedShardData(shardColValues));
        ShardData parameters = (ShardData) hints.get(DalHintEnum.parameters);
        Map<String, Object> fields = (Map<String, Object>) hints.get(DalHintEnum.fields);
//        if (parameters != null && fields != null)
//            throw new ClusterRuntimeException("parameters and fields cannot be set at the same time");
        if (parameters != null)
            ctx.addShardData(parameters);
        if (fields != null)
            ctx.addShardData(new MappedShardData(fields));
        Map<String, Object> userDefined = new HashMap<>();
        if (hints.get(DalHintEnum.userDefined1) != null)
            userDefined.put(DalHintEnum.userDefined1.name(), hints.get(DalHintEnum.userDefined1));
        if (hints.get(DalHintEnum.userDefined2) != null)
            userDefined.put(DalHintEnum.userDefined2.name(), hints.get(DalHintEnum.userDefined2));
        if (hints.get(DalHintEnum.userDefined3) != null)
            userDefined.put(DalHintEnum.userDefined3.name(), hints.get(DalHintEnum.userDefined3));
        if (userDefined.size() != 0)
            ctx.addShardData(new MappedShardData(userDefined));
    }

}
