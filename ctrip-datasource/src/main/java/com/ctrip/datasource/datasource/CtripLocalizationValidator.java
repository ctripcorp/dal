package com.ctrip.datasource.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.ucs.client.api.StrategyValidatedResult;
import com.ctrip.framework.ucs.client.api.Ucs;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidator;

public class CtripLocalizationValidator implements LocalizationValidator {

    private Ucs ucs;
    private LocalizationConfig config;

    public CtripLocalizationValidator(Ucs ucs, LocalizationConfig config) {
        this.ucs = ucs;
        this.config = config;
    }

    @Override
    public boolean validate() {
        StrategyValidatedResult result = ucs.validateStrategyContext(config.getUnitStrategyId());
        return result.shouldProcessDBOperation();
    }

}
