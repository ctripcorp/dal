package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.ShardExecutionResult;

/**
 * @author c7ch23en
 */
public class ShardExecutionResultImpl<V> extends ExecutionResultImpl<V> implements ShardExecutionResult<V> {

    private String dbShard;
    private String tableShard;

    public ShardExecutionResultImpl(String dbShard, String tableShard, V result) {
        super(result);
        init(dbShard, tableShard);
    }

    public ShardExecutionResultImpl(String dbShard, String tableShard, Throwable errorCause) {
        super(errorCause);
        init(dbShard, tableShard);
    }

    private void init(String dbShard, String tableShard) {
        this.dbShard = dbShard;
        this.tableShard = tableShard;
    }

    @Override
    public String getDbShard() {
        return dbShard;
    }

    @Override
    public String getTableShard() {
        return tableShard;
    }

}
