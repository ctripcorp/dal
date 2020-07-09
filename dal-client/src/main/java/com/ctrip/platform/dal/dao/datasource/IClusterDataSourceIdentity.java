package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;

/**
 * @author c7ch23en
 */
public interface IClusterDataSourceIdentity extends DataSourceIdentity {

    String getClusterName();

    Integer getShardIndex();

    DatabaseRole getDatabaseRole();

}
