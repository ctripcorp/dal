package com.ctrip.platform.dal.dao.datasource.tomcat;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.configure.DalExtendedPoolConfiguration;
import com.ctrip.platform.dal.dao.datasource.ConnectionListener;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.LoggerHelper;
import com.ctrip.platform.dal.dao.helper.ServiceLoaderHelper;
import com.ctrip.platform.dal.dao.log.Callback;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.sun.javafx.image.BytePixelSetter;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PooledConnection;

import java.sql.*;

public class DalConnectionPool extends ConnectionPool {
    private static ILogger logger = DalElementFactory.DEFAULT.getILogger();
    private static ConnectionListener connectionListener = ServiceLoaderHelper.getInstance(ConnectionListener.class);

    private static ThreadLocal<Long> poolWaitTime = new ThreadLocal<>();

    public DalConnectionPool(PoolConfiguration prop) throws SQLException {
        super(prop);
    }

    @Override
    public Connection getConnection() throws SQLException {
        poolWaitTime.set(System.currentTimeMillis());
        return super.getConnection();
    }

    @Override
    protected PooledConnection borrowConnection(long now, PooledConnection con, String username, String password) throws SQLException {
        try {
            long waitTime = System.currentTimeMillis() - poolWaitTime.get().longValue();
            if (waitTime > 1) {
                connectionListener.onWaitConnection(getName(), getConnection(con), poolWaitTime.get().longValue());
            }
        } catch (Exception e) {
            logger.error("[borrowConnection]" + this, e);
        }

        return super.borrowConnection(now, con, username, password);
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

        preHandleConnection(pooledConnection);

        return pooledConnection;
    }

    @Override
    protected void release(PooledConnection con) {
        try {
            connectionListener.onReleaseConnection(getName(), getConnection(con));
        } catch (Exception e) {
            logger.error("[releaseConnection]" + this, e);
        }

        super.release(con);
    }

    @Override
    protected void abandon(PooledConnection con) {
        try {
            connectionListener.onAbandonConnection(getName(), getConnection(con));
        } catch (Exception e) {
            logger.error("[abandonConnection]" + this, e);
        }

        super.abandon(con);
    }


    public static void setConnectionListener(ConnectionListener connectionListener) {
        DalConnectionPool.connectionListener = connectionListener;
    }

    private Connection getConnection(PooledConnection con) {
        return con == null ? null : con.getConnection();
    }

    private void preHandleConnection(PooledConnection conn) {
        Connection connection = getConnection(conn);
        if (connection != null) {
            trySetServerWaitTimeout(connection);
        }
    }

    private void trySetServerWaitTimeout(Connection conn) {
        PoolConfiguration config = getPoolProperties();
        if (config instanceof DalExtendedPoolConfiguration &&
                DatabaseCategory.MySql == DatabaseCategory.matchWithConnectionUrl(config.getUrl())) {
            int serverWaitTimeout = ((DalExtendedPoolConfiguration) config).getServerWaitTimeout();
            if (serverWaitTimeout > 0) {
                String connUrl = LoggerHelper.getSimplifiedDBUrl(config.getUrl());
                String logName = String.format("Connection::setServerWaitTimeout:%s", connUrl);
                try {
                    logger.logTransaction(DalLogTypes.DAL_DATASOURCE, logName,
                            String.format("serverWaitTimeout: %ds, connectionUrl: %s", serverWaitTimeout, connUrl),
                            () -> setServerWaitTimeout(conn, serverWaitTimeout));
                } catch (Throwable t) {
                    logger.error("set serverWaitTimeout exception: " + connUrl, t);
                }
            }
        }
    }

    private void setServerWaitTimeout(Connection conn, int serverWaitTimeout) throws SQLException {
        boolean autoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(true);
            try (Statement statement = conn.createStatement()) {
                statement.setQueryTimeout(1);
                statement.execute(String.format("set session wait_timeout = %d", serverWaitTimeout));
                try (ResultSet rs = statement.executeQuery("show session variables like 'wait_timeout'")) {
                    if (rs != null && rs.next() && serverWaitTimeout == rs.getInt(2))
                        logger.info(String.format("set serverWaitTimeout to %ds succeeded: %s",
                                serverWaitTimeout, getName()));
                    else
                        logger.warn("check serverWaitTimeout failed: " + getName());
                } catch (Throwable t) {
                    logger.warn("check serverWaitTimeout exception: " + getName(), t);
                }
            }
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }

}
