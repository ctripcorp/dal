package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataSourceTerminator {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceTerminator.class);
    private static volatile DataSourceTerminator terminator = null;
    private static final int INIT_DELAY = 0;
    private static final int DELAY = 5 * 1000; // milliseconds
    private static final int DEFAULT_INT_VALUE = 0;
    private static final int MAX_RETRY_TIMES = 3;
    private Map<String, Integer> retryTimesMap = new ConcurrentHashMap<>();

    public synchronized static DataSourceTerminator getInstance() {
        if (terminator == null) {
            terminator = new DataSourceTerminator();
            terminator.init();
        }

        return terminator;
    }

    private BlockingQueue<SingleDataSourceTask> taskQueue = new LinkedBlockingQueue<>();

    public synchronized void close(SingleDataSource dataSource) {
        if (dataSource != null) {
            SingleDataSourceTask task = new SingleDataSourceTask(dataSource, new Date());
            taskQueue.offer(task);
            logger.info(String.format("DataSource %s offered to DataSource destroy queue for the first time.",
                    dataSource.getName()));
        }
    }

    // executors
    private synchronized void init() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new DataSourceTerminateTask(), INIT_DELAY, DELAY, TimeUnit.MILLISECONDS);
    }

    class DataSourceTerminateTask implements Runnable {
        @Override
        public void run() {
            SingleDataSourceTask task = null;
            SingleDataSource dataSource = null;
            String name = "";
            try {
                task = taskQueue.take();
                if (task == null)
                    return;

                dataSource = task.getSingleDataSource();
                if (dataSource == null)
                    return;

                name = dataSource.getName();
                logger.info(String.format("DataSource %s has been taken from DataSource destroy queue.", name));

                boolean success = closeDataSource(task);
                if (!success) {
                    taskQueue.offer(task);
                    logger.info(
                            String.format("DataSource %s has been offered to DataSource destroy queue again.", name));
                }
            } catch (Throwable e) {
                logger.warn(String.format("Error occured while closing DataSource %s", name), e);
                taskQueue.offer(task);
                logger.info(String.format(
                        "DataSource %s has been offered to DataSource destroy queue again due to exception.", name));
            }
        }

        private boolean closeDataSource(SingleDataSourceTask task) throws Exception {
            SingleDataSource singleDataSource = task.getSingleDataSource();
            DataSource dataSource = singleDataSource.getDataSource();
            String name = singleDataSource.getName();
            logger.info(String.format("Trying to close DataSource %s", name));
            boolean success = true;

            try {
                // Tomcat DataSource
                if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
                    int retryTimes = task.getRetryTimes();
                    logger.info(String.format("Error retry times for DataSource %s:%s", name, retryTimes));

                    int abandonedTimeout = getAbandonedTimeout(singleDataSource);
                    logger.info(String.format("Abandoned timeout for DataSource %s:%s", name, abandonedTimeout));

                    int elapsedSeconds = getElapsedSeconds(task.getEnqueueTime());
                    logger.info(String.format("Elapsed seconds for DataSource %s:%s", name, elapsedSeconds));

                    org.apache.tomcat.jdbc.pool.DataSource ds = (org.apache.tomcat.jdbc.pool.DataSource) dataSource;
                    if (retryTimes > MAX_RETRY_TIMES) {
                        logger.info(String.format("Force closing DataSource %s,retry times:%s,max retry times:%s.",
                                name, retryTimes, MAX_RETRY_TIMES));
                        ds.close(true);
                        return success;
                    } else if (elapsedSeconds >= abandonedTimeout) {
                        logger.info(
                                String.format("Force closing DataSource %s,elapsed seconds:%s,abandoned timeout:%s.",
                                        name, elapsedSeconds, abandonedTimeout));
                        ds.close(true);
                        return success;
                    }

                    ConnectionPool pool = ds.getPool();
                    if (pool == null)
                        return success;

                    int idle = pool.getIdle();
                    if (idle > 0) {
                        pool.checkIdle();
                    }

                    int active = pool.getActive();
                    if (active == 0) {
                        ds.close();
                        logger.info(String.format("DataSource %s has been closed,idle connections are zero.", name));
                    } else if (active > 0) {
                        logger.info(String.format("Active connections of DataSource %s:%s.", name, active));
                        success = false;
                    }
                }
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
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
