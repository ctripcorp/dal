package com.ctrip.framework.dal.cluster.client.cluster;

public enum ReadStrategyEnum {

    READ_MASTER("ReadMasterStrategy", "com.ctrip.platform.dal.cluster.shard.read.ReadMasterStrategy"),
    READ_SLAVES_FIRST("ReadSlavesFirstStrategy", "com.ctrip.platform.dal.cluster.shard.read.ReadSlavesFirstStrategy"),
    READ_SLAVES_ONLY("ReadSlavesOnlyStrategy", "com.ctrip.platform.dal.cluster.shard.read.ReadSlavesOnlyStrategy"),
    READ_CURRENT_ZONE_SLAVES_FIRST("ReadCurrentZoneSlavesFirstStrategy", "com.ctrip.platform.dal.cluster.shard.read.ReadCurrentZoneSlavesFirstStrategy"),
    READ_CURRENT_ZONE_SLAVES_ONLY("ReadCurrentZoneSlavesOnlyStrategy", "com.ctrip.platform.dal.cluster.shard.read.ReadCurrentZoneSlavesOnlyStrategy"),
    READ_MASTER_ZONE_SLAVES_FIRST("ReadMasterZoneSlavesFirstStrategy", "com.ctrip.platform.dal.cluster.shard.read.ReadMasterZoneSlavesFirstStrategy"),
    READ_MASTER_ZONE_SLAVES_ONLY("ReadMasterZoneSlavesOnlyStrategy", "com.ctrip.platform.dal.cluster.shard.read.ReadMasterZoneSlavesOnlyStrategy");

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

    public static String parse(String name) {
        for (ReadStrategyEnum readStrategyEnum : ReadStrategyEnum.values()) {
            if (readStrategyEnum.name().equalsIgnoreCase(name))
                return readStrategyEnum.clazz;
        }
        return name;
    }


    public String getAlias() {
        return alias;
    }
}
