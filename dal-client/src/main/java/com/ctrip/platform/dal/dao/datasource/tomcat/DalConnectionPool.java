package com.ctrip.platform.dal.dao.datasource.tomcat;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.configure.DalExtendedPoolConfiguration;
import com.ctrip.platform.dal.dao.datasource.ConnectionListener;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.cluster.validator.ConnectionValidator;
import com.ctrip.platform.dal.dao.datasource.cluster.DefaultHostConnection;
import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;
import com.ctrip.platform.dal.dao.helper.ConnectionUtils;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.LoggerHelper;
import com.ctrip.platform.dal.dao.helper.ServiceLoaderHelper;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.InvalidConnectionException;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PooledConnection;

import java.sql.*;

public class DalConnectionPool extends ConnectionPool {
    private static ILogger logger = DalElementFactory.DEFAULT.getILogger();
    private static ConnectionListener connectionListener = ServiceLoaderHelper.getInstance(ConnectionListener.class);
    private static ThreadLocal<Long> poolWaitTime = new ThreadLocal<>();

    private final ConnectionValidator clusterConnValidator;

    public DalConnectionPool(PoolConfiguration prop) throws SQLException {
        this(prop, null);
    }

    public DalConnectionPool(PoolConfiguration prop, ConnectionValidator clusterConnValidator) throws SQLException {
        super(prop);
        this.clusterConnValidator = clusterConnValidator;
    }

    @Override
    public Connection getConnection() throws SQLException {
        poolWaitTime.set(System.currentTimeMillis());
        return super.getConnection();
    }

    @Override
    protected PooledConnection borrowConnection(long now, PooledConnection con, String username, String password) throws SQLException {
        try {
            long waitTime = System.currentTimeMillis() - poolWaitTime.get();
            if (waitTime > 1) {
                connectionListener.onWaitConnection(getName(), getConnection(con), poolWaitTime.get());
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

        PoolConfiguration poolConfig = getPoolProperties();
        DataSourceIdentity dataSourceId = poolConfig instanceof DalExtendedPoolConfiguration ?
                ((DalExtendedPoolConfiguration) poolConfig).getDataSourceId() : null;

        try {
            pooledConnection = super.createConnection(now, notUsed, username, password);
        } catch (Throwable e) {
            String connectionUrl = LoggerHelper.getSimplifiedDBUrl(getPoolProperties().getUrl());
            connectionListener.onCreateConnectionFailed(getName(), connectionUrl, dataSourceId, e, startTime);
            throw e;
        }

        try {
            connectionListener.onCreateConnection(getName(), getConnection(pooledConnection), dataSourceId, startTime);
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
            tryValidateClusterConnection(conn);
            trySetSessionWaitTimeout(connection);
        }
    }

    private void tryValidateClusterConnection(PooledConnection conn) {
        if (clusterConnValidator != null) {
            boolean isValid = true;
            try {
                PoolConfiguration config = getPoolProperties();
                HostConnection connection;
                if (config instanceof DalExtendedPoolConfiguration)
                    connection = new DefaultHostConnection(getConnection(conn),
                            ((DalExtendedPoolConfiguration) config).getHost());
                else
                    connection = new DefaultHostConnection(getConnection(conn), null);
                isValid = clusterConnValidator.validate(connection);
            } catch (Throwable t) {
                logger.warn("tryValidateClusterConnection exception", t);
            }
            if (!isValid) {
                release(conn);
                throw new InvalidConnectionException("Created connection is invalid");
            }
        }
    }

    private void trySetSessionWaitTimeout(Connection conn) {
        PoolConfiguration config = getPoolProperties();
        if (config instanceof DalExtendedPoolConfiguration &&
                DatabaseCategory.MySql == DatabaseCategory.matchWithConnectionUrl(config.getUrl())) {
            int sessionWaitTimeout = ((DalExtendedPoolConfiguration) config).getSessionWaitTimeout();
            if (sessionWaitTimeout > 0) {
                String connUrl = ConnectionUtils.getConnectionUrl(conn, config.getUrl());
                String logName = String.format("Connection::setSessionWaitTimeout:%s", connUrl);
                try {
                    logger.logTransaction(DalLogTypes.DAL_DATASOURCE, logName,
                            String.format("sessionWaitTimeout: %ds. Connection url: %s",
                                    sessionWaitTimeout, config.getUrl()),
                            () -> setSessionWaitTimeout(conn, sessionWaitTimeout));
                } catch (Throwable t) {
                    logger.error("set sessionWaitTimeout exception: " + connUrl, t);
                }
            }
        }
    }

    private void setSessionWaitTimeout(Connection conn, int sessionWaitTimeout) throws SQLException {
        boolean autoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(true);
            try (Statement statement = conn.createStatement()) {
                statement.setQueryTimeout(1);
                statement.execute(String.format("set session wait_timeout = %d", sessionWaitTimeout));
                try (ResultSet rs = statement.executeQuery("show session variables like 'wait_timeout'")) {
                    if (rs != null && rs.next() && sessionWaitTimeout == rs.getInt(2))
                        logger.info(String.format("set sessionWaitTimeout to %ds succeeded: %s",
                                sessionWaitTimeout, getName()));
                    else
                        logger.warn("check sessionWaitTimeout failed: " + getName());
                } catch (Throwable t) {
                    logger.warn("check sessionWaitTimeout exception: " + getName(), t);
                }
            }
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }

}
