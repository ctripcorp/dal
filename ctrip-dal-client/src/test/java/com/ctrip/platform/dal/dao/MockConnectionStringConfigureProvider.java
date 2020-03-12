package com.ctrip.platform.dal.dao;

import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.platform.dal.dao.configure.DalConnectionStringConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringConfigureProvider;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

public class MockConnectionStringConfigureProvider implements ConnectionStringConfigureProvider {

    @Override
    public DalConnectionStringConfigure getConnectionString() throws Exception {
        String url = "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.2.7.196)(port=3306),address=(type=master)(protocol=tcp)(host=10.2.7.184)(port=3306),address=(type=master)(protocol=tcp)(host=10.2.7.187)(port=3306)/kevin";
        String userName = "f_xie";
        String password = "123456";
        String driver = "com.mysql.jdbc.Driver";
        String dbName = "kevin";
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        dataSourceConfigure.setConnectionUrl(url);
        dataSourceConfigure.setName(dbName);
        dataSourceConfigure.setUserName(userName);
        dataSourceConfigure.setPassword(password);
        dataSourceConfigure.setDriverClass(driver);
        return dataSourceConfigure;
    }

    @Override
    public void addListener(Listener<DalConnectionStringConfigure> listener) {

    }
}
