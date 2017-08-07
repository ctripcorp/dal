package com.ctrip.platform.dal.dao.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.atomic.AtomicReference;

import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeListener;
import com.ctrip.platform.dal.dao.configure.DatabasePoolConfigConstants;
import com.ctrip.platform.dal.dao.configure.DatabasePoolConfigParser;

public class RefreshableDataSource implements DataSource, DataSourceConfigureChangeListener, DatabasePoolConfigConstants {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceLocator.class);
    
    private String name;
    private AtomicReference<DataSource> dataSourceRef = new AtomicReference<>();
    
    public RefreshableDataSource(String name, DataSourceConfigure config) throws SQLException {
        this.name = name;
        dataSourceRef.set(create(config));
    }
        
    @Override
    public synchronized void configChanged(DataSourceConfigure config) throws SQLException {
        DataSource newDS = create(config);
        DataSource oldDS = dataSourceRef.getAndSet(newDS);
        shutdown(oldDS);
    }
    
    private DataSource create(DataSourceConfigure config) throws SQLException {
        if (config == null)
            throw new SQLException("Can not find any connection configure for " + name);
        
        PoolProperties p = convert(config);

        DataSourceLocator.setPoolProperties(p);

        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource(p);

        ds.createPool();

        logger.info("Datasource[name=" + name + ", Driver=" + p.getDriverClassName() + "] created.");

        return ds;
    }
    
    private PoolProperties convert(DataSourceConfigure config) {
        PoolProperties poolProperties = new PoolProperties();
        
        /**
         * It is assumed that user name/password/url/driver class name are provided in pool config If not, it should be
         * provided by the config provider
         */
        poolProperties.setUrl(config.getConnectionUrl());
        poolProperties.setUsername(config.getUserName());
        poolProperties.setPassword(config.getPassword());
        poolProperties.setDriverClassName(config.getDriverClass());
        
        poolProperties.setTestWhileIdle(config.getBooleanProperty(TESTWHILEIDLE, DatabasePoolConfigParser.DEFAULT_TESTWHILEIDLE));
        poolProperties.setTestOnBorrow(config.getBooleanProperty(TESTONBORROW, DatabasePoolConfigParser.DEFAULT_TESTONBORROW));
        poolProperties.setTestOnReturn(config.getBooleanProperty(TESTONRETURN, DatabasePoolConfigParser.DEFAULT_TESTONRETURN));
        
        poolProperties.setValidationQuery(config.getProperty(VALIDATIONQUERY, DatabasePoolConfigParser.DEFAULT_VALIDATIONQUERY));
        poolProperties.setValidationQueryTimeout(config.getIntProperty(VALIDATIONQUERYTIMEOUT, DatabasePoolConfigParser.DEFAULT_VALIDATIONQUERYTIMEOUT));
        poolProperties.setValidationInterval(config.getLongProperty(VALIDATIONINTERVAL, DatabasePoolConfigParser.DEFAULT_VALIDATIONINTERVAL));
        
        poolProperties.setTimeBetweenEvictionRunsMillis(config.getIntProperty(TIMEBETWEENEVICTIONRUNSMILLIS, DatabasePoolConfigParser.DEFAULT_TIMEBETWEENEVICTIONRUNSMILLIS));
        
        poolProperties.setMaxActive(config.getIntProperty(MAXACTIVE, DatabasePoolConfigParser.DEFAULT_MAXACTIVE));
        poolProperties.setMinIdle(config.getIntProperty(MINIDLE, DatabasePoolConfigParser.DEFAULT_MINIDLE));
        poolProperties.setMaxWait(config.getIntProperty(MAXWAIT, DatabasePoolConfigParser.DEFAULT_MAXWAIT));
        poolProperties.setMaxAge(config.getIntProperty(MAX_AGE, DatabasePoolConfigParser.DEFAULT_MAXAGE));
        poolProperties.setInitialSize(config.getIntProperty(INITIALSIZE, DatabasePoolConfigParser.DEFAULT_INITIALSIZE));
        
        poolProperties.setRemoveAbandonedTimeout(config.getIntProperty(REMOVEABANDONEDTIMEOUT, DatabasePoolConfigParser.DEFAULT_REMOVEABANDONEDTIMEOUT));
        poolProperties.setRemoveAbandoned(config.getBooleanProperty(REMOVEABANDONED, DatabasePoolConfigParser.DEFAULT_REMOVEABANDONED));
        poolProperties.setLogAbandoned(config.getBooleanProperty(LOGABANDONED, DatabasePoolConfigParser.DEFAULT_LOGABANDONED));
        
        poolProperties.setMinEvictableIdleTimeMillis(DatabasePoolConfigParser.DEFAULT_MINEVICTABLEIDLETIMEMILLIS);
        poolProperties.setConnectionProperties(config.getProperty(CONNECTIONPROPERTIES, DatabasePoolConfigParser.DEFAULT_CONNECTIONPROPERTIES));
        
        poolProperties.setValidatorClassName(config.getProperty(VALIDATORCLASSNAME, DatabasePoolConfigParser.DEFAULT_VALIDATORCLASSNAME));
        
        // This are current hard coded as default value
        poolProperties.setJmxEnabled(DatabasePoolConfigParser.DEFAULT_JMXENABLED);
        poolProperties.setJdbcInterceptors(DatabasePoolConfigParser.DEFAULT_JDBCINTERCEPTORS);
        
        return poolProperties;
    }
    
    private void shutdown(DataSource toBeClosed) {
        //TO BE closed when no lent connection out there
    }

    private DataSource getDS() {
        return dataSourceRef.get();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getDS().getConnection();
    }

    @Override
    public Connection getConnection(String paramString1, String paramString2) throws SQLException {
        return getDS().getConnection(paramString1, paramString2);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return getDS().getLogWriter();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return getDS().getLoginTimeout();
    }

    @Override
    public void setLogWriter(PrintWriter paramPrintWriter) throws SQLException {
        getDS().setLogWriter(paramPrintWriter);
    }

    @Override
    public void setLoginTimeout(int paramInt) throws SQLException {
        getDS().setLoginTimeout(paramInt);
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return getDS().getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getDS().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getDS().isWrapperFor(iface);
    }
}
