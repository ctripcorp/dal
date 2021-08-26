package com.ctrip.platform.dal.cluster.config;

import com.ctrip.platform.dal.cluster.cluster.DrcConsistencyTypeEnum;

/**
 * @author c7ch23en
 */
public interface LocalizationConfig {

    Integer getUnitStrategyId();

    String getZoneId();

    DrcConsistencyTypeEnum getDrcConsistencyType();

    LocalizationState getLocalizationState();

}
