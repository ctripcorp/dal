package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.platform.dal.dao.datasource.log.OperationType;

public class ConstantLocalizationValidator implements LocalizationValidator {

    private boolean constantResult;

    public ConstantLocalizationValidator() {
        this(true);
    }

    public ConstantLocalizationValidator(boolean constantResult) {
        this.constantResult = constantResult;
    }

    @Override
    public ValidationResult validateRequest(OperationType operationType) {
        return new ValidationResult(constantResult, null, null);
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
