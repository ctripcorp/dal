package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.DataSourceName;

/**
 * @author c7ch23en
 */
public interface DataBase {

    String getName();

    boolean isMaster();

    String getSharding();

    String getConnectionString();

    default DataSourceIdentity getDataSourceIdentity() {
        return new DataSourceName(getName());
    }

}
