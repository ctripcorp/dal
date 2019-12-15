package com.ctrip.platform.dal.dao.datasource;

public class ConstantLocalizationValidator implements LocalizationValidator {

    private boolean constantResult;

    public ConstantLocalizationValidator() {
        this(true);
    }

    public ConstantLocalizationValidator(boolean constantResult) {
        this.constantResult = constantResult;
    }

    @Override
    public boolean validate() {
        return constantResult;
    }

}
