package com.ctrip.datasource.interceptor;

import com.dianping.cat.Cat;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class CtripConnectionState extends JdbcInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(CtripConnectionState.class);
    private static final String DAL = "DAL";
    private static final String DAL_CREATE_CONNECTION = "DataSource::createConnection:%s";
    private static final String DAL_DESTROY_CONNECTION = "DataSource::destroyConnection:%s";
    private static final String QUESTION_MARK = "?";

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
            String temp = con.getPoolProperties().getUrl();
            String url = getUrl(temp);
            String poolName = parent.getName();
            String info = String.format("%s of url %s has been created for the first time,connection pool name:%s.",
                    connectionName, url, poolName);
            logger.info(info);
            Cat.logEvent(DAL, String.format(DAL_CREATE_CONNECTION, url));
        } catch (Throwable e) {
            String aaa = e.getMessage();
        }
    }

    @Override
    public void disconnected(ConnectionPool parent, PooledConnection con, boolean finalizing) {
        if (parent == null || con == null)
            return;

        try {
            String connectionName = con.toString();
            String temp = con.getPoolProperties().getUrl();
            String url = getUrl(temp);
            String poolName = parent.getName();
            String info = String.format("%s of url %s has been destroyed,connection pool name:%s.", connectionName, url,
                    poolName);
            logger.info(info);
            Cat.logEvent(DAL, String.format(DAL_DESTROY_CONNECTION, url));
        } catch (Throwable e) {
        }
    }

    private String getUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }

        int index = url.indexOf(QUESTION_MARK);
        if (index > -1) {
            return url.substring(0, index);
        }

        return url;
    }

}
