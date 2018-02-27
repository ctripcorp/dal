package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.helper.Ordered;

public interface DataSourceTerminateTaskFactory extends Ordered {
    DataSourceTerminateTask createTask(SingleDataSource oldDataSource);
}
