package com.ctrip.platform.dal.dao.configure;

public interface IDataSourceConfigureProvider {
//    get datasource configure from local cache
    IDataSourceConfigure getDataSourceConfigure() throws Exception;

//    load datasource configure from configure center
    IDataSourceConfigure forceLoadDataSourceConfigure();
}
