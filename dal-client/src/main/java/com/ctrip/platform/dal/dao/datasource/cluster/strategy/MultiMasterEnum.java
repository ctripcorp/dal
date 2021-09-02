package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import static com.ctrip.platform.dal.cluster.config.ClusterConfigXMLConstants.LOCALIZED_ACCESS_STRATEGY;
import static com.ctrip.platform.dal.cluster.config.ClusterConfigXMLConstants.ORDERED_ACCESS_STRATEGY;

/**
 * @Author limingdong
 * @create 2021/9/2
 */
public enum MultiMasterEnum {

    OrderedAccessStrategy(ORDERED_ACCESS_STRATEGY, "com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.mgr.MGRStrategy"),

    LocalizedAccessStrategy(LOCALIZED_ACCESS_STRATEGY, "com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob.OBStrategy");

    private String alias;
    private String clazz;

    MultiMasterEnum(String alias, String clazz) {
        this.alias = alias;
        this.clazz = clazz;
    }

    public static String parse(String name) {
        for (MultiMasterEnum readStrategyEnum : MultiMasterEnum.values()) {
            if (readStrategyEnum.name().equalsIgnoreCase(name))
                return readStrategyEnum.clazz;
        }
        return name;
    }

    public String getClazz() {
        return this.clazz;
    }

    public String getAlias() {
        return alias;
    }
}
