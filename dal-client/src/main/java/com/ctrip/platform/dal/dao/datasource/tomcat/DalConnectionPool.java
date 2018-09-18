package com.ctrip.platform.dal.dao.datasource.tomcat;

import com.ctrip.platform.dal.dao.datasource.ConnectionListener;
import com.ctrip.platform.dal.dao.datasource.CreateConnectionCallback;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.LoggerHelper;
import com.ctrip.platform.dal.dao.helper.ServiceLoaderHelper;
import com.ctrip.platform.dal.dao.log.ILogger;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PooledConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

public class DalConnectionPool extends ConnectionPool {
    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static ConnectionListener connectionListener = ServiceLoaderHelper.getInstance(ConnectionListener.class);
    private static final String NULL = "NULL";
    private AtomicReference<String> poolUrlReference = new AtomicReference<>();

    public DalConnectionPool(PoolConfiguration prop) throws SQLException {
        super(prop);
    }

    @Override
    protected PooledConnection createConnection(long now, PooledConnection notUsed, String username, String password)
            throws SQLException {
        final AtomicReference<PooledConnection> pooledConnectionReference = new AtomicReference<>(null);
        final long tempNow = now;
        final PooledConnection tempNotUsed = notUsed;
        final String tempUsername = username;
        final String tempPassword = password;

        try {
            _createConnection(tempNow, tempNotUsed, tempUsername, tempPassword, pooledConnectionReference);
        } catch (Exception e) {
            LOGGER.error("[createConnection]" + this, e);
        }
        return pooledConnectionReference.get();
    }

    private void _createConnection(final long now, final PooledConnection notUsed, final String username,
            final String password, final AtomicReference<PooledConnection> pooledConnectionReference) {
        connectionListener.onCreateConnection(getPoolUrl(), new CreateConnectionCallback() {
            @Override
            public Connection createConnection() throws Exception {
                PooledConnection pooledConnection =
                        DalConnectionPool.super.createConnection(now, notUsed, username, password);
                pooledConnectionReference.set(pooledConnection);
                return pooledConnection == null ? null : pooledConnection.getConnection();
            }
        });
    }

    @Override
    protected void release(PooledConnection con) {
        try {
            connectionListener.onReleaseConnection(getName(), con == null ? null : con.getConnection());
        } catch (Exception e) {
            LOGGER.error("[release]" + this, e);
        }

        super.release(con);
    }

    @Override
    protected void abandon(PooledConnection con) {
        try {
            connectionListener.onAbandonConnection(getName(), con == null ? null : con.getConnection());
        } catch (Exception e) {
            LOGGER.error("[abandon]" + this, e);
        }

        super.abandon(con);
    }

    public static void setConnectionListener(ConnectionListener connectionListener) {
        DalConnectionPool.connectionListener = connectionListener;
    }

    private String getPoolUrl() {
        // initialization of the instance has not completed yet
        if (poolUrlReference == null) {
            return _getPoolUrl();
        }

        // the instance has already been created
        if (poolUrlReference.get() == null) {
            poolUrlReference.set(_getPoolUrl());
        }

        return poolUrlReference.get();
    }

    private String _getPoolUrl() {
        try {
            return LoggerHelper.getSimplifiedDBUrl(getPoolProperties().getUrl());
        } catch (Throwable e) {
            return NULL;
        }
    }

}
