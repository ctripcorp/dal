package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.factory.CustomThreadFactory;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DataSourceTerminator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceTerminator.class);
    private static volatile DataSourceTerminator terminator = null;
    private static volatile ScheduledExecutorService service = null;
    private static final int INIT_DELAY = 0;
    private static final int FIXED_DELAY = 5 * 1000; // milliseconds
    private static final int MAX_RETRY_TIMES = 3;
    private static final String THREAD_NAME = "DataSourceTerminator";

    public synchronized static DataSourceTerminator getInstance() {
        if (terminator == null) {
            terminator = new DataSourceTerminator();
            service = Executors.newSingleThreadScheduledExecutor(new CustomThreadFactory(THREAD_NAME));
        }

        return terminator;
    }

    public synchronized void close(final SingleDataSource dataSource) {
        if (dataSource == null)
            return;

        boolean success;
        int retryTimes = 0;
        int delay = INIT_DELAY;

        final String name = dataSource.getName();
        LOGGER.info(String.format(
                "Datasource %s has been scheduled to datasource destroy thread pool for the first time.", name));

        Date enqueueTime = new Date();

        do {
            ScheduledFuture<SingleDataSourceTask> future = service.schedule(
                    new DataSourceTerminateTask(dataSource, enqueueTime, retryTimes), delay, TimeUnit.MILLISECONDS);
            try {
                SingleDataSourceTask task = future.get();
                retryTimes = task.getRetryTimes();
                success = task.getExecuteResult();
            } catch (Throwable e) {
                success = false;
            }

            delay = FIXED_DELAY;
        } while (!success);
    }

    class DataSourceTerminateTask implements Callable<SingleDataSourceTask> {
        private SingleDataSource dataSource;
        private Date enqueueTime;
        private int retryTimes;

        public DataSourceTerminateTask(SingleDataSource dataSource, Date enqueueTime, int retryTimes) {
            this.dataSource = dataSource;
            this.enqueueTime = enqueueTime;
            this.retryTimes = retryTimes;
        }

        @Override
        public SingleDataSourceTask call() throws Exception {
            SingleDataSourceTask task = new SingleDataSourceTask(dataSource, enqueueTime, retryTimes);
            boolean result = closeDataSource(task);
            task.setExecuteResult(result);
            return task;
        }

        private boolean closeDataSource(SingleDataSourceTask task) throws Exception {
            SingleDataSource singleDataSource = task.getSingleDataSource();
            DataSource dataSource = singleDataSource.getDataSource();
            String name = singleDataSource.getName();
            LOGGER.info(String.format("Trying to close datasource %s", name));
            boolean success = true;

            try {
                // Tomcat DataSource
                if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
                    int retryTimes = task.getRetryTimes();
                    LOGGER.info(String.format("Error retry times for datasource %s:%s", name, retryTimes));

                    int abandonedTimeout = getAbandonedTimeout(singleDataSource);
                    LOGGER.info(String.format("Abandoned timeout for datasource %s:%s", name, abandonedTimeout));

                    int elapsedSeconds = getElapsedSeconds(task.getEnqueueTime());
                    LOGGER.info(String.format("Elapsed seconds for datasource %s:%s", name, elapsedSeconds));

                    org.apache.tomcat.jdbc.pool.DataSource ds = (org.apache.tomcat.jdbc.pool.DataSource) dataSource;
                    if (retryTimes > MAX_RETRY_TIMES) {
                        LOGGER.info(String.format("Force closing datasource %s,retry times:%s,max retry times:%s.",
                                name, retryTimes, MAX_RETRY_TIMES));
                        ds.close(true);
                        return success;
                    } else if (elapsedSeconds >= abandonedTimeout) {
                        LOGGER.info(
                                String.format("Force closing datasource %s,elapsed seconds:%s,abandoned timeout:%s.",
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
                        LOGGER.info(String.format(
                                "Active connections of datasource %s is zero, datasource has been closed.", name));
                    } else if (active > 0) {
                        LOGGER.info(String.format("Active connections of datasource %s is %s.", name, active));
                        success = false;
                    }
                }
            } catch (Throwable e) {
                LOGGER.warn(e.getMessage(), e);
                addRetryTimes(task);
                success = false;
            }

            return success;
        }

        private void addRetryTimes(SingleDataSourceTask task) {
            int retryTimes = task.getRetryTimes();
            retryTimes++;
            task.setRetryTimes(retryTimes);
        }

        private int getAbandonedTimeout(SingleDataSource singleDataSource) {
            DataSourceConfigure configure = singleDataSource.getDataSourceConfigure();
            return configure.getIntProperty(DataSourceConfigureConstants.REMOVEABANDONEDTIMEOUT,
                    DataSourceConfigureConstants.DEFAULT_REMOVEABANDONEDTIMEOUT);
        }

        private int getElapsedSeconds(Date time) {
            long elapsed = new Date().getTime() - time.getTime();
            return (int) (elapsed / 1000);
        }

    }
}
