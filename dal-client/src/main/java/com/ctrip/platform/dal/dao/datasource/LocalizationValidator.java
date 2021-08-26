package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.cluster.config.LocalizationConfig;
import com.ctrip.platform.dal.dao.datasource.log.OperationType;

public interface LocalizationValidator {

    LocalizationValidator DEFAULT = new ConstantLocalizationValidator();

    ValidationResult validateRequest(OperationType operationType);

    boolean validateZone();

    LocalizationConfig getLocalizationConfig();

}
