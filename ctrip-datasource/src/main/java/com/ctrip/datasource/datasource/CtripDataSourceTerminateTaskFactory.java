package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.dao.datasource.DataSourceTerminateTask;
import com.ctrip.platform.dal.dao.datasource.DataSourceTerminateTaskFactory;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;

public class CtripDataSourceTerminateTaskFactory implements DataSourceTerminateTaskFactory {
    @Override
    public DataSourceTerminateTask createTask(SingleDataSource oldDataSource) {
        DataSourceTerminateTask task = new CtripDataSourceTerminateTask(oldDataSource);
        return task;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
