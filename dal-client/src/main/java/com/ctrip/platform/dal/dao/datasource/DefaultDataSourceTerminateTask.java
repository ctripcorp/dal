package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import org.apache.commons.lang.time.StopWatch;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Date;

public class DefaultDataSourceTerminateTask implements DataSourceTerminateTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDataSourceTerminateTask.class);
    protected static final int FIXED_DELAY = 5 * 1000; // milliseconds
    private static final int MAX_RETRY_TIMES = 3;

    protected String name;
    protected DataSource dataSource;
    private DataSourceConfigure dataSourceConfigure;
    private Date enqueueTime;
    private int retryTimes;

    @Override
    public void init(SingleDataSource singleDataSource) {
        this.name = singleDataSource.getName();
        this.dataSource = singleDataSource.getDataSource();
        this.dataSourceConfigure = singleDataSource.getDataSourceConfigure();
        this.enqueueTime = new Date();
        this.retryTimes = 0;
    }

    @Override
    public void run() {
        StopWatch watch = new StopWatch();
        LOGGER.info(String.format("**********Start closing datasource %s.**********", name));
        watch.start();
        boolean success = false;
        while (!success) {
            try {
                success = closeDataSource(dataSource);
                if (success)
                    break;

                Thread.sleep(FIXED_DELAY);
            } catch (Throwable e) {
            }
        }

        watch.stop();
        LOGGER.info(String.format("**********End closing datasource %s,cost:%s ms.**********", name, watch.getTime()));
    }

    protected boolean closeDataSource(DataSource dataSource) throws Exception {
        LOGGER.info(String.format("Trying to close datasource %s", name));

        boolean success = true;

        try {
            // Tomcat DataSource
            if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
                LOGGER.info(String.format("Error retry times for datasource %s:%s", name, retryTimes));

                int abandonedTimeout = getAbandonedTimeout();
                LOGGER.info(String.format("Abandoned timeout for datasource %s:%s", name, abandonedTimeout));

                int elapsedSeconds = getElapsedSeconds();
                LOGGER.info(String.format("Elapsed seconds for datasource %s:%s", name, elapsedSeconds));

                org.apache.tomcat.jdbc.pool.DataSource ds = (org.apache.tomcat.jdbc.pool.DataSource) dataSource;
                if (retryTimes > MAX_RETRY_TIMES) {
                    LOGGER.info(String.format("Force closing datasource %s,retry times:%s,max retry times:%s.", name,
                            retryTimes, MAX_RETRY_TIMES));
                    ds.close(true);
                    return success;
                } else if (elapsedSeconds >= abandonedTimeout) {
                    LOGGER.info(String.format("Force closing datasource %s,elapsed seconds:%s,abandoned timeout:%s.",
                            name, elapsedSeconds, abandonedTimeout));
                    ds.close(true);
                    return success;
                }

                ConnectionPool pool = ds.getPool();
                if (pool == null)
                    return success;

                int idle = pool.getIdle();
                if (idle > 0) {
                    pool.purge();
                    LOGGER.info(String.format("Idle connections of datasource %s have been closed.", name));
                }

                int active = pool.getActive();
                if (active == 0) {
                    ds.close();
                    LOGGER.info(String
                            .format("Active connections of datasource %s is zero, datasource has been closed.", name));
                } else if (active > 0) {
                    LOGGER.info(String.format("Active connections of datasource %s is %s.", name, active));
                    success = false;
                }
            }
        } catch (Throwable e) {
            LOGGER.warn(e.getMessage(), e);
            retryTimes++;
            success = false;
        }

        return success;
    }

    private int getAbandonedTimeout() {
        return dataSourceConfigure.getIntProperty(DataSourceConfigureConstants.REMOVEABANDONEDTIMEOUT,
                DataSourceConfigureConstants.DEFAULT_REMOVEABANDONEDTIMEOUT);
    }

    private int getElapsedSeconds() {
        long elapsed = new Date().getTime() - enqueueTime.getTime();
        return (int) (elapsed / 1000);
    }

}
