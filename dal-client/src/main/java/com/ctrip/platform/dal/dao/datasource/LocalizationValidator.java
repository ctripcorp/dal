package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;

public interface LocalizationValidator {

    LocalizationValidator DEFAULT = new ConstantLocalizationValidator();

    boolean validateRequest();

    boolean validateZone();

    LocalizationConfig getLocalizationConfig();

}
