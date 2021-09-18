package com.ctrip.framework.dal.cluster.client.sharding.context;

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
