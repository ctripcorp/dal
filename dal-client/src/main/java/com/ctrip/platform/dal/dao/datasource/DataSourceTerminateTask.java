package com.ctrip.platform.dal.dao.datasource;

public interface DataSourceTerminateTask extends Runnable {
    void init(SingleDataSource singleDataSource);
}
