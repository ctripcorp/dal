package com.ctrip.framework.dal.dbconfig.plugin.entity.titan;

/**
 * Created by shenjie on 2019/6/26.
 */
public class TitanUpdateBasicData {
    private String dbName;
    private String domain;
    private String ip;
    private int port;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "TitanUpdateBasicData{" +
                "dbName='" + dbName + '\'' +
                ", domain='" + domain + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
