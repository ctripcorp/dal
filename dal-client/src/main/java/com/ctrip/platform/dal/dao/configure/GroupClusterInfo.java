package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;

public class GroupClusterInfo extends ClusterInfo {

    protected Integer slaveIndex;
    protected final String ID_FORMAT = "%s-%d-%s-%d"; //clusterName-shardIndex-role-slaveIndex

    public GroupClusterInfo(String clusterName, Integer shardIndex, DatabaseRole role, boolean dbSharding, Cluster cluster, Integer slaveIndex) {
        this.clusterName = clusterName;
        this.shardIndex = shardIndex;
        this.role = role;
        this.dbSharding = dbSharding;
        this.cluster = cluster;
        this.slaveIndex = slaveIndex;
    }

    @Override
    public String toString() {
        return String.format(ID_FORMAT, clusterName, shardIndex, role != null ? role.getValue() : null, slaveIndex);
    }

    public Integer getSlaveIndex() {
        return slaveIndex;
    }
}
