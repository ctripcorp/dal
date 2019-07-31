package com.ctrip.framework.dal.cluster.client.sharding.context;

/**
 * @author c7ch23en
 */
public abstract class ShardContext {

    private String logicDbName;
    private Object shardValue;
    private ShardData shardColValues;
    private ShardData shardData;

    public ShardContext(String logicDbName) {
        this.logicDbName = logicDbName;
    }

    public String getLogicDbName() {
        return logicDbName;
    }

    public Object getShardValue() {
        return shardValue;
    }

    public ShardData getShardColValues() {
        return shardColValues;
    }

    public ShardData getShardData() {
        return shardData;
    }

    public ShardContext setShardValue(Object shardValue) {
        this.shardValue = shardValue;
        return this;
    }

    public ShardContext setShardColValues(ShardData shardColValues) {
        this.shardColValues = shardColValues;
        return this;
    }

    public ShardContext setShardData(ShardData shardData) {
        this.shardData = shardData;
        return this;
    }

}
