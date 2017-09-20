package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataSourceTerminator {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceTerminator.class);
    private static volatile DataSourceTerminator terminator = null;
    private static final int INIT_DELAY = 0;
    private static final int DELAY = 5 * 1000; // milliseconds

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
            try {
                dataSource = dataSourceQueue.take();
                boolean success = closeDataSource(dataSource);
                if (!success) {
                    if (dataSource != null) {
                        dataSourceQueue.offer(dataSource);
                    }
                }
            } catch (Throwable e) {
                if (dataSource != null) {
                    dataSourceQueue.offer(dataSource);
                }
            }
        }

        private boolean closeDataSource(SingleDataSource singleDataSource) throws Exception {
            DataSource dataSource = singleDataSource.getDataSource();
            boolean success = true;

            try {
                // Tomcat DataSource
                if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
                    org.apache.tomcat.jdbc.pool.DataSource ds = (org.apache.tomcat.jdbc.pool.DataSource) dataSource;
                    String name = ds.getName();
                    ds.close();

                    int abandonedTimeout = getAbandonedTimeout(singleDataSource);
                    int elapsedSeconds = getElapsedSeconds(singleDataSource.getEnqueueTime());
                    if (ds.getActive() == 0 || elapsedSeconds >= abandonedTimeout) {
                        ds.close(true);
                        logger.info(String.format("DataSource %s closed.", name));
                    } else {
                        success &= false;
                    }
                }
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
                success &= false;
            }

            return success;
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
