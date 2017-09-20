package com.ctrip.platform.dal.dao.configure;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceConfigureHolder {
    private static DataSourceConfigureHolder holder = null;

    public synchronized static DataSourceConfigureHolder getInstance() {
        if (holder == null) {
            holder = new DataSourceConfigureHolder();
        }
        return holder;
    }

    // user datasource.xml configure
    private Map<String, DataSourceConfigure> userDataSourceConfigures = new ConcurrentHashMap<>();

    private Map<String, DataSourceConfigure> dataSourceConfigures = new ConcurrentHashMap<>();

    public DataSourceConfigure getUserDataSourceConfigure(String name) {
        return userDataSourceConfigures.get(name.toUpperCase());
    }

    public DataSourceConfigure getDataSourceConfigure(String name) {
        return dataSourceConfigures.get(name.toUpperCase());
    }

    public Set<String> getDataSourceConfigureKeySet() {
        return dataSourceConfigures.keySet();
    }

    public void addUserDataSourceConfigure(String name, DataSourceConfigure configure) {
        userDataSourceConfigures.put(name.toUpperCase(), configure);
    }

    public void addDataSourceConfigure(String name, DataSourceConfigure configure) {
        dataSourceConfigures.put(name.toUpperCase(), configure);
    }

    public boolean contains(String name) {
        return dataSourceConfigures.containsKey(name.toUpperCase());
    }

}
