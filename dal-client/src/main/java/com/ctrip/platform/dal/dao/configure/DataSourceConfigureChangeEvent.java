package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.datasource.DataSourceTerminateTask;

public class DataSourceConfigureChangeEvent {
    private String name;
    private DataSourceConfigure newDataSourceConfigure;
    private DataSourceConfigure oldDataSourceConfigure;
    private DataSourceTerminateTask dataSourceTerminateTask;

    public DataSourceConfigureChangeEvent(String name, DataSourceConfigure newDataSourceConfigure,
            DataSourceConfigure oldDataSourceConfigure) {
        this.name = name;
        this.newDataSourceConfigure = newDataSourceConfigure;
        this.oldDataSourceConfigure = oldDataSourceConfigure;
    }

    public DataSourceConfigureChangeEvent(String name, DataSourceConfigure newDataSourceConfigure,
            DataSourceConfigure oldDataSourceConfigure, DataSourceTerminateTask dataSourceTerminateTask) {
        this.name = name;
        this.newDataSourceConfigure = newDataSourceConfigure;
        this.oldDataSourceConfigure = oldDataSourceConfigure;
        this.dataSourceTerminateTask = dataSourceTerminateTask;
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


    public DataSourceTerminateTask getDataSourceTerminateTask() {
        return dataSourceTerminateTask;
    }

    public void setDataSourceTerminateTask(DataSourceTerminateTask dataSourceTerminateTask) {
        this.dataSourceTerminateTask = dataSourceTerminateTask;
    }

}
