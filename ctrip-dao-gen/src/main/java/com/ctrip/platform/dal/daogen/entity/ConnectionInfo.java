package com.ctrip.platform.dal.daogen.entity;

/**
 * Created by taochen on 2019/7/26.
 */
public class ConnectionInfo {
    private String server;

    private String serverIp;

    private String dbName;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
