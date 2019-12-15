package com.ctrip.datasource.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.ucs.client.api.Ucs;
import com.ctrip.framework.ucs.client.api.UcsClient;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidator;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidatorFactory;

/**
 * @author c7ch23en
 */
public class CtripLocalizationValidatorFactory implements LocalizationValidatorFactory {

    private Ucs ucs = UcsClient.getInstance();

    public CtripLocalizationValidatorFactory() {}

    @Override
    public LocalizationValidator createValidator(LocalizationConfig config) {
        return new CtripLocalizationValidator(ucs, config);
    }

}
