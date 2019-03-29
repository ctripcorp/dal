package com.ctrip.framework.dal.dbconfig.plugin.config;

/**
 * @author c7ch23en
 */
public interface ConfigProvider {

    String getStringValue(String key);

    default Integer getIntValue(String key) {
        // TODO: to be improved
        return Integer.parseInt(getStringValue(key));
    }

    default Long getLongValue(String key) {
        // TODO: to be improved
        return Long.parseLong(getStringValue(key));
    }

}
