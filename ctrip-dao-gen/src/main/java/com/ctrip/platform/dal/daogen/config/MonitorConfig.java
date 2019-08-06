package com.ctrip.platform.dal.daogen.config;

import java.util.Map;
import java.util.Properties;

/**
 * Created by taochen on 2019/8/6.
 */
public class MonitorConfig {
    private static final String IP_ADDRESS = "ipAddress";

    private static final String SENDER = "sender";

    private static final String RECIPIENT = "recipient";

    private static final String CC = "cc";

    private static final String FILTER_TITAN_KEY = "filterTitanKey";

    private Properties properties = new Properties();

    public MonitorConfig(Map<String, String> propertyMap) {
        merge(propertyMap);
    }

    public String getIpAddress() {
        return getProperty(IP_ADDRESS);
    }

    public void setIpAddress(String ipAddress) {
        setProperty(IP_ADDRESS, ipAddress);
    }

    public String getSender() {
        return getProperty(SENDER);
    }

    public void setSender(String sender) {
        setProperty(SENDER, sender);
    }

    public String getRecipient() {
        return getProperty(RECIPIENT);
    }

    public void setRecipient(String recipient) {
        setProperty(RECIPIENT, recipient);
    }

    public String getCc() {
        return getProperty(CC);
    }

    public void setCc(String cc) {
        setProperty(CC, cc);
    }

    public String getFilterTitanKey() {
        return getProperty(FILTER_TITAN_KEY);
    }

    public void setFilterTitanKey(String filterTitanKey) {
        setProperty(FILTER_TITAN_KEY, filterTitanKey);
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

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public void merge(Map<String, String> propertyMap) {
        if (propertyMap != null) {
            for (Map.Entry<String, String> entry : propertyMap.entrySet())
                properties.setProperty(entry.getKey(), entry.getValue());
        }
    }
}
