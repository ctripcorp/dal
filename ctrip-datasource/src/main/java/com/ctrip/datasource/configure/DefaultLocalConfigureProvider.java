package com.ctrip.datasource.configure;

import com.ctrip.framework.foundation.Foundation;
import com.ctrip.framework.foundation.config.local.Config;
import com.ctrip.platform.dal.exceptions.DalConfigException;

import java.io.FileNotFoundException;

public class DefaultLocalConfigureProvider implements LocalConfigureProvider {
    public String getConfigContent(String productName, String configName) throws Exception {
        if (!Foundation.server().getEnv().isLocal())
            throw new DalConfigException("only local env can get config from framework config");

        Config config = Foundation.server().localConfig().createPrioritizedConfig(productName, configName);
        if (config == null)
            throw new FileNotFoundException("config file not found");
        return config.getContent();
    }
}
