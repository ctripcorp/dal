package com.ctrip.datasource.interceptor;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public class CtripConnectionState extends JdbcInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(CtripConnectionState.class);
    private static final String DAL = "DAL";
    private static final String DAL_CREATE_CONNECTION = "DataSource::createConnection";
    private static final String DAL_DESTROY_CONNECTION = "DataSource::destroyConnection";

    private AtomicReference<Boolean> isFirstTime = new AtomicReference<>(true);

    @Override
    public void reset(ConnectionPool parent, PooledConnection con) {
        if (parent == null || con == null)
            return;

        try {
            boolean firstTime = isFirstTime.get().booleanValue();
            if (!firstTime)
                return;

            isFirstTime.set(false);
            String connectionName = con.toString();
            String url = con.getPoolProperties().getUrl();
            String poolName = parent.getName();
            String info = String.format("%s of url %s has been created for the first time,connection pool name:%s.",
                    connectionName, url, poolName);
            logger.info(info);
            Cat.logEvent(DAL, DAL_CREATE_CONNECTION, Message.SUCCESS, info);
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
            Cat.logEvent(DAL, DAL_DESTROY_CONNECTION, Message.SUCCESS, info);
        } catch (Throwable e) {
        }
    }

}
