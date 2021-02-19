package com.ctrip.platform.dal.dao.datasource.jdbc;

/**
 * @author c7ch23en
 */
public interface ClusterDatabaseMetaData extends DalDatabaseMetaData {

    String getClusterName();

    int getShardIndex();

}
