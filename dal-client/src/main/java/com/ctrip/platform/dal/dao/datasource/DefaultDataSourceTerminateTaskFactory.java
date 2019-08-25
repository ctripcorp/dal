package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

import javax.sql.DataSource;

public class DefaultDataSourceTerminateTaskFactory implements DataSourceTerminateTaskFactory {
    @Override
    public DataSourceTerminateTask createTask(SingleDataSource oldDataSource) {
        return new DefaultDataSourceTerminateTask(oldDataSource);
    }

    @Override
    public DataSourceTerminateTask createTask(String name, DataSource ds, DataSourceConfigure configure) {
        return new DefaultDataSourceTerminateTask(name, ds, configure);
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
