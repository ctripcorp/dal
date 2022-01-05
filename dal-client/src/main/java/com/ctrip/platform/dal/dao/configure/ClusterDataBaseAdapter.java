package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.platform.dal.dao.datasource.ClusterInfoDelegateIdentity;

/**
 * @Author limingdong
 * @create 2021/11/16
 */
public class ClusterDataBaseAdapter extends ClusterDataBase implements DataBase, ClusterInfoDelegateIdentity {

    private static final int SHARD = 0;

    private Cluster cluster;

    public ClusterDataBaseAdapter(Cluster cluster) {
        super(cluster.getMasterOnShard(SHARD));
        this.cluster = cluster;
    }

    @Override
    public ClusterInfo getClusterInfo() {
        return new ClusterInfo(cluster.getClusterName(), SHARD, DatabaseRole.MASTER, false, cluster);
    }
}
