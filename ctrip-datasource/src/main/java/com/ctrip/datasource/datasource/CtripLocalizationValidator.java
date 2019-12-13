package com.ctrip.datasource.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.ucs.client.api.StrategyValidatedResult;
import com.ctrip.framework.ucs.client.api.Ucs;
import com.ctrip.framework.ucs.client.api.UcsClient;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidator;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

public class CtripLocalizationValidator implements LocalizationValidator {

    private Ucs ucs = UcsClient.getInstance();
    private LocalizationConfig config;
    private volatile boolean initialized = false;

    @Override
    public synchronized void initialize(LocalizationConfig config) {
        if (!initialized) {
            this.config = config;
            initialized = true;
        }
    }

    @Override
    public boolean validate() {
        if (!initialized)
            throw new DalRuntimeException("CtripLocalizationValidator uninitialized");
        StrategyValidatedResult result = ucs.validateStrategyContext(config.getUnitStrategyId());
        return result.shouldProcessDBOperation();
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
