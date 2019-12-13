package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.platform.dal.dao.helper.Ordered;

public interface LocalizationValidator extends Ordered {

    LocalizationValidator DEFAULT = new DefaultLocalizationValidator();

    void initialize(LocalizationConfig config);

    boolean validate();

}
