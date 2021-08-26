package com.ctrip.platform.dal.cluster.sharding.context;

import java.util.LinkedList;
import java.util.List;

/**
 * @author c7ch23en
 */
public abstract class ShardContext {

    private String logicDbName;
    private Object shardValue;
    private ShardData shardColValues;
    private List<ShardData> shardDataCandidates = new LinkedList<>();

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

    public List<ShardData> getShardDataCandidates() {
        return new LinkedList<>(shardDataCandidates);
    }

    public ShardContext setShardValue(Object shardValue) {
        this.shardValue = shardValue;
        return this;
    }

    public ShardContext setShardColValues(ShardData shardColValues) {
        this.shardColValues = shardColValues;
        return this;
    }

    public ShardContext addShardData(ShardData shardData) {
        this.shardDataCandidates.add(shardData);
        return this;
    }

}
