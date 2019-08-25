package com.ctrip.platform.dal.dao.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.atomic.AtomicReference;

import javax.sql.DataSource;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeEvent;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeListener;

public class RefreshableDataSource implements DataSource, DataSourceConfigureChangeListener {

    private AtomicReference<SingleDataSource> dataSourceReference = new AtomicReference<>();

    public RefreshableDataSource(String name, DataSourceConfigure config) {
        SingleDataSource ds = createSingleDataSource(name, config);
        init(ds);
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

    public void forceRefreshDataSource(String name, DataSourceConfigure configure) throws SQLException {
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

    private void init(SingleDataSource newDataSource) {
        newDataSource.register();
        dataSourceReference.set(newDataSource);
    }

    private void refresh(SingleDataSource newDataSource) {
        newDataSource.register();
        SingleDataSource oldDataSource = dataSourceReference.getAndSet(newDataSource);
        close(oldDataSource);
    }

    private void close(SingleDataSource oldDataSource) {
        if (oldDataSource != null && oldDataSource.unRegister() <= 0) {
            DataSourceTerminator.getInstance().close(oldDataSource);
            oldDataSource.cancelTask();
        }
    }

    private SingleDataSource createSingleDataSource(String name, DataSourceConfigure configure) {
        return DataSourceCreator.getInstance().getOrCreateSingleDataSource(name, configure);
    }

    private SingleDataSource asyncCreateSingleDataSource(String name, DataSourceConfigure configure, DataSourceCreatePoolListener listener) {
        return DataSourceCreator.getInstance().getOrCreateSingleDataSource(name, configure, listener);
    }

    private SingleDataSource forceCreateSingleDataSource(String name, DataSourceConfigure configure, DataSourceCreatePoolListener listener) {
        return DataSourceCreator.getInstance().forceCreateSingleDataSource(name, configure, listener);
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
