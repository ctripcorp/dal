package com.ctrip.platform.dal.dao.datasource.tomcat;

import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionValidator;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;

import java.sql.SQLException;

public class DalTomcatDataSource extends DataSource {

    private final ConnectionValidator clusterConnValidator;

    public DalTomcatDataSource(PoolConfiguration poolProperties) {
        this(poolProperties, null);
    }

    public DalTomcatDataSource(PoolConfiguration poolProperties, ConnectionValidator clusterConnValidator) {
        super(poolProperties);
        this.clusterConnValidator = clusterConnValidator;
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
