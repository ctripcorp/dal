package com.ctrip.platform.dal.dao.datasource.tomcat;

import com.ctrip.platform.dal.dao.datasource.ConnectionListener;
import com.ctrip.platform.dal.dao.helper.LoggerHelper;
import com.ctrip.platform.dal.dao.helper.ServiceLoaderHelper;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DalConnectionPool extends ConnectionPool {
    private static Logger logger = LoggerFactory.getLogger(DalConnectionPool.class);

    private static ConnectionListener connectionListener = ServiceLoaderHelper.getInstance(ConnectionListener.class);

    public DalConnectionPool(PoolConfiguration prop) throws SQLException {
        super(prop);
    }

    @Override
    protected PooledConnection createConnection(long now, PooledConnection notUsed, String username, String password)
            throws SQLException {
        long startTime = System.currentTimeMillis();
        PooledConnection pooledConnection;

        try {
            pooledConnection = super.createConnection(now, notUsed, username, password);
        } catch (Throwable e) {
            String connectionUrl = LoggerHelper.getSimplifiedDBUrl(getPoolProperties().getUrl());
            connectionListener.onCreateConnectionFailed(getName(), connectionUrl, e, startTime);
            throw e;
        }

        try {
            connectionListener.onCreateConnection(getName(), getConnection(pooledConnection), startTime);
        } catch (Throwable e) {
            logger.error("[createConnection]" + this, e);
        }

        return pooledConnection;
    }

    @Override
    protected void release(PooledConnection con) {
        try {
            connectionListener.onReleaseConnection(getName(), getConnection(con));
        } catch (Exception e) {
            logger.error("[release]" + this, e);
        }

        super.release(con);
    }

    @Override
    protected void abandon(PooledConnection con) {
        try {
            connectionListener.onAbandonConnection(getName(), getConnection(con));
        } catch (Exception e) {
            logger.error("[abandon]" + this, e);
        }

        super.abandon(con);
    }

    @Override
    protected PooledConnection borrowConnection(long now, PooledConnection con, String username, String password) throws SQLException {
        try {
            connectionListener.onBorrowConnection(getName(), getConnection(con));
        } catch (Exception e) {
            logger.error("[borrow]" + this, e);
        }

        return super.borrowConnection(now, con, username, password);
    }

    public static void setConnectionListener(ConnectionListener connectionListener) {
        DalConnectionPool.connectionListener = connectionListener;
    }

    private Connection getConnection(PooledConnection con) {
        return con == null ? null : con.getConnection();
    }

}
