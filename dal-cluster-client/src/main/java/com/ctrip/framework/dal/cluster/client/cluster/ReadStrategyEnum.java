package com.ctrip.framework.dal.cluster.client.cluster;

public enum ReadStrategyEnum {

    READ_MASTER("ReadMasterStrategy", "com.ctrip.platform.dal.dao.datasource.cluster.ReadMasterStrategy"),
    READ_SLAVES_FIRST("ReadSlavesFirstStrategy", "com.ctrip.platform.dal.dao.datasource.cluster.ReadSlavesFirstStrategy"),
    READ_SLAVES_ONLY("ReadSlavesOnlyStrategy", "com.ctrip.platform.dal.dao.datasource.cluster.ReadSlavesOnlyStrategy"),
    READ_CURRENT_ZONE_SLAVES_FIRST("ReadCurrentZoneSlavesFirstStrategy", "com.ctrip.platform.dal.dao.datasource.cluster.ReadCurrentZoneSlavesFirstStrategy"),
    READ_CURRENT_ZONE_SLAVES_ONLY("ReadCurrentZoneSlavesOnlyStrategy", "com.ctrip.platform.dal.dao.datasource.cluster.ReadCurrentZoneSlavesOnlyStrategy"),
    READ_MASTER_ZONE_SLAVES_FIRST("ReadCurrentZoneSlavesOnlyStrategy", "com.ctrip.platform.dal.dao.datasource.cluster.ReadMasterZoneSlavesFirstStrategy"),
    READ_MASTER_ZONE_SLAVES_ONLY("ReadCurrentZoneSlavesOnlyStrategy", "com.ctrip.platform.dal.dao.datasource.cluster.ReadMasterZoneSlavesOnlyStrategy");

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
