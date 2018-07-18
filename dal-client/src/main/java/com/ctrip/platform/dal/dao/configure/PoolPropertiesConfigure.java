package com.ctrip.platform.dal.dao.configure;

import java.util.Properties;

public interface PoolPropertiesConfigure {
    Properties getProperties();

    String getProperty(String key);

    String getProperty(String key, String defaultValue);

    int getIntProperty(String key, int defaultValue);

    long getLongProperty(String key, long defaultValue);

    boolean getBooleanProperty(String key, boolean defaultValue);

}
