package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;

public class DefaultLocalizationValidatorFactory implements LocalizationValidatorFactory {

    @Override
    public LocalizationValidator createValidator(LocalizationConfig config) {
        return LocalizationValidator.DEFAULT;
    }

}
