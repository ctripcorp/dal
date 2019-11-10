package com.ctrip.framework.dal.dbconfig.plugin.entity.dal.configure;

import javax.xml.bind.annotation.*;

/**
 * Created by shenjie on 2019/5/7.
 */
@XmlAccessorType(XmlAccessType.FIELD)
// XML文件中的根标识
@XmlRootElement(name = "Cluster")
public class Cluster {

    @XmlAttribute(name = "name")
    private String name;
    @XmlAttribute(name = "dbCategory")
    private String category;
    @XmlAttribute(name = "version")
    private int version;
    @XmlElement(name = "DatabaseShards")
    private DatabaseShards shards;
    @XmlElement(name = "SslCode")
    private String sslCode;
    @XmlElement(name = "Operator")
    private String operator;
    @XmlElement(name = "UpdateTime")
    private String updateTime;

    public Cluster() {
    }

    public Cluster(String name, String category, int version, DatabaseShards shards) {
        this.name = name;
        this.category = category;
        this.version = version;
        this.shards = shards;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public DatabaseShards getShards() {
        return shards;
    }

    public void setShards(DatabaseShards shards) {
        this.shards = shards;
    }

    public String getSslCode() {
        return sslCode;
    }

    public void setSslCode(String sslCode) {
        this.sslCode = sslCode;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", version=" + version +
                ", shards=" + shards +
                ", sslCode='" + sslCode + '\'' +
                ", operator='" + operator + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
