package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.cluster.database.DatabaseRole;
import com.ctrip.platform.dal.cluster.util.StringUtils;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.IClusterDataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.log.ClusterDbSqlContext;
import com.ctrip.platform.dal.dao.datasource.log.SqlContext;

public class ClusterInfo {

    private static final String ID_FORMAT = "%s-%d-%s";
    private static final String TAG = "-%s";
    private static final String SLAVE_INDEX = "-%d";

    private String clusterName;
    private Integer shardIndex;
    private DatabaseRole role;
    private String tag;
    private boolean dbSharding;
    private Cluster cluster;
    private Integer slaveIndex;

    public ClusterInfo() {}

    public ClusterInfo(String clusterName, Integer shardIndex, DatabaseRole role, boolean dbSharding) {
        this(clusterName, shardIndex, role, dbSharding, null);
    }

    public ClusterInfo(String clusterName, Integer shardIndex, DatabaseRole role, boolean dbSharding, Cluster cluster) {
        this(clusterName, shardIndex, role, null, dbSharding, cluster);
    }

    public ClusterInfo(String clusterName, Integer shardIndex, DatabaseRole role, String tag, boolean dbSharding, Cluster cluster) {
        this(clusterName, shardIndex, role, tag, dbSharding, cluster, null);
    }

    private ClusterInfo(String clusterName, Integer shardIndex, DatabaseRole role, String tag, boolean dbSharding, Cluster cluster, Integer slaveIndex) {
        this.clusterName = clusterName;
        this.shardIndex = shardIndex;
        this.role = role;
        this.tag = tag;
        this.dbSharding = dbSharding;
        this.cluster = cluster;
        this.slaveIndex = slaveIndex;
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

    public boolean dbSharding() {
        return dbSharding;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public String getTag() {
        return tag;
    }

    public Integer getSlaveIndex() {
        return slaveIndex;
    }

    public DataSourceIdentity toDataSourceIdentity() {
        return new SimpleClusterDataSourceIdentity(this, cluster);
    }

    public ClusterInfo defineRoleClone(DatabaseRole role, Integer slaveIndex) {
        return new ClusterInfo(clusterName, shardIndex, role, tag, dbSharding, cluster, slaveIndex);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format(ID_FORMAT, clusterName, shardIndex, role != null ? role.getValue() : null));
        if (!StringUtils.isEmpty(tag))
            sb.append(String.format(TAG, tag));
        if (slaveIndex != null)
            sb.append(String.format(SLAVE_INDEX, slaveIndex));
        return sb.toString();
    }

    static class SimpleClusterDataSourceIdentity implements DataSourceIdentity, IClusterDataSourceIdentity {

        private ClusterInfo clusterInfo;
        private Cluster cluster;
        private String id;

        public SimpleClusterDataSourceIdentity(ClusterInfo clusterInfo, Cluster cluster) {
            this.clusterInfo = clusterInfo;
            this.cluster = cluster;
            this.id = clusterInfo.toString();
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public SqlContext createSqlContext() {
            ClusterDbSqlContext context = new ClusterDbSqlContext(cluster, getShardIndex(), getDatabaseRole());
            if (cluster != null && cluster.getLocalizationConfig() != null)
                context.populateDbZone(cluster.getLocalizationConfig().getZoneId());
            return context;
        }

        @Override
        public String getClusterName() {
            return clusterInfo.getClusterName();
        }

        @Override
        public Integer getShardIndex() {
            return clusterInfo.getShardIndex();
        }

        @Override
        public DatabaseRole getDatabaseRole() {
            return clusterInfo.getRole();
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
