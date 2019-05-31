package com.ctrip.platform.dal.dao.configure;

public interface IDataSourceConfigureProvider {
//    get datasource configure from local cache
    IDataSourceConfigure getDataSourceConfigure(String dbName);

//    load datasource configure from configure center
    IDataSourceConfigure loadDataSourceConfigure(String dbName);
}
