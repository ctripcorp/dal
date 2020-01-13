package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

public interface SingleDataSourceWrapper {

    SingleDataSource getSingleDataSource();

    void forceRefreshDataSource(String name, DataSourceConfigure configure);

}
