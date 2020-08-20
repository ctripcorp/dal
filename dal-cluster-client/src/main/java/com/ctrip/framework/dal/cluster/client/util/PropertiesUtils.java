package com.ctrip.framework.dal.cluster.client.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

/**
 * @author c7ch23en
 */
public class PropertiesUtils {

    protected static final String KEY_SEPARATOR = ".";

    public static Properties toProperties(String content) throws IOException {
        if (content == null)
            return null;
        Properties properties = new Properties();
        try (StringReader reader = new StringReader(content)) {
            properties.load(reader);
        }
        return properties;
    }

    public static Properties filterProperties(Properties properties, String keyPrefix) {
        return filterProperties(properties, keyPrefix, false);
    }

    public static Properties filterProperties(Properties properties, String keyPrefix, boolean caseSensitive) {
        if (properties == null)
            return null;
        Properties filteredProperties = new Properties();
        if (!StringUtils.isEmpty(keyPrefix))
            keyPrefix = (caseSensitive ? keyPrefix : keyPrefix.toLowerCase()) + KEY_SEPARATOR;
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String originalKey = (String) entry.getKey();
            if (StringUtils.isEmpty(keyPrefix) && !originalKey.contains(KEY_SEPARATOR)) {
                filteredProperties.setProperty(originalKey, (String) entry.getValue());
                continue;
            }
            String normalizedKey = caseSensitive ? originalKey : originalKey.toLowerCase();
            if (!StringUtils.isEmpty(keyPrefix) && normalizedKey.startsWith(keyPrefix))
                filteredProperties.setProperty(originalKey.substring(keyPrefix.length()), (String) entry.getValue());
        }
        return filteredProperties;
    }

    public static Properties mergeProperties(Properties parentProperties, Properties subProperties) {
        Properties mergedProperties = cloneProperties(parentProperties);
        if (subProperties == null)
            return mergedProperties;
        if (mergedProperties == null)
            mergedProperties = new Properties();
        mergedProperties.putAll(subProperties);
        return mergedProperties;
    }

    public static Properties cloneProperties(Properties properties) {
        if (properties == null)
            return null;
        Properties clonedProperties = new Properties();
        clonedProperties.putAll(properties);
        return clonedProperties;
    }

    public static String getProperty(Properties properties, String key) {
        return properties != null ? properties.getProperty(key) : null;
    }

    public static String getProperty(Properties properties, String key, String defaultValue) {
        String value = getProperty(properties, key);
        return value == null ? defaultValue : value;
    }

    public static String getPropertyNotEmpty(Properties properties, String key, String defaultValue) {
        String value = getProperty(properties, key);
        value = StringUtils.isTrimmedEmpty(value) ? defaultValue : value;
        if (StringUtils.isTrimmedEmpty(value))
            throw new IllegalArgumentException("Default value should not be trimmed empty");
        return value;
    }

    public static int getIntProperty(Properties properties, String key, int defaultValue) {
        String value = getProperty(properties, key);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    public static int getIntPropertyNotEmpty(Properties properties, String key, int defaultValue) {
        String value = getProperty(properties, key);
        return StringUtils.isTrimmedEmpty(value) ? defaultValue : Integer.parseInt(value);
    }

    public static long getLongProperty(Properties properties, String key, long defaultValue) {
        String value = getProperty(properties, key);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    public static long getLongPropertyNotEmpty(Properties properties, String key, long defaultValue) {
        String value = getProperty(properties, key);
        return StringUtils.isTrimmedEmpty(value) ? defaultValue : Long.parseLong(value);
    }

    public static boolean getBoolProperty(Properties properties, String key, boolean defaultValue) {
        String value = getProperty(properties, key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    public static boolean getBoolPropertyNotEmpty(Properties properties, String key, boolean defaultValue) {
        String value = getProperty(properties, key);
        return StringUtils.isTrimmedEmpty(value) ? defaultValue : Boolean.parseBoolean(value);
    }

}
