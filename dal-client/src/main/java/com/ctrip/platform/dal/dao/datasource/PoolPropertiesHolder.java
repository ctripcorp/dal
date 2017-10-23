package com.ctrip.platform.dal.dao.datasource;

import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.util.concurrent.ConcurrentHashMap;

public class PoolPropertiesHolder {
    private static PoolPropertiesHolder holder = null;

    public synchronized static PoolPropertiesHolder getInstance() {
        if (holder == null) {
            holder = new PoolPropertiesHolder();
        }
        return holder;
    }

    private static final Object LOCK = new Object();
    private static final Object LOCK2 = new Object();
    private static final String SEMICOLON = ";";
    private static final String AT = "@";

    private static ConcurrentHashMap<String, ConcurrentHashMap<String, PoolProperties>> poolPropertiesMap =
            new ConcurrentHashMap<>();

    public void setPoolProperties(PoolProperties poolProperties) {
        if (poolProperties == null)
            return;

        String url = poolProperties.getUrl();
        if (url == null || url.length() == 0)
            return;

        String userName = poolProperties.getUsername();
        if (userName == null || userName.length() == 0)
            return;

        url = getShortString(url, SEMICOLON);
        userName = getShortString(userName, AT);
        ConcurrentHashMap<String, PoolProperties> map = poolPropertiesMap.get(url);

        if (map == null) {
            synchronized (LOCK) {
                map = poolPropertiesMap.get(url);
                if (map == null) {
                    map = new ConcurrentHashMap<>();
                    poolPropertiesMap.put(url, map);
                }
            }
        }

        /*
         * if (!map.containsKey(userName)) { synchronized (LOCK2) { if (!map.containsKey(userName)) { map.put(userName,
         * poolProperties); } } }
         */

        // avoid caching for InitSQL
        synchronized (LOCK2) {
            map.put(userName, poolProperties);
        }
    }

    public PoolProperties getPoolProperties(String url, String userName) {
        if (url == null || url.length() == 0)
            return null;
        if (userName == null || userName.length() == 0)
            return null;

        url = getShortString(url, SEMICOLON);
        userName = getShortString(userName, AT);
        ConcurrentHashMap<String, PoolProperties> map = poolPropertiesMap.get(url);
        if (map == null)
            return null;
        return map.get(userName);
    }

    private String getShortString(String str, String separator) {
        if (str == null || str.length() == 0)
            return null;
        int index = str.indexOf(separator);
        if (index > -1)
            str = str.substring(0, index);
        return str;
    }

}
