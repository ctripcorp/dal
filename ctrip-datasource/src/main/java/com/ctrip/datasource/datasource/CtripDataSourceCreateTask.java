package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.DefaultDataSourceCreateTask;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;

public class CtripDataSourceCreateTask extends DefaultDataSourceCreateTask {
    public CtripDataSourceCreateTask(String name, DataSourceConfigure configure, SingleDataSource singleDataSource) {
        super(name, configure, singleDataSource);
    }
}
