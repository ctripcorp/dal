package com.ctrip.framework.dal.cluster.client.sharding.strategy;

import com.ctrip.framework.dal.cluster.client.sharding.context.DbShardContext;
import com.ctrip.framework.dal.cluster.client.sharding.context.TableShardContext;

public class UserHintStrategy extends BaseShardStrategy implements ShardStrategy {

    public UserHintStrategy() {}

    @Override
    protected Integer calcDbShard(String tableName, DbShardContext context) {
        return context.getShardId();
    }

    @Override
    protected String calcTableShard(String tableName, TableShardContext context) {
        return context.getShardId();
    }

}
