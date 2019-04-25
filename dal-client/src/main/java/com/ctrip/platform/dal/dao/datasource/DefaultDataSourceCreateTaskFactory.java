package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;


public class DefaultDataSourceCreateTaskFactory implements DataSourceCreateTaskFactory {
    @Override
    public DataSourceCreateTask createTask(String name, DataSourceConfigure configure, SingleDataSource singleDataSource) {
        DataSourceCreateTask task = new DefaultDataSourceCreateTask(name, configure, singleDataSource);
        return task;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
