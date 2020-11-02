package com.ctrip.platform.dal.dao.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import javax.sql.DataSource;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeEvent;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeListener;
import com.ctrip.platform.dal.dao.datasource.jdbc.DalConnection;
import com.ctrip.platform.dal.dao.datasource.jdbc.DalDataSource;
import com.ctrip.platform.dal.dao.datasource.log.SqlContext;
import com.ctrip.platform.dal.dao.helper.ConnectionUtils;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;

public class RefreshableDataSource extends DalDataSource implements DataSource,
        ClosableDataSource, SingleDataSourceWrapper, DataSourceConfigureChangeListener {

    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private AtomicReference<SingleDataSource> dataSourceReference = new AtomicReference<>();

    private AtomicReference<String> dBServerReference = new AtomicReference<>();

    private CopyOnWriteArraySet<DataSourceSwitchListener> dataSourceSwitchListeners = new CopyOnWriteArraySet<>();

    private volatile ScheduledExecutorService service = null;

    private static volatile ThreadPoolExecutor listenersExecutor = null;

    private static volatile ThreadPoolExecutor listenerExecutor = null;

    private volatile ScheduledExecutorService timer = null;

    private Map<Integer, DataSourceSwitchBlockThreads> waiters = new ConcurrentHashMap<>();
    private final DataSourceIdentity id;
    private long switchListenerTimeout = DEFAULT_SWITCH_LISTENER_TIME_OUT; //ms

    private int switchVersion = 0;

    private static final int INIT_DELAY = 0;
    private static final int POOL_SIZE = 1;
    private static final String THREAD_NAME_POOL = "ConnectionPoolCreator";
    private static final String THREAD_NAME_TIMER = "TimerGetConnection";
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 1L;
    private static final long DEFAULT_SWITCH_LISTENER_TIME_OUT = 10; //ms
    private static final long MAX_SWITCH_LISTENER_TIME_OUT = 500; //ms
    private static final long FIXED_DELAY = 60; //second
    private static final String SWITCH_VERSION = "SwitchVersion:%s";
    private static final String LISTENER_TIME_OUT = "SwitchListenerTimeout:%s";
    private static final String BLOCK_CONNECTION = "Connection::blockConnection:%s";

    public RefreshableDataSource(String name, DataSourceConfigure config) {
        this.id = new DataSourceName(name);
        SingleDataSource ds = createSingleDataSource(name, config);
        LOGGER.info(String.format("create RefreshableDataSource '%s', with SingleDataSource '%s' ref count [%d]", name, ds.getName(), ds.getReferenceCount()));
        dataSourceReference.set(ds);
    }

    public RefreshableDataSource(DataSourceIdentity id, DataSourceConfigure config) {
        this.id = id;
        SingleDataSource ds = createSingleDataSource(id.getId(), config);
        LOGGER.info(String.format("create RefreshableDataSource '%s', with SingleDataSource '%s' ref count [%d]", id.getId(), ds.getName(), ds.getReferenceCount()));
        dataSourceReference.set(ds);
    }

    // for ForceSwitchableDataSourceAdapter
    protected RefreshableDataSource() {
        id = null;
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

    @Override
    protected SqlContext createSqlContext() {
        return id.createSqlContext();
    }

    @Override
    public DatabaseCategory getDatabaseCategory() {
        return getSingleDataSource().getDataSourceConfigure().getDatabaseCategory();
    }

    @Override
    protected String getDataSourceName() {
        return id.getId();
    }

    @Override
    public DataSource getDelegated() {
        return getDataSource();
    }

    public void addDataSourceSwitchListener(DataSourceSwitchListener dataSourceSwitchListener) {
        this.dataSourceSwitchListeners.add(dataSourceSwitchListener);
        scheduledCheckDataSourceSwitch();
    }

    public void setDataSourceSwitchListenerTimeout(long switchListenerTimeout) {
        if (switchListenerTimeout <= 0) {
            this.switchListenerTimeout = DEFAULT_SWITCH_LISTENER_TIME_OUT;
        }
        else if (switchListenerTimeout > 500) {
            this.switchListenerTimeout = MAX_SWITCH_LISTENER_TIME_OUT;
        }
        else {
            this.switchListenerTimeout = switchListenerTimeout;
        }
    }

    public long getSwitchListenerTimeout() {
        return this.switchListenerTimeout;
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
                                // ignore
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

    private ThreadPoolExecutor getListenersExecutor() {
        if (listenersExecutor == null) {
            synchronized (this) {
                if (listenersExecutor == null) {
                    listenersExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<Runnable>(),
                            new CustomThreadFactory("DataSourceSwitchListenersExecutor"));
                    listenersExecutor.allowCoreThreadTimeOut(true);
                }
            }
        }
        return listenersExecutor;
    }

    private ThreadPoolExecutor getListenerExecutor() {
        if (listenerExecutor == null) {
            synchronized (this) {
                if (listenerExecutor == null) {
                    listenerExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<Runnable>(),
                            new CustomThreadFactory("DataSourceSwitchListenerExecutor"));
                    listenerExecutor.allowCoreThreadTimeOut(true);
                }
            }
        }
        return listenerExecutor;
    }

    private void executeDataSourceListener(int switchVersion) {
        final String keyName = getSingleDataSource().getName();
        final CountDownLatch latch = new CountDownLatch(dataSourceSwitchListeners.size());
        for (final DataSourceSwitchListener dataSourceSwitchListener : dataSourceSwitchListeners) {
            if (dataSourceSwitchListener != null) {
                getListenerExecutor().submit(new Runnable() {
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
            boolean isExecuteEnd = latch.await(getSwitchListenerTimeout(), TimeUnit.MILLISECONDS);
            if (!isExecuteEnd) {
                LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format(LISTENER_TIME_OUT, switchVersion), "timeout:" + getSwitchListenerTimeout());
            }
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
                return new DalConnection(connection, this, id.createSqlContext());
            }
            int currentSwitchVersion;
            synchronized (this) {
                if (currentServer != null) {
                    String oldServer = dBServerReference.getAndSet(currentServer);
                    if (!oldServer.equalsIgnoreCase(currentServer)) {
                        final int tempSwitchVersion = ++switchVersion;
                        LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format(SWITCH_VERSION, tempSwitchVersion), oldServer + " switch to " + currentServer);
                        waiters.put(tempSwitchVersion, new DataSourceSwitchBlockThreads());
                        getListenersExecutor().submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    executeDataSourceListener(tempSwitchVersion);
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
                    LockSupport.parkNanos(getSwitchListenerTimeout() * 1000000);
                    LOGGER.logTransaction(DalLogTypes.DAL_DATASOURCE, String.format(BLOCK_CONNECTION, ConnectionUtils.getConnectionUrl(connection)),
                            String.format(SWITCH_VERSION, currentSwitchVersion), startTime);
                }
            }
        }
        return new DalConnection(connection, this, id.createSqlContext());
    }

}
