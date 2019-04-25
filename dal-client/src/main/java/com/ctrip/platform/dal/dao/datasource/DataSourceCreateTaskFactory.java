package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.helper.Ordered;

public interface DataSourceCreateTaskFactory extends Ordered {
    DataSourceCreateTask createTask(String name, DataSourceConfigure configure, SingleDataSource singleDataSource);
}
