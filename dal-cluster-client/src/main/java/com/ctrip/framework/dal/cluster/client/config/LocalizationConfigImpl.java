package com.ctrip.framework.dal.cluster.client.config;

/**
 * @author c7ch23en
 */
public class LocalizationConfigImpl implements LocalizationConfig {

    private int unitStrategyId;

    public LocalizationConfigImpl(int unitStrategyId) {
        this.unitStrategyId = unitStrategyId;
    }

    @Override
    public int getUnitStrategyId() {
        return unitStrategyId;
    }

    @Override
    public String toString() {
        return "unitStrategyId=" + unitStrategyId;
    }

}
