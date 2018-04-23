package com.ctrip.platform.dal.dao.configure;

import java.util.Map;
import java.util.Properties;

public class PropertiesWrapper {
    private Properties originalProperties;
    private Properties appProperties;
    private Map<String, Properties> datasourceProperties;

    public PropertiesWrapper(Properties originalProperties, Properties appProperties,
            Map<String, Properties> datasourceProperties) {
        this.originalProperties = originalProperties;
        this.appProperties = appProperties;
        this.datasourceProperties = datasourceProperties;
    }

    public Properties getAppProperties() {
        return appProperties;
    }

    public Map<String, Properties> getDatasourceProperties() {
        return datasourceProperties;
    }

}
