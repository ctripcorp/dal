package com.ctrip.platform.dal.dao.configure;

public class MockDataSourceConfigureProvider implements IDataSourceConfigureProvider {

    @Override
    public IDataSourceConfigure getDataSourceConfigure() {
//        connectionString="Server=10.32.20.116;port=3306;UID=root;password=!QAZ@WSX1qaz2wsx;database=llj_test;"
        String connectionUrl = "jdbc:mysql://10.32.20.116:3306/llj_test?useUnicode=true&characterEncoding=UTF-8";
        String userName = "root";
        String password = "!QAZ@WSX1qaz2wsx";
        String driverClass = "com.mysql.jdbc.Driver";
        String connectionProperties = "connectTimeout=2000";
        return new MockDataSourceConfigure(connectionUrl, userName, password, driverClass, connectionProperties);
    }

    @Override
    public IDataSourceConfigure forceLoadDataSourceConfigure() {
//        connectionString="Server=DST56614;port=3306;UID=root;password=!QAZ@WSX1qaz2wsx;database=llj_test;"
        String connectionUrl = "jdbc:mysql://DST56614:3306/llj_test?useUnicode=true&characterEncoding=UTF-8";
        String userName = "root";
        String password = "!QAZ@WSX1qaz2wsx";
        String driverClass = "com.mysql.jdbc.Driver";
        String connectionProperties = "connectTimeout=2000";
        return new MockDataSourceConfigure(connectionUrl, userName, password, driverClass, connectionProperties);
    }
}
