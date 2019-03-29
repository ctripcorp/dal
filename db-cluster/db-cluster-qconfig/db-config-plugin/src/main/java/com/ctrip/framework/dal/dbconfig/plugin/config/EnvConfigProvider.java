package com.ctrip.framework.dal.dbconfig.plugin.config;

import com.ctrip.framework.dal.dbconfig.plugin.context.IEnvProfile;

/**
 * @author c7ch23en
 */
public interface EnvConfigProvider {

    String getStringValue(String key, IEnvProfile envProfile);

    default Integer getIntValue(String key, IEnvProfile envProfile) {
        // TODO: to be improved
        return Integer.parseInt(getStringValue(key, envProfile));
    }

    default Long getLongValue(String key, IEnvProfile envProfile) {
        // TODO: to be improved
        return Long.parseLong(getStringValue(key, envProfile));
    }

}
