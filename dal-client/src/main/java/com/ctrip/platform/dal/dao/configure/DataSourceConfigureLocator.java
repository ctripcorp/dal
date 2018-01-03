package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;

import java.util.Collections;
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

    private Set<String> dataSourceConfigureKeySet = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    public DataSourceConfigure getUserDataSourceConfigure(String name) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        return userDataSourceConfigures.get(keyName);
    }

    public DataSourceConfigure getDataSourceConfigure(String name) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        return dataSourceConfigures.get(keyName);
    }

    public void addDataSourceConfigureKeySet(Set<String> dbNames) {
        if (dbNames == null || dbNames.isEmpty())
            return;

        for (String name : dbNames) {
            addDataSourceConfigureKey(name);
        }
    }

    private void addDataSourceConfigureKey(String name) {
        if (name == null || name.isEmpty())
            return;

        dataSourceConfigureKeySet.add(name);
    }

    public Set<String> getDataSourceConfigureKeySet() {
        return dataSourceConfigureKeySet;
    }

    public void addUserDataSourceConfigure(String name, DataSourceConfigure configure) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        userDataSourceConfigures.put(keyName, configure);
    }

    public void addDataSourceConfigure(String name, DataSourceConfigure configure) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        dataSourceConfigures.put(keyName, configure);
    }

    public boolean contains(String name) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        return dataSourceConfigures.containsKey(keyName);
    }

}
