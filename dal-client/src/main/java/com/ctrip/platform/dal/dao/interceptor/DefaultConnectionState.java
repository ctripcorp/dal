package com.ctrip.platform.dal.dao.interceptor;

import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultConnectionState extends JdbcInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(DefaultConnectionState.class);

    private AtomicBoolean isFirstTime = new AtomicBoolean(true);

    @Override
    public void reset(ConnectionPool parent, PooledConnection con) {
        if (parent == null || con == null)
            return;

        try {
            boolean firstTime = isFirstTime.get();
            if (!firstTime)
                return;

            isFirstTime.set(false);
            String connectionName = con.toString();
            String url = con.getPoolProperties().getUrl();
            String poolName = parent.getName();
            String info = String.format("%s of url %s has been created for the first time,connection pool name:%s.",
                    connectionName, url, poolName);
            logger.info(info);
        } catch (Throwable e) {
        }
    }

    @Override
    public void disconnected(ConnectionPool parent, PooledConnection con, boolean finalizing) {
        if (parent == null || con == null)
            return;

        try {
            String connectionName = con.toString();
            String url = con.getPoolProperties().getUrl();
            String poolName = parent.getName();
            String info = String.format("%s of url %s has been destroyed,connection pool name:%s.", connectionName, url,
                    poolName);
            logger.info(info);
        } catch (Throwable e) {
        }
    }

}
