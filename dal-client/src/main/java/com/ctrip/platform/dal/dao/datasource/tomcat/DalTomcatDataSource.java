package com.ctrip.platform.dal.dao.datasource.tomcat;

import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;

import java.sql.SQLException;

public class DalTomcatDataSource extends DataSource {

    public DalTomcatDataSource(PoolConfiguration poolProperties) {
        super(poolProperties);
    }

    public ConnectionPool createPool() throws SQLException {
        if (pool != null) {
            return pool;
        } else {
            return pCreatePool();
        }
    }

    private synchronized ConnectionPool pCreatePool() throws SQLException {
        if (pool != null) {
            return pool;
        } else {
            pool = new DalConnectionPool(poolProperties);
            return pool;
        }
    }

}
