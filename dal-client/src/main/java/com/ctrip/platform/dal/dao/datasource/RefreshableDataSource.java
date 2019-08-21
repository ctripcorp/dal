package com.ctrip.platform.dal.dao.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import javax.sql.DataSource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeEvent;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeListener;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;

public class RefreshableDataSource implements DataSource, DataSourceConfigureChangeListener {
    private AtomicReference<SingleDataSource> dataSourceReference = new AtomicReference<>();

    private ScheduledExecutorService service =
            Executors.newScheduledThreadPool(POOL_SIZE, new CustomThreadFactory(THREAD_NAME));

    private static final int INIT_DELAY = 0;
    private static final int POOL_SIZE = 1;
    private static final String THREAD_NAME = "ConnectionPoolCreator";

    public RefreshableDataSource(String name, DataSourceConfigure config) throws SQLException {
        SingleDataSource dataSource = new SingleDataSource(name, config);
        dataSourceReference.set(dataSource);
    }

    @Override
    public synchronized void configChanged(DataSourceConfigureChangeEvent event) throws SQLException {
        String name = event.getName();
        DataSourceConfigure newConfigure = event.getNewDataSourceConfigure();
        refreshDataSource(name, newConfigure);
    }

    public void refreshDataSource(String name, DataSourceConfigure configure) throws SQLException {
        SingleDataSource newDataSource = createSingleDataSource(name, configure, null);
        service.schedule(newDataSource.getTask(), INIT_DELAY, TimeUnit.MILLISECONDS);
        SingleDataSource oldDataSource = dataSourceReference.getAndSet(newDataSource);
        close(oldDataSource);
        DataSourceCreateTask oldTask = oldDataSource.getTask();
        if (oldTask != null)
            oldTask.cancel();
    }

    public void refreshDataSource(final String name, final DataSourceConfigure configure, final DataSourceCreatePoolListener listener) throws SQLException {
        service.schedule(new Runnable() {
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

    private DataSource getDataSource() {
        SingleDataSource singleDataSource = getSingleDataSource();
        if (singleDataSource == null)
            throw new IllegalStateException("SingleDataSource can't be null.");

        DataSource dataSource = singleDataSource.getDataSource();
        if (dataSource == null)
            throw new IllegalStateException("DataSource can't be null.");

        return dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
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
