package com.ctrip.framework.dal.cluster.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CaseInsensitiveProperty {

    private volatile HashMap<String, String> properties = new HashMap<>();

    public void setProperty(String key, String value) {
        if (!StringUtils.isTrimmedEmpty(key)) {
            properties.put(key.toLowerCase(), value);
        }
    }

    public String getString(String key, String defaultValue) {
        if (StringUtils.isTrimmedEmpty(key)) {
            return defaultValue;
        }
        String value = properties.get(key);
        return StringUtils.isTrimmedEmpty(value) ? defaultValue : value;
    }

    public long getLongValue(String key, long defaultValue) {
        if (StringUtils.isTrimmedEmpty(key)) {
            return defaultValue;
        }
        String value = properties.get(key.toLowerCase());
        return StringUtils.isTrimmedEmpty(value) ? defaultValue : Long.getLong(value);
    }

    public int getIntValue(String key, int defaultValue) {
        if (StringUtils.isTrimmedEmpty(key)) {
            return defaultValue;
        }
        String value = properties.get(key.toLowerCase());
        return StringUtils.isTrimmedEmpty(value) ? defaultValue : Integer.getInteger(value);
    }

    public List<String> getStringList(String key, List<String> defaultValue) {
        if (StringUtils.isTrimmedEmpty(key)) {
            return defaultValue;
        }

        String value = properties.get(key.toLowerCase());
        List<String> resultList = new ArrayList<>();
        if (StringUtils.isEmpty(value)) {
            return defaultValue == null ? resultList : defaultValue;
        }

        String[] ss = value.split(",");
        for (String s : ss) {
            if (!StringUtils.isTrimmedEmpty(s)) {
                resultList.add(s.trim());
            }
        }

        return resultList;
    }

    public boolean getBooleanValue(String key, boolean defaultValue) {
        if (StringUtils.isTrimmedEmpty(key)) {
            return defaultValue;
        }
        String value = properties.get(key.toLowerCase());
        return StringUtils.isTrimmedEmpty(value) ? defaultValue : Boolean.getBoolean(value);
    }

}
