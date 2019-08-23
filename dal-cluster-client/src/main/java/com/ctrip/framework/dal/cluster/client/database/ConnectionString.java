package com.ctrip.framework.dal.cluster.client.database;

/**
 * @author c7ch23en
 */
public interface ConnectionString {

    String getPrimaryConnectionUrl();

    String getFailOverConnectionUrl();

    String getDbName();

    String getUsername();

    String getPassword();

    String getDriverClassName();

    String getPrimaryHost();

    int getPrimaryPort();

}
