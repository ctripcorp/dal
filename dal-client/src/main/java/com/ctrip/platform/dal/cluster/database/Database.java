package com.ctrip.platform.dal.cluster.database;

import com.ctrip.platform.dal.cluster.Cluster;

/**
 * @author c7ch23en
 */
public interface Database {

    String getClusterName();

    int getShardIndex();

    boolean isMaster();

    String getZone();

    ConnectionString getConnectionString();

    String[] getAliasKeys();

    Cluster getCluster();

}
