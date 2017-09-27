package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyNameHelper;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceConfigureLocator {
    private volatile static DataSourceConfigureLocator locator = null;

    public synchronized static DataSourceConfigureLocator getInstance() {
        if (locator == null) {
            locator = new DataSourceConfigureLocator();
        }
        return locator;
    }

    // user datasource.xml configure
    private Map<String, DataSourceConfigure> userDataSourceConfigures = new ConcurrentHashMap<>();

    private Map<String, DataSourceConfigure> dataSourceConfigures = new ConcurrentHashMap<>();

    public DataSourceConfigure getUserDataSourceConfigure(String name) {
        String keyName = ConnectionStringKeyNameHelper.getKeyName(name);
        return userDataSourceConfigures.get(keyName);
    }

    public DataSourceConfigure getDataSourceConfigure(String name) {
        String keyName = ConnectionStringKeyNameHelper.getKeyName(name);
        return dataSourceConfigures.get(keyName);
    }

    public Set<String> getDataSourceConfigureKeySet() {
        return dataSourceConfigures.keySet();
    }

    public void addUserDataSourceConfigure(String name, DataSourceConfigure configure) {
        String keyName = ConnectionStringKeyNameHelper.getKeyName(name);
        userDataSourceConfigures.put(keyName, configure);
    }

    public void addDataSourceConfigure(String name, DataSourceConfigure configure) {
        String keyName = ConnectionStringKeyNameHelper.getKeyName(name);
        dataSourceConfigures.put(keyName, configure);
    }

    public boolean contains(String name) {
        String keyName = ConnectionStringKeyNameHelper.getKeyName(name);
        return dataSourceConfigures.containsKey(keyName);
    }

}
