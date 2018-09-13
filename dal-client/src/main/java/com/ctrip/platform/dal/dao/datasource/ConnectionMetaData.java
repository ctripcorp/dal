package com.ctrip.platform.dal.dao.datasource;

public class ConnectionMetaData {
    private String connectionUrl;
    private String userName;

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
