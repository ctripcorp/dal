package com.ctrip.framework.dal.cluster.client.config;

/**
 * @author c7ch23en
 */
public class LocalizationConfigImpl implements LocalizationConfig {

    private int unitStrategyId;
    private String zoneId;

    public LocalizationConfigImpl(int unitStrategyId, String zoneId) {
        this.unitStrategyId = unitStrategyId;
        this.zoneId = zoneId;
    }

    @Override
    public int getUnitStrategyId() {
        return unitStrategyId;
    }

    @Override
    public String getZoneId() {
        return zoneId;
    }

    @Override
    public String toString() {
        return "unitStrategyId=" + unitStrategyId +
                (zoneId == null ? "" : ", zoneId=" + zoneId);
    }

}
