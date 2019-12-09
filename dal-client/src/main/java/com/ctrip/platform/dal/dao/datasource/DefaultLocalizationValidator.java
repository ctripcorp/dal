package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;

public class DefaultLocalizationValidator implements LocalizationValidator {

    @Override
    public void initialize(LocalizationConfig config) {}

    @Override
    public boolean validate() {
        return true;
    }

}
