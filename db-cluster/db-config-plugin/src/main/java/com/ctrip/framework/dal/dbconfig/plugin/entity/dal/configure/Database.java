package com.ctrip.framework.dal.dbconfig.plugin.entity.dal.configure;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by shenjie on 2019/5/7.
 */
@XmlAccessorType(XmlAccessType.FIELD)
// XML文件中的根标识
@XmlRootElement(name = "Database")
public class Database {

    @XmlAttribute(name = "role")
    private String role;
    @XmlAttribute(name = "ip")
    private String ip;
    @XmlAttribute(name = "port")
    private int port;
    @XmlAttribute(name = "dbName")
    private String dbName;
    @XmlAttribute(name = "uid")
    private String uid;
    @XmlAttribute(name = "pwd")
    private String password;
    @XmlAttribute(name = "readWeight")
    private Integer readWeight;
    @XmlAttribute(name = "tags")
    private String tags;

    public Database() {
    }

    public Database(String role, String ip, int port, String dbName, String uid, String password, Integer readWeight, String tags) {
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
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
        return "Database{" +
                "role='" + role + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", dbName='" + dbName + '\'' +
                ", uid='" + uid + '\'' +
                ", pwd='" + "******" + '\'' +
                ", readWeight=" + readWeight +
                ", tags='" + tags + '\'' +
                '}';
    }
}
