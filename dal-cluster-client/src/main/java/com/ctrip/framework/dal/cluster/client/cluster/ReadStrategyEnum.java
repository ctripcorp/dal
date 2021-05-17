package com.ctrip.framework.dal.cluster.client.cluster;

public enum ReadStrategyEnum {

    READ_MASTER("ReadMasterStrategy", "com.ctrip.framework.dal.cluster.client.shard.read.ReadMasterStrategy"),
    READ_SLAVES_FIRST("ReadSlavesFirstStrategy", "com.ctrip.framework.dal.cluster.client.shard.read.ReadSlavesFirstStrategy"),
    READ_SLAVES_ONLY("ReadSlavesOnlyStrategy", "com.ctrip.framework.dal.cluster.client.shard.read.ReadSlavesOnlyStrategy"),
    READ_CURRENT_ZONE_SLAVES_FIRST("ReadCurrentZoneSlavesFirstStrategy", "com.ctrip.framework.dal.cluster.client.shard.read.ReadCurrentZoneSlavesFirstStrategy"),
    READ_CURRENT_ZONE_SLAVES_ONLY("ReadCurrentZoneSlavesOnlyStrategy", "com.ctrip.framework.dal.cluster.client.shard.read.ReadCurrentZoneSlavesOnlyStrategy"),
    READ_MASTER_ZONE_SLAVES_FIRST("ReadMasterZoneSlavesFirstStrategy", "com.ctrip.framework.dal.cluster.client.shard.read.ReadMasterZoneSlavesFirstStrategy"),
    READ_MASTER_ZONE_SLAVES_ONLY("ReadMasterZoneSlavesOnlyStrategy", "com.ctrip.framework.dal.cluster.client.shard.read.ReadMasterZoneSlavesOnlyStrategy");

    private String alias;
    private String clazz;

    ReadStrategyEnum(String alias, String clazz) {
        this.alias = alias;
        this.clazz = clazz;
    }

    public static String getStrategyName(String alias) {
        for (ReadStrategyEnum readStrategyEnum : ReadStrategyEnum.values()) {
            if (readStrategyEnum.alias.equalsIgnoreCase(alias))
                return readStrategyEnum.clazz;
        }
        return alias;
    }


    public String getAlias() {
        return alias;
    }
}
