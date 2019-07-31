package com.ctrip.framework.dal.cluster.client.database;

/**
 * @author c7ch23en
 */
public interface Database {

    boolean isMaster();

    ConnectionString getConnectionString();

}
