package com.ctrip.platform.dal.cluster.sharding.context;

/**
 * @author c7ch23en
 */
public class TableShardContext extends ShardContext {

    private String shardId;

    public TableShardContext(String logicDbName) {
        super(logicDbName);
    }

    public String getShardId() {
        return shardId;
    }

    public TableShardContext setShardId(String shardId) {
        this.shardId = shardId;
        return this;
    }

}
