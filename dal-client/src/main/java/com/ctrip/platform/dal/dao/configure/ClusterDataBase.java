package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.cluster.database.Database;
import com.ctrip.platform.dal.dao.datasource.ClusterDataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;

/**
 * @author c7ch23en
 */
public class ClusterDataBase extends ClusterDataSourceIdentity implements DataBase {

    public ClusterDataBase(Database database) {
        super(database);
    }

    @Override
    public String getName() {
        return getId();
    }

    @Override
    public boolean isMaster() {
        return getDatabase().isMaster();
    }

    @Override
    public String getSharding() {
        return String.valueOf(getDatabase().getShardIndex());
    }

    @Override
    public String getConnectionString() {
        return getName();
    }

    @Override
    public DataSourceIdentity getDataSourceIdentity() {
        return this;
    }

}
