package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;

public interface LocalizationValidator {

    LocalizationValidator DEFAULT = new DefaultLocalizationValidator();

    void initialize(LocalizationConfig config);

    boolean validate();

}
