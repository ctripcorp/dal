package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.helper.EncryptionHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class DataSourceConfigure implements DataSourceConfigureConstants {
    private String name;
    private Properties properties = new Properties();
    private Map<String, String> map = new HashMap<>();
    private String version;

    public DataSourceConfigure() {}

    public DataSourceConfigure(String name) {
        this.name = name;
    }

    public DataSourceConfigure(String name, Properties properties) {
        this(name);
        this.properties = properties;
    }

    public DataSourceConfigure(String name, Map<String, String> propertyMap) {
        this(name);
        merge(propertyMap);
    }

    public void merge(Properties properties) {
        for (Object keyObj : properties.keySet()) {
            String key = (String) keyObj;
            setProperty(key, properties.getProperty(key));
        }
    }

    public void merge(Map<String, String> propertyMap) {
        for (Map.Entry<String, String> entry : propertyMap.entrySet())
            properties.setProperty(entry.getKey(), entry.getValue());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return getProperty(USER_NAME);
    }

    public void setUserName(String userName) {
        setProperty(USER_NAME, userName);
    }

    public String getPassword() {
        return getProperty(PASSWORD);
    }

    public void setPassword(String password) {
        setProperty(PASSWORD, password);
    }

    public String getConnectionUrl() {
        return getProperty(CONNECTION_URL);
    }

    public void setConnectionUrl(String connectionUrl) {
        setProperty(CONNECTION_URL, connectionUrl);
    }

    public String getDriverClass() {
        return getProperty(DRIVER_CLASS_NAME);
    }

    public void setDriverClass(String driverClass) {
        setProperty(DRIVER_CLASS_NAME, driverClass);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        map.put(key, value);
    }

    public int getIntProperty(String key, int defaultValue) {
        return properties.containsKey(key) ? Integer.parseInt(getProperty(key)) : defaultValue;
    }

    public long getLongProperty(String key, long defaultValue) {
        return properties.containsKey(key) ? Long.parseLong(getProperty(key)) : defaultValue;
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return properties.containsKey(key) ? Boolean.parseBoolean(getProperty(key)) : defaultValue;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public String toConnectionUrl() {
        return String.format("{ConnectionUrl:%s,Version:%s,CRC:%s}", getConnectionUrl(),
                (version == null ? "" : version), getCRC());
    }

    public Map<String, String> toMap() {
        Map<String, String> m = new HashMap<>();
        Set<String> set = new HashSet<>();
        set.add(USER_NAME);
        set.add(PASSWORD);
        set.add(CONNECTION_URL);
        set.add(DRIVER_CLASS_NAME);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!set.contains(entry.getKey())) {
                m.put(entry.getKey(), entry.getValue());
            }
        }

        return m;
    }

    public boolean dynamicPoolPropertiesEnabled() {
        if (map == null || map.isEmpty())
            return false;

        String value = map.get(ENABLE_DYNAMIC_POOL_PROPERTIES);
        if (value == null)
            return false;

        return Boolean.parseBoolean(value);
    }

    // Rule: username concat password,and then take 8 characters of md5 code from beginning
    private String getCRC() {
        String crc = "";
        String userName = getUserName();
        String pass = getPassword();
        try {
            userName.concat(pass);
            crc = EncryptionHelper.getCRC(userName);
        } catch (Throwable e) {
        }
        return crc;
    }

}
