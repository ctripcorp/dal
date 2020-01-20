package com.ctrip.platform.dal.dao.configure;

public class MockMgrDataSourceConfigureProvider extends MockDataSourceConfigureProvider {

    @Override
    public IDataSourceConfigure getDataSourceConfigure() {
        String connectionUrl = "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.2.7.196)(port=3306),address=((type=master)(protocol=tcp)(host=10.2.7.184)(port=3306),address=((type=master)(protocol=tcp)(host=10.2.7.187)(port=3306)/kevin";
        String userName = "f_xie";
        String password = "123456";
        String driverClass = "com.mysql.jdbc.Driver";
        String connectionProperties = "connectTimeout=2000";
        return new MockDataSourceConfigure(connectionUrl, userName, password, driverClass, connectionProperties);
    }

    @Override
    public IDataSourceConfigure forceLoadDataSourceConfigure() {
        String connectionUrl = "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.2.7.196)(port=3306),address=((type=master)(protocol=tcp)(host=10.2.7.184)(port=3306),address=((type=master)(protocol=tcp)(host=10.2.7.187)(port=3306)/kevin";
        String userName = "f_xie";
        String password = "123456";
        String driverClass = "com.mysql.jdbc.Driver";
        String connectionProperties = "connectTimeout=2000";
        return new MockDataSourceConfigure(connectionUrl, userName, password, driverClass, connectionProperties);
    }
}
