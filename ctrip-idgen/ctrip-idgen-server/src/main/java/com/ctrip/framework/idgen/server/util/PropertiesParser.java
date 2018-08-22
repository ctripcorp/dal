package com.ctrip.framework.idgen.server.util;

import com.ctrip.framework.idgen.server.config.ConfigConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

public class PropertiesParser {

    public static int parseInt(final Map<String, String> properties, String key) {
        validate(properties, key);
        try {
            return Integer.parseInt(properties.get(key));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Property of [" + key + "] format exception", e);
        }
    }

    public static long parseLong(final Map<String, String> properties, String key) {
        validate(properties, key);
        try {
            return Long.parseLong(properties.get(key));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Property of [" + key + "] format exception", e);
        }
    }

    public static boolean parseBoolean(final Map<String, String> properties, String key) {
        validate(properties, key);
        try {
            return Boolean.parseBoolean(properties.get(key));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Property of [" + key + "] format exception", e);
        }
    }

    public static String parseDateString(final Map<String, String> properties, String key) {
        validate(properties, key);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(ConfigConstants.DATEREFERENCE_FORMAT);
            sdf.parse(properties.get(key));
        } catch (ParseException e) {
            throw new RuntimeException("Property of [" + key + "] format exception", e);
        }
        return properties.get(key);
    }

    private static boolean validate(final Map<String, String> properties, String key) {
        if (null == properties) {
            throw new RuntimeException("Properties is empty");
        }
        if (!properties.containsKey(key)) {
            throw new RuntimeException("Property of [" + key + "] not found");
        }
        return true;
    }

}
