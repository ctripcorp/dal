package com.ctrip.platform.dal.dao.configure;

public class MockDataSourceConfigure extends AbstractDataSourceConfigure {
    private String connectionUrl;
    private String userName;
    private String password;
//    private String hostName;
//    private Integer port;
//    private String dbName;
    private String driverClass;
    private String connectionProperties;

    public MockDataSourceConfigure(String connectionUrl, String userName, String password, String driverClass, String connectionProperties) {
        this.connectionUrl = connectionUrl;
        this.userName = userName;
        this.password = password;
//        this.hostName = hostName;
//        this.port = port;
//        this.dbName = dbName;
        this.driverClass = driverClass;
        this.connectionProperties = connectionProperties;
    }


    @Override
    public String getConnectionUrl() {
        return connectionUrl;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

//    @Override
//    public String getHostName() {
//        return hostName;
//    }
//
//    @Override
//    public Integer getPort() {
//        return port;
//    }
//
//    public String getDBName() {
//        return dbName;
//    }

    @Override
    public String getDriverClass() {
        return driverClass;
    }

    @Override
    public String getConnectionProperties() {
        return connectionProperties;
    }
}
