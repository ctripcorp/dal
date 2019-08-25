package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.DataSourceTerminateTask;
import com.ctrip.platform.dal.dao.datasource.DataSourceTerminateTaskFactory;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;

import javax.sql.DataSource;

public class CtripDataSourceTerminateTaskFactory implements DataSourceTerminateTaskFactory {
    @Override
    public DataSourceTerminateTask createTask(SingleDataSource oldDataSource) {
        return new CtripDataSourceTerminateTask(oldDataSource);
    }

    @Override
    public DataSourceTerminateTask createTask(String name, DataSource ds, DataSourceConfigure configure) {
        return new CtripDataSourceTerminateTask(name, ds, configure);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
