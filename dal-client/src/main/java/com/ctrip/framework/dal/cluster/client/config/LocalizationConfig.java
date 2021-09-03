package com.ctrip.framework.dal.cluster.client.config;


import com.ctrip.framework.dal.cluster.client.cluster.DrcConsistencyTypeEnum;

/**
 * @author c7ch23en
 */
public interface LocalizationConfig {

    Integer getUnitStrategyId();

    String getZoneId();

    DrcConsistencyTypeEnum getDrcConsistencyType();

    LocalizationState getLocalizationState();

}
