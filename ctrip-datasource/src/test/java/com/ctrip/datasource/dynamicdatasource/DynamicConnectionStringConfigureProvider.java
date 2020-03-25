package com.ctrip.datasource.dynamicdatasource;

import com.ctrip.datasource.configure.MysqlApiConnectionStringConfigureProvider;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.configure.DalConnectionStringConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

public class DynamicConnectionStringConfigureProvider extends MysqlApiConnectionStringConfigureProvider {

    private String url;
    private boolean connectionStringSwitch = false;

    public DynamicConnectionStringConfigureProvider(String dbName) {
        super(dbName);
    }

    @Override
    protected DalConnectionStringConfigure getConnectionStringFromMysqlApi() throws Exception {
        initMysqlApiConfigure();
        if (connectionStringSwitch) {
            String url = "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.2.7.196)(port=3306),address=((type=master)(protocol=tcp)(host=10.2.7.184)(port=3306),address=((type=master)(protocol=tcp)(host=10.2.7.187)(port=3306)/kevin";
            String userName = "f_xie";
            String password = "123456";
            String driver = "com.mysql.jdbc.Driver";
            String dbName = "kevin";
            DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
            if (StringUtils.isEmpty(this.url)) {
                dataSourceConfigure.setConnectionUrl(url);
            } else {
                dataSourceConfigure.setConnectionUrl(this.url);
            }
            dataSourceConfigure.setName(dbName);
            dataSourceConfigure.setUserName(userName);
            dataSourceConfigure.setPassword(password);
            dataSourceConfigure.setDriverClass(driver);
            return dataSourceConfigure;
        }
        else {
            return null;
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setConnectionStringSwitch(boolean connectionStringSwitch) {
        this.connectionStringSwitch = connectionStringSwitch;
    }
}
