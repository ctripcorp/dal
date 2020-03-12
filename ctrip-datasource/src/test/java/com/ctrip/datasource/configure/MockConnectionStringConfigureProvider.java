package com.ctrip.datasource.configure;

import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.configure.DalConnectionStringConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringConfigureProvider;


public class MockConnectionStringConfigureProvider implements ConnectionStringConfigureProvider {

    private Listener<DalConnectionStringConfigure> listener;
    private String url;

    @Override
    public DalConnectionStringConfigure getConnectionString() throws Exception {
        String url = "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.2.7.196)(port=3306),address=(type=master)(protocol=tcp)(host=10.2.7.184)(port=3306),address=(type=master)(protocol=tcp)(host=10.2.7.187)(port=3306)/kevin" +
                "?useUnicode=true&characterEncoding=UTF-8" +
                "&loadBalanceStrategy=serverAffinity&serverAffinityOrder=" +
                "address=(type=master)(protocol=tcp)(host=10.2.7.184)(port=3306):3306," +
                "address=(type=master)(protocol=tcp)(host=10.2.7.196)(port=3306):3306," +
                "address=(type=master)(protocol=tcp)(host=10.2.7.187)(port=3306):3306";
        String userName = "f_xie";
        String password = "123456";
        String driver = "com.mysql.jdbc.Driver";
        String dbName = "kevin";
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        if (StringUtils.isEmpty(this.url)) {
            dataSourceConfigure.setConnectionUrl(url);
        }
        else {
            dataSourceConfigure.setConnectionUrl(this.url);
        }
        dataSourceConfigure.setName(dbName);
        dataSourceConfigure.setUserName(userName);
        dataSourceConfigure.setPassword(password);
        dataSourceConfigure.setDriverClass(driver);
        return dataSourceConfigure;
    }

    @Override
    public void addListener(Listener<DalConnectionStringConfigure> listener) {
        this.listener = listener;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void switchDataSource() {
        String url = "jdbc:mysql://localhost:3306/test";
        String userName = "root";
        String password = "123456";
        String driver = "com.mysql.jdbc.Driver";
        String dbName = "test";
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        dataSourceConfigure.setConnectionUrl(url);
        dataSourceConfigure.setName(dbName);
        dataSourceConfigure.setUserName(userName);
        dataSourceConfigure.setPassword(password);
        dataSourceConfigure.setDriverClass(driver);
        listener.onChanged(dataSourceConfigure);
    }
}
