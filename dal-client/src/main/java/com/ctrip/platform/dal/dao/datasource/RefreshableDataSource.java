package com.ctrip.platform.dal.dao.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.sql.DataSource;
import javax.sql.PooledConnection;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeEvent;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeListener;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.mysql.jdbc.MySQLConnection;
import org.apache.commons.lang.StringUtils;

public class RefreshableDataSource implements DataSource, DataSourceConfigureChangeListener {
    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private AtomicReference<SingleDataSource> dataSourceReference = new AtomicReference<>();

    private AtomicReference<String> connectionIpReference = new AtomicReference<>();

    private CopyOnWriteArraySet<DataSourceSwitchListener> dataSourceSwitchListeners = new CopyOnWriteArraySet<>();

    private AtomicBoolean isListenerExecuted = new AtomicBoolean(false);

    private ScheduledExecutorService service =
            Executors.newScheduledThreadPool(POOL_SIZE, new CustomThreadFactory(THREAD_NAME));

    private static ThreadPoolExecutor executor;

    private static ExecutorService getConnExecutor = Executors.newSingleThreadExecutor();

    private static final int INIT_DELAY = 0;
    private static final int POOL_SIZE = 1;
    private static final String THREAD_NAME = "ConnectionPoolCreator";
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 1L;
    private static final long TIME_OUT = 1000; //ms

    public RefreshableDataSource(String name, DataSourceConfigure config) throws SQLException {
        SingleDataSource dataSource = new SingleDataSource(name, config);
        dataSourceReference.set(dataSource);
        asyncGetConnection();
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
        isListenerExecuted.set(false);
        SingleDataSource oldDataSource = dataSourceReference.getAndSet(newDataSource);
        asyncGetConnection();
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
                    isListenerExecuted.set(false);
                    SingleDataSource oldDataSource = dataSourceReference.getAndSet(newDataSource);
                    asyncGetConnection();
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

    private ScheduledExecutorService getExecutorService() {
        if (service == null) {
            synchronized (this) {
                if (service == null) {
                    service = Executors.newScheduledThreadPool(POOL_SIZE, new CustomThreadFactory(THREAD_NAME));
                }
            }
        }
        return service;
    }

    private void executeDataSourceListener(final String keyName) {
        synchronized (this) {
            if (isListenerExecuted.get()) {
                return;
            }
            final CountDownLatch latch = new CountDownLatch(dataSourceSwitchListeners.size());
            for (final DataSourceSwitchListener dataSourceSwitchListener : dataSourceSwitchListeners) {
                if (dataSourceSwitchListener != null) {
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                dataSourceSwitchListener.execute();
                                latch.countDown();
                            } catch (Throwable e) {
                                LOGGER.error(String.format("execute datasource switch listener fail for %s", keyName), e);
                            }
                        }
                    });
                }
            }
            try {
                latch.await(TIME_OUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                LOGGER.error(String.format("timeout,execute datasource switch listener is interrupted for %s", keyName), e);
            } finally {
                isListenerExecuted.set(true);
            }
        }
    }

    private void asyncGetConnection() {
        getConnExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    getConnection();
                } catch (SQLException e) {
                    //ignore
                }
            }
        });
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection =  getDataSource().getConnection();
        if (dataSourceSwitchListeners.size() > 0) {
            String currentIp = ((MySQLConnection)(((PooledConnection)connection).getConnection())).getIO().mysqlConnection.getInetAddress().getHostAddress();
            String oldIp = connectionIpReference.get();
            if (StringUtils.isEmpty(oldIp)) {
                connectionIpReference.set(currentIp);
            }
            else {
                if (!oldIp.equalsIgnoreCase(currentIp)) {
                    String keyName = getSingleDataSource().getName();
                    executeDataSourceListener(keyName);
                    connectionIpReference.set(currentIp);
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
