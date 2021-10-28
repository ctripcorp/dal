package com.ctrip.framework.dal.cluster.client.config;


import com.ctrip.framework.dal.cluster.client.cluster.DrcConsistencyTypeEnum;

/**
 * @author c7ch23en
 */
public class LocalizationConfigImpl implements LocalizationConfig {

    private Integer unitStrategyId;
    private String zoneId;
    private LocalizationState localizationState;
    private DrcConsistencyTypeEnum drcConsistencyType;

    public LocalizationConfigImpl(Integer unitStrategyId, String zoneId, LocalizationState localizationState, DrcConsistencyTypeEnum drcConsistencyTypeEnum) {
        this.unitStrategyId = unitStrategyId;
        this.zoneId = zoneId;
        this.localizationState = localizationState;
        this.drcConsistencyType = drcConsistencyTypeEnum;
    }

    @Override
    public Integer getUnitStrategyId() {
        return unitStrategyId;
    }

    @Override
    public String getZoneId() {
        return zoneId;
    }

    @Override
    public DrcConsistencyTypeEnum getDrcConsistencyType() {
        return drcConsistencyType;
    }

    @Override
    public LocalizationState getLocalizationState() {
        return localizationState;
    }

    @Override
    public String toString() {
        return "LocalizationConfigImpl{" +
                "unitStrategyId=" + unitStrategyId +
                ", zoneId='" + zoneId + '\'' +
                ", localizationState=" + localizationState +
                '}';
    }

}
