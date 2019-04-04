package com.ctrip.framework.dal.dbconfig.plugin.entity.mongo;

import java.util.List;
import java.util.Map;

/**
 * Created by shenjie on 2019/4/3.
 */
public class MongoClusterEntity {

    private String clusterName;
    private String clusterType;
    private String dbName;
    private String userId;
    private String password;
    private List<Node> nodes;
    private Map<String, String> extraProperties;
    private Boolean enabled = true;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterType() {
        return clusterType;
    }

    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public Map<String, String> getExtraProperties() {
        return extraProperties;
    }

    public void setExtraProperties(Map<String, String> extraProperties) {
        this.extraProperties = extraProperties;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "MongoClusterEntity{" +
                "clusterName='" + clusterName + '\'' +
                ", clusterType='" + clusterType + '\'' +
                ", dbName='" + dbName + '\'' +
                ", userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                ", nodes=" + nodes +
                ", extraProperties=" + extraProperties +
                ", enabled=" + enabled +
                '}';
    }
}
