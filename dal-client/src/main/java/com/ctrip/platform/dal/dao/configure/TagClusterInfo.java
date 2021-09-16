package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;

public class TagClusterInfo extends ClusterInfo{

    protected String tag;
    protected final String ID_FORMAT = "%s-%d-%s-%s"; //clusterName-shardIndex-role-tag

    public TagClusterInfo(String clusterName, Integer shardIndex, DatabaseRole role, String tag, boolean dbSharding, Cluster cluster) {
        this.clusterName = clusterName;
        this.shardIndex = shardIndex;
        this.role = role;
        this.tag = tag;
        this.dbSharding = dbSharding;
        this.cluster = cluster;
    }

    @Override
    public String toString() {
        return String.format(ID_FORMAT, clusterName, shardIndex, role != null ? role.getValue() : null, tag);
    }

    public String getTag() {
        return tag;
    }
}
