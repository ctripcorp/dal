package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;

public class BlockingLocalizationValidator implements LocalizationValidator {

    @Override
    public void initialize(LocalizationConfig config) {}

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

}
