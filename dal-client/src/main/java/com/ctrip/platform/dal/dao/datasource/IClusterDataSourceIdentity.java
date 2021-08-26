package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.cluster.database.DatabaseRole;

/**
 * @author c7ch23en
 */
public interface IClusterDataSourceIdentity extends ClusterIdentity {

    Integer getShardIndex();

    DatabaseRole getDatabaseRole();

}
