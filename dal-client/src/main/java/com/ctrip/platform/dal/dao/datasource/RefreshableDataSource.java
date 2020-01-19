package com.ctrip.platform.dal.dao.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import javax.sql.DataSource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeEvent;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeListener;
import com.ctrip.platform.dal.dao.datasource.jdbc.DalConnection;
import com.ctrip.platform.dal.dao.helper.ConnectionHelper;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;

public class RefreshableDataSource implements DataSource, ClosableDataSource, SingleDataSourceWrapper, DataSourceConfigureChangeListener {

    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private AtomicReference<SingleDataSource> dataSourceReference = new AtomicReference<>();

    private AtomicReference<String> dBServerReference = new AtomicReference<>();

    private CopyOnWriteArraySet<DataSourceSwitchListener> dataSourceSwitchListeners = new CopyOnWriteArraySet<>();

    private volatile ScheduledExecutorService service = null;

    private static volatile ThreadPoolExecutor executor;

    private volatile ScheduledExecutorService timer = null;

    private Map<Integer, DataSourceSwitchBlockThreads> waiters = new ConcurrentHashMap<>();
    private DataSourceIdentity id;

    private volatile long firstErrorTime = 0;
    private volatile long lastReportErrorTime = 0;

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
    private static final String THREAD_NAME = "DataSourceRefresher";
    public static final int PERMIT_ERROR_DURATION_TIME = 60; //second
    public static final int REPORT_ERROR_FREQUENCY = 30; //second

    public RefreshableDataSource(String name, DataSourceConfigure config) {
        this.id = new DataSourceName(name);
        SingleDataSource ds = createSingleDataSource(name, config);
        LOGGER.info(String.format("create RefreshableDataSource '%s', with SingleDataSource '%s' ref count [%d]", name, ds.getName(), ds.getReferenceCount()));
        dataSourceReference.set(ds);
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new CustomThreadFactory("DataSourceSwitchListener"));
        executor.allowCoreThreadTimeOut(true);
    }

    public RefreshableDataSource(DataSourceIdentity id, DataSourceConfigure config) {
        this.id = id;
        SingleDataSource ds = createSingleDataSource(id.getId(), config);
        LOGGER.info(String.format("create RefreshableDataSource '%s', with SingleDataSource '%s' ref count [%d]", id.getId(), ds.getName(), ds.getReferenceCount()));
        dataSourceReference.set(ds);
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
        refreshDataSource(name,configure,null);
    }

    @Override
    public void forceRefreshDataSource(String name, DataSourceConfigure configure) {
        forceRefreshDataSource(name,configure,null);
    }

    public void refreshDataSource(String name, DataSourceConfigure configure, DataSourceCreatePoolListener listener) {
        SingleDataSource newDataSource = asyncCreateSingleDataSource(name, configure, listener);
        refresh(newDataSource);
    }

    public void forceRefreshDataSource(String name, DataSourceConfigure configure, DataSourceCreatePoolListener listener) {
        SingleDataSource newDataSource = forceCreateSingleDataSource(name, configure, listener);
        refresh(newDataSource);
    }

    private void refresh(SingleDataSource newDataSource) {
        SingleDataSource oldDataSource = dataSourceReference.getAndSet(newDataSource);
        LOGGER.info(String.format("switch RefreshableDataSource '%s', with SingleDataSource '%s' ref count [%d]", id.getId(), newDataSource.getName(), newDataSource.getReferenceCount()));
        close(oldDataSource);
    }

    public void asyncRefreshDataSource(final String name, final DataSourceConfigure configure, final DataSourceCreatePoolListener listener) throws SQLException {
        getExecutorService().schedule(new Runnable() {
            @Override
            public void run() {
                SingleDataSource newDataSource = createSingleDataSource(name, configure, listener);
                boolean isSuccess = newDataSource.createPool();
                if (isSuccess) {
                    refresh(newDataSource);
                    listener.onCreatePoolSuccess();
                }
            }
        }, INIT_DELAY, TimeUnit.MILLISECONDS);
    }

    public void handleException(SQLException e) throws SQLException {
        if (e != null) {
            long nowTime = System.currentTimeMillis();
            if (firstErrorTime == 0) {
                firstErrorTime = nowTime;
            }
            else {
                long duration = nowTime - firstErrorTime;
                if (duration > PERMIT_ERROR_DURATION_TIME * 1000) {
                    if (lastReportErrorTime == 0 || nowTime - lastReportErrorTime > REPORT_ERROR_FREQUENCY * 1000) {
                        LOGGER.reportError(getSingleDataSource().getName());
                        lastReportErrorTime = nowTime;
                    }
                }
            }
            throw e;
        }
        else {
            firstErrorTime = 0;
            lastReportErrorTime = 0;
        }
    }

    @Override
    public void close() {
        LOGGER.info(String.format("close RefreshableDataSource '%s'", id.getId()));
        SingleDataSource ds = dataSourceReference.get();
        if (ds != null) {
            close(ds);
        }
    }

    private void close(SingleDataSource oldDataSource) {
        DataSourceCreator.getInstance().returnDataSource(oldDataSource);
    }

    private SingleDataSource createSingleDataSource(String name, DataSourceConfigure configure) {
        return DataSourceCreator.getInstance().getOrCreateDataSource(name, configure);
    }

    private SingleDataSource createSingleDataSource(String name, DataSourceConfigure configure, DataSourceCreatePoolListener listener) {
        return DataSourceCreator.getInstance().getOrCreateDataSourceWithoutPool(name, configure, listener);
    }

    private SingleDataSource asyncCreateSingleDataSource(String name, DataSourceConfigure configure, DataSourceCreatePoolListener listener) {
        return DataSourceCreator.getInstance().getOrAsyncCreateDataSourceWithPool(name, configure, listener);
    }

    private SingleDataSource forceCreateSingleDataSource(String name, DataSourceConfigure configure, DataSourceCreatePoolListener listener) {
        return DataSourceCreator.getInstance().forceCreateSingleDataSource(name, configure, listener);
    }

    @Override
    public SingleDataSource getSingleDataSource() {
        return dataSourceReference.get();
    }

    public DataSourceIdentity getId() {
        return id;
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
            if (dBServerReference.compareAndSet(null, currentServer)) {
                return new DalConnection(connection, this);
            }
            int currentSwitchVersion;
            synchronized (this) {
                if (currentServer != null) {
                    String oldServer = dBServerReference.getAndSet(currentServer);
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
        return new DalConnection(connection, this);
    }

    public long getFirstErrorTime() {
        return firstErrorTime;
    }

    public long getLastReportErrorTime() {
        return lastReportErrorTime;
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
