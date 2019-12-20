package com.ctrip.datasource.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.ucs.client.api.Ucs;
import com.ctrip.framework.ucs.client.api.UcsClient;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidator;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidatorFactory;

/**
 * @author c7ch23en
 */
public class CtripLocalizationValidatorFactory implements LocalizationValidatorFactory {

    private Ucs ucs;
    private DalPropertiesLocator locator;

    public CtripLocalizationValidatorFactory(Ucs ucs, DalPropertiesLocator locator) {
        this.ucs = ucs;
        this.locator = locator;
    }

    @Override
    public LocalizationValidator createValidator(ClusterInfo clusterInfo, LocalizationConfig localizationConfig) {
        return new CtripLocalizationValidator(ucs, locator, clusterInfo, localizationConfig);
    }

}
