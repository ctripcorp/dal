package com.ctrip.platform.dal.dao.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import javax.sql.DataSource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeEvent;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeListener;
import com.ctrip.platform.dal.dao.helper.ConnectionHelper;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;

public class RefreshableDataSource implements DataSource, DataSourceConfigureChangeListener {
    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private AtomicReference<SingleDataSource> dataSourceReference = new AtomicReference<>();

    private AtomicReference<String> DBServerReference = new AtomicReference<>();

    private CopyOnWriteArraySet<DataSourceSwitchListener> dataSourceSwitchListeners = new CopyOnWriteArraySet<>();

    private volatile ScheduledExecutorService service = null;

    private static volatile ThreadPoolExecutor executor;

    private volatile ScheduledExecutorService timer = null;

    private Map<Integer, DataSourceSwitchBlockThreads> waiters = new ConcurrentHashMap<>();

    private static int switchVersion = 0;

    private static final int INIT_DELAY = 0;
    private static final int POOL_SIZE = 1;
    private static final String THREAD_NAME_POOL = "ConnectionPoolCreator";
    private static final String THREAD_NAME_TIMER = "TimerGetConnection";
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 1L;
    private static final long TIME_OUT = 500; //ms
    private static final long FIXED_DELAY = 60;//second

    private static final String SWITCH_VERSION = "SwitchVersion:%s";
    private static final String BLOCK_CONNECTION = "Connection::blockConnection:%s";

