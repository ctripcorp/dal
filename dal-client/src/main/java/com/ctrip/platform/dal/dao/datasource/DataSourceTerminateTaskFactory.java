package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.helper.Ordered;

import javax.sql.DataSource;

public interface DataSourceTerminateTaskFactory extends Ordered {
    DataSourceTerminateTask createTask(SingleDataSource oldDataSource);
    DataSourceTerminateTask createTask(String name, DataSource ds, DataSourceConfigure configure);
}
