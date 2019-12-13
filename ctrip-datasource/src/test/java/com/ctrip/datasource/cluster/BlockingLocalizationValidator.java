package com.ctrip.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidator;

public class BlockingLocalizationValidator implements LocalizationValidator {

    @Override
    public void initialize(LocalizationConfig config) {}

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
