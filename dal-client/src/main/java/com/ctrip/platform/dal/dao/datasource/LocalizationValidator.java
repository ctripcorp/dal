package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;

public interface LocalizationValidator {

    LocalizationValidator DEFAULT = new ConstantLocalizationValidator();

    ValidationResult validateRequest(boolean isUpdateOperation);

    boolean validateZone();

    LocalizationConfig getLocalizationConfig();

}
