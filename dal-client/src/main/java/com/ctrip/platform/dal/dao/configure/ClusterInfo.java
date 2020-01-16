package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;

public class ClusterInfo {

    private static final String ID_FORMAT = "%s-%d-%s";

    private String clusterName;
    private Integer shardIndex;
    private DatabaseRole role;

    public ClusterInfo() {}

    public ClusterInfo(String clusterName, Integer shardIndex, DatabaseRole role) {
        this.clusterName = clusterName;
        this.shardIndex = shardIndex;
        this.role = role;
    }

    public String getClusterName() {
        return clusterName;
    }

    public Integer getShardIndex() {
        return shardIndex;
    }

    public DatabaseRole getRole() {
        return role;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public DataSourceIdentity toDataSourceIdentity() {
        return new SimpleClusterDataSourceIdentity(toString());
    }

    public boolean isValid() {
        return clusterName != null && shardIndex != null && role == DatabaseRole.MASTER;
    }

    @Override
    public String toString() {
        return String.format(ID_FORMAT, clusterName, shardIndex, role != null ? role.getValue() : null);
    }

    static class SimpleClusterDataSourceIdentity implements DataSourceIdentity {

        private String id;

        public SimpleClusterDataSourceIdentity(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SimpleClusterDataSourceIdentity) {
                String id = getId();
                String objId = ((SimpleClusterDataSourceIdentity) obj).getId();
                return (id == null && objId == null) || (id != null && id.equalsIgnoreCase(objId));
            }
            return false;
        }

        @Override
        public int hashCode() {
            String id = getId();
            return id != null ? id.hashCode() : 0;
        }

    }

}
