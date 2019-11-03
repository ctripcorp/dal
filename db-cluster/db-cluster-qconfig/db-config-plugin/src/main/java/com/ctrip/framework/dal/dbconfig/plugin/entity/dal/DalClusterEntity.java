package com.ctrip.framework.dal.dbconfig.plugin.entity.dal;

import java.util.List;

/**
 * Created by shenjie on 2019/4/18.
 */
public class DalClusterEntity {

    private String clusterName;
    private String dbCategory;
    private int version;
    private List<DatabaseShardInfo> databaseShards;
    private String operator;
    private String sslCode;

    public DalClusterEntity() {
    }

    public DalClusterEntity(String clusterName, String dbCategory, int version, List<DatabaseShardInfo> databaseShards) {
        this.clusterName = clusterName;
        this.dbCategory = dbCategory;
        this.version = version;
        this.databaseShards = databaseShards;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDbCategory() {
        return dbCategory;
    }

    public void setDbCategory(String dbCategory) {
        this.dbCategory = dbCategory;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<DatabaseShardInfo> getDatabaseShards() {
        return databaseShards;
    }

    public void setDatabaseShards(List<DatabaseShardInfo> databaseShards) {
        this.databaseShards = databaseShards;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getSslCode() {
        return sslCode;
    }

    public void setSslCode(String sslCode) {
        this.sslCode = sslCode;
    }

    @Override
    public String toString() {
        return "DalClusterEntity{" +
                "clusterName='" + clusterName + '\'' +
                ", category='" + dbCategory + '\'' +
                ", version=" + version +
                ", databaseShards=" + databaseShards +
                ", operator='" + operator + '\'' +
                ", sslCode='" + sslCode + '\'' +
                '}';
    }
}
