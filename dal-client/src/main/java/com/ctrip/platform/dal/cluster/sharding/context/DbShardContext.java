package com.ctrip.platform.dal.cluster.sharding.context;

/**
 * @author c7ch23en
 */
public class DbShardContext extends ShardContext {

    private Integer shardId;

    public DbShardContext(String logicDbName) {
        super(logicDbName);
    }

    public Integer getShardId() {
        return shardId;
    }

    public DbShardContext setShardId(Integer shardId) {
        this.shardId = shardId;
        return this;
    }

}
