package com.ctrip.framework.dal.cluster.client.database;

import com.ctrip.framework.dal.cluster.client.Cluster;

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
