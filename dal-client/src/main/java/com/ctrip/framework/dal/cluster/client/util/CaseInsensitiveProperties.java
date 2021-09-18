package com.ctrip.framework.dal.cluster.client.util;

import java.util.*;

public class CaseInsensitiveProperties {

    private final Map<String, String> properties = new HashMap<>();

    public CaseInsensitiveProperties() {}

    public CaseInsensitiveProperties(Map<String, String> properties) {
        if (properties != null)
            properties.forEach(this::set);
    }

    public CaseInsensitiveProperties(Properties properties) {
        if (properties != null)
            properties.forEach((k, v) -> set((String) k, (String) v));
    }

    public String set(String key, String value) {
        return properties.put(key.toLowerCase(), value);
    }

    public String get(String key) {
        return properties.get(key.toLowerCase());
    }

    public String getString(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        String value = get(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public long getLong(String key, long defaultValue) {
        String value = get(key);
        return value != null ? Long.parseLong(value) : defaultValue;
    }

    public boolean getBool(String key, boolean defaultValue) {
        String value = get(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public List<String> getStringList(String key, String separator, List<String> defaultValue) {
        String value = get(key);
        return value != null ? Arrays.asList(value.split(separator, -1)) : defaultValue;
    }

    @Override
    public String toString() {
        return "CaseInsensitiveProperties{" +
                "properties=" + properties.toString() +
                '}';
    }

}
