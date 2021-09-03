package com.ctrip.framework.dal.cluster.client.cluster;

public enum RouteStrategyEnum {

    READ_MASTER("ReadMasterStrategy", "com.ctrip.platform.dal.cluster.shard.read.ReadMasterStrategy"),
    READ_SLAVES_FIRST("ReadSlavesFirstStrategy", "com.ctrip.platform.dal.cluster.shard.read.ReadSlavesFirstStrategy"),
    READ_SLAVES_ONLY("ReadSlavesOnlyStrategy", "com.ctrip.platform.dal.cluster.shard.read.ReadSlavesOnlyStrategy"),
    READ_CURRENT_ZONE_SLAVES_FIRST("ReadCurrentZoneSlavesFirstStrategy", "com.ctrip.platform.dal.cluster.shard.read.ReadCurrentZoneSlavesFirstStrategy"),
    READ_CURRENT_ZONE_SLAVES_ONLY("ReadCurrentZoneSlavesOnlyStrategy", "com.ctrip.platform.dal.cluster.shard.read.ReadCurrentZoneSlavesOnlyStrategy"),
    READ_MASTER_ZONE_SLAVES_FIRST("ReadMasterZoneSlavesFirstStrategy", "com.ctrip.platform.dal.cluster.shard.read.ReadMasterZoneSlavesFirstStrategy"),
    READ_MASTER_ZONE_SLAVES_ONLY("ReadMasterZoneSlavesOnlyStrategy", "com.ctrip.platform.dal.cluster.shard.read.ReadMasterZoneSlavesOnlyStrategy"),

    WRITE_ORDERED("OrderedAccessStrategy", "com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.mgr.MGRStrategy"),
    WRITE_CURRENT_ZONE_FIRST("LocalizedAccessStrategy", "com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob.OBStrategy");

    private String alias;
    private String clazz;

    RouteStrategyEnum(String alias, String clazz) {
        this.alias = alias;
        this.clazz = clazz;
    }

    public static String parse(String strategyName) {
        for (RouteStrategyEnum routeStrategyEnum : RouteStrategyEnum.values()) {
            if (routeStrategyEnum.name().equalsIgnoreCase(strategyName) || routeStrategyEnum.getAlias().equalsIgnoreCase(strategyName))
                return routeStrategyEnum.clazz;
        }
        return strategyName;
    }

    public String getClazz() {
        return this.clazz;
    }

    public String getAlias() {
        return alias;
    }
}
