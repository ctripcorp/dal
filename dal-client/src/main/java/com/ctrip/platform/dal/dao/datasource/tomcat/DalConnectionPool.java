package com.ctrip.platform.dal.dao.datasource.tomcat;

import com.ctrip.platform.dal.dao.datasource.ConnectionListener;
import com.ctrip.platform.dal.dao.helper.ServiceLoaderHelper;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        PooledConnection pooledConnection = super.createConnection(now, notUsed, username, password);
        try {
            connectionListener.onCreateConnection(getName(),
                    pooledConnection == null ? null : pooledConnection.getConnection());
        } catch (Exception e) {
            logger.error("[createConnection]" + this, e);

        }
        return pooledConnection;
    }

    @Override
    protected void release(PooledConnection con) {

        try {
            connectionListener.onReleaseConnection(getName(), con == null ? null : con.getConnection());
        } catch (Exception e) {
            logger.error("[release]" + this, e);
        }
        super.release(con);
    }

    @Override
    protected void abandon(PooledConnection con) {
        try {
            connectionListener.onAbandonConnection(getName(), con == null ? null : con.getConnection());
        } catch (Exception e) {
            logger.error("[abandon]" + this, e);
        }

        super.abandon(con);
    }

    public static void setConnectionListener(ConnectionListener connectionListener) {
        DalConnectionPool.connectionListener = connectionListener;
    }

}
