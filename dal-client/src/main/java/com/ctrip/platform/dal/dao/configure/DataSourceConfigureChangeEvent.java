package com.ctrip.platform.dal.dao.configure;

public class DataSourceConfigureChangeEvent {
    private String name;
    private DataSourceConfigure newDataSourceConfigure;
    private DataSourceConfigure oldDataSourceConfigure;

    public DataSourceConfigureChangeEvent(String name, DataSourceConfigure newDataSourceConfigure,
            DataSourceConfigure oldDataSourceConfigure) {
        this.name = name;
        this.newDataSourceConfigure = newDataSourceConfigure;
        this.oldDataSourceConfigure = oldDataSourceConfigure;
    }

    public String getName() {
        return name;
    }

    public DataSourceConfigure getNewDataSourceConfigure() {
        return newDataSourceConfigure;
    }

    public DataSourceConfigure getOldDataSourceConfigure() {
        return oldDataSourceConfigure;
    }

}
