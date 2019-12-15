package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;

/**
 * @author c7ch23en
 */
public interface LocalizationValidatorFactory {

    LocalizationValidator createValidator(LocalizationConfig config);

}
