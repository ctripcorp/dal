package com.ctrip.framework.dal.cluster.client.cluster;

public enum ReadStrategyEnum {

    READ_MASTER("com.ctrip.platform.dal.dao.datasource.cluster.ReadMasterStrategy"),
    READ_SLAVES_FIRST("com.ctrip.platform.dal.dao.datasource.cluster.ReadSlavesFirstStrategy"),
    READ_SLAVES_ONLY("com.ctrip.platform.dal.dao.datasource.cluster.ReadSlavesOnlyStrategy"),
    READ_CURRENT_ZONE_SLAVES_FIRST("com.ctrip.platform.dal.dao.datasource.cluster.ReadCurrentZoneSlavesFirstStrategy"),
    READ_CURRENT_ZONE_SLAVES_ONLY("com.ctrip.platform.dal.dao.datasource.cluster.ReadCurrentZoneSlavesOnlyStrategy"),
    READ_MASTER_ZONE_SLAVES_FIRST("com.ctrip.platform.dal.dao.datasource.cluster.ReadMasterZoneSlavesFirstStrategy"),
    READ_MASTER_ZONE_SLAVES_ONLY("com.ctrip.platform.dal.dao.datasource.cluster.ReadMasterZoneSlavesOnlyStrategy");

    private String clazz;

    ReadStrategyEnum(String clazz) {
        this.clazz = clazz;
    }

    public static String getClazz(String name) {
        for (ReadStrategyEnum readStrategyEnum : ReadStrategyEnum.values()) {
            if (readStrategyEnum.name().equalsIgnoreCase(name))
                return readStrategyEnum.clazz;
        }
        return name;
    }




}
