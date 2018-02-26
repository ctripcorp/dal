package com.ctrip.platform.dal.dao.datasource;

public class SingleDataSourceTask {
    private SingleDataSource singleDataSource;
    private DataSourceTerminateTask dataSourceTerminateTask;

    public SingleDataSourceTask(SingleDataSource singleDataSource, DataSourceTerminateTask dataSourceTerminateTask) {
        this.singleDataSource = singleDataSource;
        this.dataSourceTerminateTask = dataSourceTerminateTask;
    }

    public SingleDataSource getSingleDataSource() {
        return singleDataSource;
    }

    public void setSingleDataSource(SingleDataSource singleDataSource) {
        this.singleDataSource = singleDataSource;
    }

    public DataSourceTerminateTask getDataSourceTerminateTask() {
        return dataSourceTerminateTask;
    }

    public void setDataSourceTerminateTask(DataSourceTerminateTask dataSourceTerminateTask) {
        this.dataSourceTerminateTask = dataSourceTerminateTask;
    }

}
