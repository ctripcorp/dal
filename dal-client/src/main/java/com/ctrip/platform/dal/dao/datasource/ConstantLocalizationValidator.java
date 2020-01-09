package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;

public class ConstantLocalizationValidator implements LocalizationValidator {

    private boolean constantResult;

    public ConstantLocalizationValidator() {
        this(true);
    }

    public ConstantLocalizationValidator(boolean constantResult) {
        this.constantResult = constantResult;
    }

    @Override
    public boolean validateRequest() {
        return constantResult;
    }

    @Override
    public boolean validateZone() {
        return true;
    }

    @Override
    public LocalizationConfig getLocalizationConfig() {
        return null;
    }

}