    public RefreshableDataSource(String name, DataSourceConfigure config) throws SQLException {
        SingleDataSource dataSource = new SingleDataSource(name, config);
        dataSourceReference.set(dataSource);
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new CustomThreadFactory("DataSourceSwitchListener"));
        executor.allowCoreThreadTimeOut(true);
    }

    @Override
    public synchronized void configChanged(DataSourceConfigureChangeEvent event) throws SQLException {
        String name = event.getName();
        DataSourceConfigure newConfigure = event.getNewDataSourceConfigure();
        refreshDataSource(name, newConfigure);
    }

    public void refreshDataSource(String name, DataSourceConfigure configure) throws SQLException {
        SingleDataSource newDataSource = createSingleDataSource(name, configure, null);
        getExecutorService().schedule(newDataSource.getTask(), INIT_DELAY, TimeUnit.MILLISECONDS);
        SingleDataSource oldDataSource = dataSourceReference.getAndSet(newDataSource);
        close(oldDataSource);
        DataSourceCreateTask oldTask = oldDataSource.getTask();
        if (oldTask != null)
            oldTask.cancel();
    }

    public void refreshDataSource(final String name, final DataSourceConfigure configure, final DataSourceCreatePoolListener listener) throws SQLException {
        getExecutorService().schedule(new Runnable() {
            @Override
            public void run() {
                SingleDataSource newDataSource = createSingleDataSource(name, configure, listener);
                boolean isSuccess = newDataSource.createPool(name, configure);
                if (isSuccess) {
                    SingleDataSource oldDataSource = dataSourceReference.getAndSet(newDataSource);
                    listener.onCreatePoolSuccess();
                    close(oldDataSource);
                    DataSourceCreateTask oldTask = oldDataSource.getTask();
                    if (oldTask != null)
                        oldTask.cancel();
                }
            }
        }, INIT_DELAY, TimeUnit.MILLISECONDS);
    }

    private void close(SingleDataSource oldDataSource) {
        if (oldDataSource != null)
            DataSourceTerminator.getInstance().close(oldDataSource);
    }

    private SingleDataSource createSingleDataSource(String name, DataSourceConfigure configure, DataSourceCreatePoolListener listener) {
        return DataSourceCreator.getInstance().createSingleDataSource(name, configure, listener);
    }

    public SingleDataSource getSingleDataSource() {
        return dataSourceReference.get();
    }

    public void addDataSourceSwitchListener(DataSourceSwitchListener dataSourceSwitchListener) {
        this.dataSourceSwitchListeners.add(dataSourceSwitchListener);
        scheduledCheckDataSourceSwitch();
    }

    public DataSource getDataSource() {
        SingleDataSource singleDataSource = getSingleDataSource();
        if (singleDataSource == null)
            throw new IllegalStateException("SingleDataSource can't be null.");

        DataSource dataSource = singleDataSource.getDataSource();
        if (dataSource == null)
            throw new IllegalStateException("DataSource can't be null.");

        return dataSource;
    }

    private void scheduledCheckDataSourceSwitch() {
        if (timer == null) {
            synchronized (this) {
                if (timer == null) {
                    timer = Executors.newScheduledThreadPool(POOL_SIZE, new CustomThreadFactory(THREAD_NAME_TIMER));
                    timer.scheduleWithFixedDelay(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getConnection().close();
                            } catch (Exception e) {
                                //ignore
                            }
                        }
                    }, INIT_DELAY, FIXED_DELAY, TimeUnit.SECONDS);
                }
            }
        }
    }

    private ScheduledExecutorService getExecutorService() {
        if (service == null) {
            synchronized (this) {
                if (service == null) {
                    service = Executors.newScheduledThreadPool(POOL_SIZE, new CustomThreadFactory(THREAD_NAME_POOL));
                }
            }
        }
        return service;
    }

    private void executeDataSourceListener() {
        final String keyName = getSingleDataSource().getName();
        final CountDownLatch latch = new CountDownLatch(dataSourceSwitchListeners.size());
        for (final DataSourceSwitchListener dataSourceSwitchListener : dataSourceSwitchListeners) {
            if (dataSourceSwitchListener != null) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dataSourceSwitchListener.execute();
                        } catch (Throwable e) {
                            LOGGER.error(String.format("execute datasource switch listener fail for %s", keyName), e);
                        }
                        latch.countDown();
                    }
                });
            }
        }
        try {
            latch.await(TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOGGER.error(String.format("timeout,execute datasource switch listener is interrupted for %s", keyName), e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = getDataSource().getConnection();
        if (dataSourceSwitchListeners.size() > 0) {
            String currentServer = null;
            try {
                currentServer = DataSourceSwitchChecker.getDBServerName(connection, getSingleDataSource().getDataSourceConfigure());
            } catch (Throwable e) {
                LOGGER.warn(e);
            }
            if (DBServerReference.compareAndSet(null, currentServer)) {
                return connection;
            }
            int currentSwitchVersion;
            synchronized (this) {
                if (currentServer != null) {
                    String oldServer = DBServerReference.getAndSet(currentServer);
                    if (!oldServer.equalsIgnoreCase(currentServer)) {
                        final int tempSwitchVersion = ++switchVersion;
                        LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format(SWITCH_VERSION, tempSwitchVersion), oldServer + " switch to " + currentServer);
                        waiters.put(tempSwitchVersion, new DataSourceSwitchBlockThreads());
                        executor.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    executeDataSourceListener();
                                } catch (Throwable e) {
                                    //ignore
                                }
                                DataSourceSwitchBlockThreads blockThreads = waiters.get(tempSwitchVersion);
                                if (blockThreads != null) {
                                    blockThreads.setNeedBlock(false);
                                    Thread waiter;
                                    while ((waiter = blockThreads.getBlockThreads().poll()) != null) {
                                        LockSupport.unpark(waiter);
                                    }
                                    waiters.remove(tempSwitchVersion);
                                }
                            }
                        });
                    }
                }
                currentSwitchVersion = switchVersion;
            }
            DataSourceSwitchBlockThreads blockThreads = waiters.get(currentSwitchVersion);
            if (blockThreads != null) {
                if (blockThreads.isNeedBlock()) {
                    blockThreads.addBlockThread(Thread.currentThread());
                }
                if (blockThreads.isNeedBlock()) {
                    long startTime = System.currentTimeMillis();
                    LockSupport.parkNanos(TIME_OUT * 1000000);
                    LOGGER.logTransaction(DalLogTypes.DAL_DATASOURCE, String.format(BLOCK_CONNECTION, ConnectionHelper.obtainUrl(connection)),
                            String.format(SWITCH_VERSION, currentSwitchVersion), startTime);
                }
            }
        }
        return connection;
    }

    @Override
    public Connection getConnection(String paramString1, String paramString2) throws SQLException {
        return getDataSource().getConnection(paramString1, paramString2);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return getDataSource().getLogWriter();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return getDataSource().getLoginTimeout();
    }

    @Override
    public void setLogWriter(PrintWriter paramPrintWriter) throws SQLException {
        getDataSource().setLogWriter(paramPrintWriter);
    }

    @Override
    public void setLoginTimeout(int paramInt) throws SQLException {
        getDataSource().setLoginTimeout(paramInt);
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return getDataSource().getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getDataSource().isWrapperFor(iface);
    }
}
