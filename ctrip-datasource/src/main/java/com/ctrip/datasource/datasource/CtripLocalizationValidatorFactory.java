package com.ctrip.datasource.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.ucs.client.api.UcsClient;
import com.ctrip.framework.ucs.client.api.UcsClientFactory;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidator;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidatorFactory;

/**
 * @author c7ch23en
 */
public class CtripLocalizationValidatorFactory implements LocalizationValidatorFactory {

    private volatile UcsClient ucsClient;
    private DalPropertiesLocator locator;

    public CtripLocalizationValidatorFactory(DalPropertiesLocator locator) {
        this(null, locator);
    }

    public CtripLocalizationValidatorFactory(UcsClient ucsClient, DalPropertiesLocator locator) {
        this.ucsClient = ucsClient;
        this.locator = locator;
    }

    @Override
    public LocalizationValidator createValidator(ClusterInfo clusterInfo, LocalizationConfig localizationConfig) {
        return new CtripLocalizationValidator(getOrCreateUcsClient(), locator, clusterInfo, localizationConfig);
    }

    private UcsClient getOrCreateUcsClient() {
        if (ucsClient == null) {
            synchronized (this) {
                if (ucsClient == null) {
                    ucsClient = UcsClientFactory.getInstance().getUcsClient();
                }
            }
        }
        return ucsClient;
    }

}
