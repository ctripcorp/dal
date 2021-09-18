package com.ctrip.framework.dal.cluster.client.sharding.strategy;


import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;

public class NonsupportCustomStrategy extends ColumnShardStrategy {

    private String className;

    public NonsupportCustomStrategy(String className) {
        this.className = className;
    }

    public NonsupportCustomStrategy() {
        this("");
    }

    @Override
    protected Integer calcDbShard(String tableName, Object shardValue) {
        throw new ClusterRuntimeException("Not support custom strategy.");
    }

    @Override
    protected String calcTableShard(String tableName, Object shardValue) {
        throw new ClusterRuntimeException("Not support custom strategy.");
    }

    public String getClassName() {
        return className;
    }
}
