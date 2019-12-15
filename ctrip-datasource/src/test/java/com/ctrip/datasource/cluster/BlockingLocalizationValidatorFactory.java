package com.ctrip.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.platform.dal.dao.datasource.ConstantLocalizationValidator;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidator;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidatorFactory;

/**
 * @author c7ch23en
 */
public class BlockingLocalizationValidatorFactory implements LocalizationValidatorFactory {

    private LocalizationValidator validator = new ConstantLocalizationValidator(false);

    @Override
    public LocalizationValidator createValidator(LocalizationConfig config) {
        return validator;
    }

}
