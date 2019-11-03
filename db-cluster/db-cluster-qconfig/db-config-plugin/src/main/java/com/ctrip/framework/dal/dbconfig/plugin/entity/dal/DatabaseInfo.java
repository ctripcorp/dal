package com.ctrip.framework.dal.dbconfig.plugin.entity.dal;

/**
 * Created by shenjie on 2019/4/18.
 */
public class DatabaseInfo {

    private String role;
    private String ip;
    private Integer port;
    private String dbName;
    private String uid;
    private String password;
    private Integer readWeight;
    private String tags;

    public DatabaseInfo() {
    }

    public DatabaseInfo(String role, String ip, Integer port, String dbName, String uid, String password, Integer readWeight, String tags) {
        this.role = role;
        this.ip = ip;
        this.port = port;
        this.dbName = dbName;
        this.uid = uid;
        this.password = password;
        this.readWeight = readWeight;
        this.tags = tags;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getReadWeight() {
        return readWeight;
    }

    public void setReadWeight(Integer readWeight) {
        this.readWeight = readWeight;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "DatabaseInfo{" +
                "role='" + role + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", dbName='" + dbName + '\'' +
                ", uid='" + uid + '\'' +
                ", password='" + password + '\'' +
                ", readWeight=" + readWeight +
                ", tags='" + tags + '\'' +
                '}';
    }
}
