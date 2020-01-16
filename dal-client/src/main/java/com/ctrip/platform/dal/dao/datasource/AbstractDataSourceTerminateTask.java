package com.ctrip.platform.dal.dao.datasource;

public abstract class AbstractDataSourceTerminateTask implements DataSourceTerminateTask {
    abstract void log(String dataSourceName, boolean isForceClosing, long startTimeMilliseconds);
}
