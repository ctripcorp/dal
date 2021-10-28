package com.ctrip.framework.dal.cluster.client.cluster;

import com.ctrip.framework.dal.cluster.client.shard.read.*;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.mgr.MGRStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob.OBStrategy;

public enum RouteStrategyEnum {

    READ_MASTER("ReadMasterStrategy", ReadMasterStrategy.class.getName()),
    READ_SLAVES_FIRST("ReadSlavesFirstStrategy", ReadSlavesFirstStrategy.class.getName()),
    READ_SLAVES_ONLY("ReadSlavesOnlyStrategy", ReadSlavesOnlyStrategy.class.getName()),
    READ_CURRENT_ZONE_SLAVES_FIRST("ReadCurrentZoneSlavesFirstStrategy", ReadCurrentZoneSlavesFirstStrategy.class.getName()),
    READ_CURRENT_ZONE_SLAVES_ONLY("ReadCurrentZoneSlavesOnlyStrategy", ReadCurrentZoneSlavesOnlyStrategy.class.getName()),
    READ_MASTER_ZONE_SLAVES_FIRST("ReadMasterZoneSlavesFirstStrategy", ReadMasterZoneSlavesFirstStrategy.class.getName()),
    READ_MASTER_ZONE_SLAVES_ONLY("ReadMasterZoneSlavesOnlyStrategy", ReadMasterZoneSlavesOnlyStrategy.class.getName()),

    WRITE_ORDERED("OrderedAccessStrategy", MGRStrategy.class.getName()),
    WRITE_CURRENT_ZONE_FIRST("LocalizedAccessStrategy", OBStrategy.class.getName());

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

    public static RouteStrategyEnum parseEnum(String strategyName) {
        for (RouteStrategyEnum routeStrategyEnum : RouteStrategyEnum.values()) {
            if (routeStrategyEnum.name().equalsIgnoreCase(strategyName) || routeStrategyEnum.getAlias().equalsIgnoreCase(strategyName))
                return routeStrategyEnum;
        }
        return null;
    }

    public String getClazz() {
        return this.clazz;
    }

    public String getAlias() {
        return alias;
    }
}
