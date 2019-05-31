package com.ctrip.platform.dal.dao.configure;

public interface IDataSourceConfigureProvider {
//    get datasource configure from local cache
    IDataSourceConfigure getDataSourceConfigure();

//    load datasource configure from configure center
    IDataSourceConfigure forceLoadDataSourceConfigure();
}
