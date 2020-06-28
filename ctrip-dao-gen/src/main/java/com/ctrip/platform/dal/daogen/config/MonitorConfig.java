package com.ctrip.platform.dal.daogen.config;

import java.util.Map;
import java.util.Properties;

/**
 * Created by taochen on 2019/8/6.
 */
public class MonitorConfig {
    private static final String IP_ADDRESS = "ipAddress";

    private static final String SENDER = "sender";

    private static final String DB_EMAIL_RECIPIENT = "dbEmailRecipient";

    private static final String SWITCH_EMAIL_RECIPIENT = "switchEmailRecipient";

    private static final String DB_EMAIL_CC = "dbEmailCc";

    private static final String SWITCH_EMAIL_CC = "switchEmailCc";

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

    public String getDBEmailRecipient() {
        return getProperty(DB_EMAIL_RECIPIENT);
    }

    public void setDBEmailRecipient(String recipient) {
        setProperty(DB_EMAIL_RECIPIENT, recipient);
    }

    public String getSwitchEmailRecipient() {
        return getProperty(SWITCH_EMAIL_RECIPIENT);
    }

    public void setSwitchEmailRecipient(String recipient) {
        setProperty(SWITCH_EMAIL_RECIPIENT, recipient);
    }

    public String getDBEmailCc() {
        return getProperty(DB_EMAIL_CC);
    }

    public void setDBEmailCc(String cc) {
        setProperty(DB_EMAIL_CC, cc);
    }

    public String getSwitchEmailCc() {
        return getProperty(SWITCH_EMAIL_CC);
    }

    public void setSwitchEmailCc(String cc) {
        setProperty(SWITCH_EMAIL_CC, cc);
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
