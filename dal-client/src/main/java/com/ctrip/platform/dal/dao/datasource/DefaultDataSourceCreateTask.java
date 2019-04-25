package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

public class DefaultDataSourceCreateTask implements DataSourceCreateTask {
    private String name;
    private DataSourceConfigure dataSourceConfigure;
    private SingleDataSource singleDataSource;
    private volatile boolean cancelled = false;

    public DefaultDataSourceCreateTask(String name, DataSourceConfigure configure, SingleDataSource singleDataSource) {
        this.name = name;
        this.dataSourceConfigure = configure;
        this.singleDataSource = singleDataSource;
    }

    @Override
    public void run() {
        if (!cancelled)
            singleDataSource.createPool(name, dataSourceConfigure);
    }

    @Override
    public void cancel() {
        cancelled = true;
    }
}
