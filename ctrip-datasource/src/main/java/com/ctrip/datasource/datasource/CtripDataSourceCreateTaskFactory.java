package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.DataSourceCreateTask;
import com.ctrip.platform.dal.dao.datasource.DataSourceCreateTaskFactory;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;

public class CtripDataSourceCreateTaskFactory implements DataSourceCreateTaskFactory {
    @Override
    public DataSourceCreateTask createTask(String name, DataSourceConfigure configure, SingleDataSource singleDataSource) {
        DataSourceCreateTask task = new CtripDataSourceCreateTask(name, configure, singleDataSource);
        return task;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
