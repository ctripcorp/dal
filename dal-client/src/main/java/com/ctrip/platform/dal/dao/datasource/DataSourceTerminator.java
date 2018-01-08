package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
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

    private BlockingQueue<SingleDataSource> dataSourceQueue = new LinkedBlockingQueue<>();

    public synchronized void close(SingleDataSource dataSource) {
        if (dataSource != null) {
            dataSource.setEnqueueTime(new Date());
            dataSourceQueue.offer(dataSource);
            logger.info(String.format("DataSource %s offered to DataSource destroy queue first time.",
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
            SingleDataSource dataSource = null;
            String name = "";
            try {
                dataSource = dataSourceQueue.take();
                name = dataSource.getName();
                logger.info(String.format("DataSource %s taken from DataSource destroy queue.", name));

                boolean success = closeDataSource(dataSource);
                if (!success) {
                    if (dataSource != null) {
                        dataSourceQueue.offer(dataSource);
                        logger.info(String.format(
                                "DataSource %s offered to DataSource destroy queue again due to failure.", name));
                    }
                }
            } catch (Throwable e) {
                logger.warn(String.format("Error occured while closing DataSource %s", name), e);
                if (dataSource != null) {
                    dataSourceQueue.offer(dataSource);
                    logger.info(String
                            .format("DataSource %s offered to DataSource destroy queue again due to exception.", name));
                }
            }
        }

        private boolean closeDataSource(SingleDataSource singleDataSource) throws Exception {
            DataSource dataSource = singleDataSource.getDataSource();
            String name = singleDataSource.getName();
            logger.info(String.format("Trying to close DataSource %s", name));
            boolean success = true;

            try {
                // Tomcat DataSource
                if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
                    org.apache.tomcat.jdbc.pool.DataSource ds = (org.apache.tomcat.jdbc.pool.DataSource) dataSource;
                    ds.close();

                    int retryTimes = getRetryTimes(singleDataSource.getName());
                    int abandonedTimeout = getAbandonedTimeout(singleDataSource);
                    int elapsedSeconds = getElapsedSeconds(singleDataSource.getEnqueueTime());
                    logger.info(String.format("Retry times for DataSource %s:%s", name, retryTimes));
                    logger.info(String.format("Abandoned timeout for DataSource %s:%s", name, abandonedTimeout));
                    logger.info(String.format("Elapsed seconds for DataSource %s:%s", name, elapsedSeconds));

                    if (ds.getActive() == 0) {
                        logger.info(String.format("Active connections of DataSource %s is zero.", name));
                        return success;
                    } else if (retryTimes > MAX_RETRY_TIMES) {
                        logger.info(String.format("Abandoned closing DataSource %s,retry times:%s,max retry times:%s.",
                                name, retryTimes, MAX_RETRY_TIMES));
                        return success;
                    } else if (elapsedSeconds >= abandonedTimeout) {
                        ds.close(true);
                        logger.info(
                                String.format("Force closing DataSource %s,elapsed seconds:%s,abandoned timeout:%s.",
                                        name, elapsedSeconds, abandonedTimeout));
                    } else {
                        success &= false;
                    }
                }
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
                addRetryTime(singleDataSource.getName());
                success &= false;
            }

            return success;
        }

        private int getRetryTimes(String name) {
            if (name == null || name.isEmpty())
                return DEFAULT_INT_VALUE;

            Integer retryTime = retryTimesMap.get(name);
            if (retryTime == null)
                return DEFAULT_INT_VALUE;

            return retryTime.intValue();
        }

        private void addRetryTime(String name) {
            if (name == null || name.isEmpty())
                return;

            if (!retryTimesMap.containsKey(name))
                retryTimesMap.put(name, DEFAULT_INT_VALUE);

            Integer value = retryTimesMap.get(name);
            value++;
            retryTimesMap.put(name, value);
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
