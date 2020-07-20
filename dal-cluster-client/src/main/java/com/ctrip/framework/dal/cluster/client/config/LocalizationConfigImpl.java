package com.ctrip.framework.dal.cluster.client.config;

/**
 * @author c7ch23en
 */
public class LocalizationConfigImpl implements LocalizationConfig {

    private Integer unitStrategyId;
    private String zoneId;
    private LocalizationState localizationState;

    public LocalizationConfigImpl(Integer unitStrategyId, String zoneId, LocalizationState localizationState) {
        this.unitStrategyId = unitStrategyId;
        this.zoneId = zoneId;
        this.localizationState = localizationState;
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
