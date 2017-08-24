package com.ctrip.platform.dal.dao.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import javax.sql.DataSource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeEvent;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeListener;

public class RefreshableDataSource
        implements DataSource, DataSourceConfigureChangeListener, DataSourceConfigureConstants {
    private static final Logger logger = LoggerFactory.getLogger(RefreshableDataSource.class);

    private String name;
    private AtomicReference<SingleDataSource> dataSourceRef = new AtomicReference<>();

    public RefreshableDataSource(String name, DataSourceConfigure config) throws SQLException {
        this.name = name;
        SingleDataSource dataSource = create(config);
        dataSourceRef.set(dataSource);
    }

    @Override
    public synchronized void configChanged(DataSourceConfigureChangeEvent event) throws SQLException {
        DataSourceConfigure newConfigure = event.getNewDataSourceConfigure();
        SingleDataSource newDataSource = create(newConfigure);
        SingleDataSource oldDataSource = dataSourceRef.getAndSet(newDataSource);
        close(oldDataSource);
    }

    private SingleDataSource create(DataSourceConfigure config) throws SQLException {
        if (config == null)
            throw new SQLException("Can not find any connection configure for " + name);

        PoolProperties p = convert(config);
        DataSourceLocator.setPoolProperties(p);
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(p);
        dataSource.createPool();
        logger.info("Datasource[name=" + name + ", Driver=" + p.getDriverClassName() + "] created.");
        return new SingleDataSource(name, config, dataSource, new Date());
    }

    private PoolProperties convert(DataSourceConfigure config) {
        PoolProperties properties = new PoolProperties();

        /**
         * It is assumed that user name/password/url/driver class name are provided in pool config If not, it should be
         * provided by the config provider
         */
        properties.setUrl(config.getConnectionUrl());
        properties.setUsername(config.getUserName());
        properties.setPassword(config.getPassword());
        properties.setDriverClassName(config.getDriverClass());

        properties.setTestWhileIdle(
                config.getBooleanProperty(TESTWHILEIDLE, DataSourceConfigureParser.DEFAULT_TESTWHILEIDLE));
        properties.setTestOnBorrow(
                config.getBooleanProperty(TESTONBORROW, DataSourceConfigureParser.DEFAULT_TESTONBORROW));
        properties.setTestOnReturn(
                config.getBooleanProperty(TESTONRETURN, DataSourceConfigureParser.DEFAULT_TESTONRETURN));

        properties.setValidationQuery(
                config.getProperty(VALIDATIONQUERY, DataSourceConfigureParser.DEFAULT_VALIDATIONQUERY));
        properties.setValidationQueryTimeout(config.getIntProperty(VALIDATIONQUERYTIMEOUT,
                DataSourceConfigureParser.DEFAULT_VALIDATIONQUERYTIMEOUT));
        properties.setValidationInterval(
                config.getLongProperty(VALIDATIONINTERVAL, DataSourceConfigureParser.DEFAULT_VALIDATIONINTERVAL));

        properties.setTimeBetweenEvictionRunsMillis(config.getIntProperty(TIMEBETWEENEVICTIONRUNSMILLIS,
                DataSourceConfigureParser.DEFAULT_TIMEBETWEENEVICTIONRUNSMILLIS));
        properties.setMinEvictableIdleTimeMillis(DataSourceConfigureParser.DEFAULT_MINEVICTABLEIDLETIMEMILLIS);

        properties.setMaxAge(config.getIntProperty(MAX_AGE, DataSourceConfigureParser.DEFAULT_MAXAGE));
        properties.setMaxActive(config.getIntProperty(MAXACTIVE, DataSourceConfigureParser.DEFAULT_MAXACTIVE));
        properties.setMinIdle(config.getIntProperty(MINIDLE, DataSourceConfigureParser.DEFAULT_MINIDLE));
        properties.setMaxWait(config.getIntProperty(MAXWAIT, DataSourceConfigureParser.DEFAULT_MAXWAIT));
        properties.setInitialSize(config.getIntProperty(INITIALSIZE, DataSourceConfigureParser.DEFAULT_INITIALSIZE));

        properties.setRemoveAbandonedTimeout(config.getIntProperty(REMOVEABANDONEDTIMEOUT,
                DataSourceConfigureParser.DEFAULT_REMOVEABANDONEDTIMEOUT));
        properties.setRemoveAbandoned(
                config.getBooleanProperty(REMOVEABANDONED, DataSourceConfigureParser.DEFAULT_REMOVEABANDONED));
        properties.setLogAbandoned(
                config.getBooleanProperty(LOGABANDONED, DataSourceConfigureParser.DEFAULT_LOGABANDONED));

        properties.setConnectionProperties(
                config.getProperty(CONNECTIONPROPERTIES, DataSourceConfigureParser.DEFAULT_CONNECTIONPROPERTIES));
        properties.setValidatorClassName(
                config.getProperty(VALIDATORCLASSNAME, DataSourceConfigureParser.DEFAULT_VALIDATORCLASSNAME));

        // This are current hard coded as default value
        properties.setJmxEnabled(DataSourceConfigureParser.DEFAULT_JMXENABLED);
        properties.setJdbcInterceptors(DataSourceConfigureParser.DEFAULT_JDBCINTERCEPTORS);

        return properties;
    }

    private void close(SingleDataSource dataSource) {
        DataSourceTerminator.getInstance().close(dataSource);
    }

    private DataSource getDataSource() {
        return dataSourceRef.get().getDataSource();
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
