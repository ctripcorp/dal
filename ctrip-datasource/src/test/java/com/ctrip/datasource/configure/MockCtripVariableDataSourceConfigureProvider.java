package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.DalConnectionStringConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MockCtripVariableDataSourceConfigureProvider extends CtripVariableDataSourceConfigureProvider {
    @Override
    public Map<String, DalConnectionStringConfigure> getConnectionStrings(Set<String> dbNames) throws UnsupportedEncodingException {
        // one config
        String url = "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.2.7.196)(port=3306),address=((type=master)(protocol=tcp)(host=10.2.7.184)(port=3306),address=((type=master)(protocol=tcp)(host=10.2.7.187)(port=3306)/kevin";
        String userName = "f_xie";
        String password = "123456";
        String driver = "com.mysql.jdbc.Driver";
        String dbName = "kevin";
        Map<String, DalConnectionStringConfigure> configureMap = new HashMap<>();
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        dataSourceConfigure.setConnectionUrl(url);
        dataSourceConfigure.setName(dbName);
        dataSourceConfigure.setUserName(userName);
        dataSourceConfigure.setPassword(password);
        dataSourceConfigure.setDriverClass(driver);
        configureMap.put(dbName, dataSourceConfigure);
        return configureMap;
    }
}
