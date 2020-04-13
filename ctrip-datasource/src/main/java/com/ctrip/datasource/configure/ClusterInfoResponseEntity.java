package com.ctrip.datasource.configure;

import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;

public class ClusterInfoResponseEntity {

    private int status;
    private String message;
    private ClusterInfoEntity result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ClusterInfoEntity getResult() {
        return result;
    }

    public void setResult(ClusterInfoEntity result) {
        this.result = result;
    }

    public ClusterInfo getClusterInfo() {
        return result != null ? result.toClusterInfo() : null;
    }

    static class ClusterInfoEntity {

        private String clusterName;
        private Integer shardIndex;
        private String role;
        private Boolean dbSharding;

        public String getClusterName() {
            return clusterName;
        }

        public void setClusterName(String clusterName) {
            this.clusterName = clusterName;
        }

        public Integer getShardIndex() {
            return shardIndex;
        }

        public void setShardIndex(Integer shardIndex) {
            this.shardIndex = shardIndex;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Boolean getDbSharding() {
            return dbSharding;
        }

        public void setDbSharding(Boolean dbSharding) {
            this.dbSharding = dbSharding;
        }

        public ClusterInfo toClusterInfo() {
            if (clusterName != null && shardIndex != null && role != null)
                return new ClusterInfo(clusterName, shardIndex, DatabaseRole.parse(role), dbSharding);
            else
                return null;
        }

    }

}
