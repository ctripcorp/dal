package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

public interface DataSourceCreatePoolListener {
    void onCreatePoolSuccess(DataSourceConfigure configure);
    void onCreatePoolFail(DataSourceConfigure configure, Throwable e);
}
