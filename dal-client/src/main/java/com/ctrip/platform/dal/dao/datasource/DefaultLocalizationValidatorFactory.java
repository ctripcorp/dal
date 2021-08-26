package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.cluster.config.LocalizationConfig;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;

public class DefaultLocalizationValidatorFactory implements LocalizationValidatorFactory {

    @Override
    public LocalizationValidator createValidator(ClusterInfo clusterInfo, LocalizationConfig localizationConfig, LocalizationConfig lastLocalizationConfig) {
        return LocalizationValidator.DEFAULT;
    }
}
