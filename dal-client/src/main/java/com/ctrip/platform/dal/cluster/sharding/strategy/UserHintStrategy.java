package com.ctrip.platform.dal.cluster.sharding.strategy;


import com.ctrip.platform.dal.cluster.sharding.context.DbShardContext;
import com.ctrip.platform.dal.cluster.sharding.context.TableShardContext;

public class UserHintStrategy extends BaseShardStrategy implements ShardStrategy {

    public UserHintStrategy() {}

    @Override
    public Integer getDbShard(String tableName, DbShardContext context) {
        return context.getShardId();
    }

    @Override
    public String getTableShard(String tableName, TableShardContext context) {
        return context.getShardId();
    }

}
