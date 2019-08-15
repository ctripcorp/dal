package com.ctrip.framework.dal.cluster.client.database;

/**
 * @author c7ch23en
 */
public interface Database {

    String getClusterName();

    int getShardIndex();

    boolean isMaster();

    ConnectionString getConnectionString();

    String[] getAliasKeys();

}
