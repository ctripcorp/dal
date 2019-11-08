package com.ctrip.framework.db.cluster.entity;

/**
 * Created by taochen on 2019/11/6.
 */
public class Shard {
    private int shardIndex;

    private String dbName;

    private ShardInstance master;

    private ShardInstance slave;

    private ShardInstance read;

    public int getShardIndex() {
        return shardIndex;
    }

    public void setShardIndex(int shardIndex) {
        this.shardIndex = shardIndex;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public ShardInstance getMaster() {
        return master;
    }

    public void setMaster(ShardInstance master) {
        this.master = master;
    }

    public ShardInstance getSlave() {
        return slave;
    }

    public void setSlave(ShardInstance slave) {
        this.slave = slave;
    }

    public ShardInstance getRead() {
        return read;
    }

    public void setRead(ShardInstance read) {
        this.read = read;
    }
}
