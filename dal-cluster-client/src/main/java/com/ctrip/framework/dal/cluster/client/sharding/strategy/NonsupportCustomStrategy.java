package com.ctrip.framework.dal.cluster.client.sharding.strategy;

import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;

public class NonsupportCustomStrategy extends ColumnShardStrategy {
    @Override
    protected Integer calcDbShard(String tableName, Object shardValue) {
        throw new ClusterRuntimeException("Not support custom strategy.");
    }

    @Override
    protected String calcTableShard(String tableName, Object shardValue) {
        throw new ClusterRuntimeException("Not support custom strategy.");
    }
}
