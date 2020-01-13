package com.ctrip.platform.dal.dao.configure;

import java.util.Map;

public class DataSourceConfigureWrapper {
    private Map<String, String> originalMap = null;
    private DataSourceConfigure dataSourceConfigure = null;
    private Map<String, DataSourceConfigure> dataSourceConfigureMap = null;

    public DataSourceConfigureWrapper(Map<String, String> originalMap, DataSourceConfigure dataSourceConfigure,
            Map<String, DataSourceConfigure> dataSourceConfigureMap) {
        this.originalMap = originalMap;
        this.dataSourceConfigure = dataSourceConfigure;
        this.dataSourceConfigureMap = dataSourceConfigureMap;
    }

    public Map<String, String> getOriginalMap() {
        return originalMap;
    }

    public void setOriginalMap(Map<String, String> originalMap) {
        this.originalMap = originalMap;
    }

    public DataSourceConfigure getDataSourceConfigure() {
        return dataSourceConfigure;
    }

    public void setDataSourceConfigure(DataSourceConfigure dataSourceConfigure) {
        this.dataSourceConfigure = dataSourceConfigure;
    }

    public Map<String, DataSourceConfigure> getDataSourceConfigureMap() {
        return dataSourceConfigureMap;
    }

    public void setDataSourceConfigureMap(Map<String, DataSourceConfigure> dataSourceConfigureMap) {
        this.dataSourceConfigureMap = dataSourceConfigureMap;
    }
}
