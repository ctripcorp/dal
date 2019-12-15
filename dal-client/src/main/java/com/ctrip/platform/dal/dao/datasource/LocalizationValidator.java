package com.ctrip.platform.dal.dao.datasource;

public interface LocalizationValidator {

    LocalizationValidator DEFAULT = new ConstantLocalizationValidator();

    boolean validate();

}
