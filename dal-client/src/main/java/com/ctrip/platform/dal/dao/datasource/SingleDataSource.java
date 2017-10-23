package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class SingleDataSource implements DataSourceConfigureConstants {
    private static final Logger logger = LoggerFactory.getLogger(SingleDataSource.class);

    private String name;
    private DataSourceConfigure dataSourceConfigure;
    private DataSource dataSource;
    private Date enqueueTime;

    public String getName() {
        return name;
    }

    public DataSourceConfigure getDataSourceConfigure() {
        return dataSourceConfigure;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setEnqueueTime(Date enqueueTime) {
        this.enqueueTime = enqueueTime;
    }

    public Date getEnqueueTime() {
        return enqueueTime;
    }

    public SingleDataSource(String name, DataSourceConfigure dataSourceConfigure) throws SQLException {
        if (dataSourceConfigure == null)
            throw new SQLException("Can not find any connection configure for " + name);

        try {
            this.name = name;
            this.dataSourceConfigure = dataSourceConfigure;

            PoolProperties p = convert(dataSourceConfigure);
            PoolPropertiesHolder.getInstance().setPoolProperties(p);
            org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(p);
            this.dataSource = dataSource;

            dataSource.createPool();
            logger.info("Datasource[name=" + name + ", Driver=" + p.getDriverClassName() + "] created.");
        } catch (Throwable e) {
            logger.error(String.format("Error creating pool for data source %s", name), e);
            // throw e;
        }
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

        properties.setTestWhileIdle(config.getBooleanProperty(TESTWHILEIDLE, DEFAULT_TESTWHILEIDLE));
        properties.setTestOnBorrow(config.getBooleanProperty(TESTONBORROW, DEFAULT_TESTONBORROW));
        properties.setTestOnReturn(config.getBooleanProperty(TESTONRETURN, DEFAULT_TESTONRETURN));

        properties.setValidationQuery(config.getProperty(VALIDATIONQUERY, DEFAULT_VALIDATIONQUERY));
        properties.setValidationQueryTimeout(
                config.getIntProperty(VALIDATIONQUERYTIMEOUT, DEFAULT_VALIDATIONQUERYTIMEOUT));
        properties.setValidationInterval(config.getLongProperty(VALIDATIONINTERVAL, DEFAULT_VALIDATIONINTERVAL));

        properties.setTimeBetweenEvictionRunsMillis(
                config.getIntProperty(TIMEBETWEENEVICTIONRUNSMILLIS, DEFAULT_TIMEBETWEENEVICTIONRUNSMILLIS));
        properties.setMinEvictableIdleTimeMillis(DEFAULT_MINEVICTABLEIDLETIMEMILLIS);

        properties.setMaxAge(config.getIntProperty(MAX_AGE, DEFAULT_MAXAGE));
        properties.setMaxActive(config.getIntProperty(MAXACTIVE, DEFAULT_MAXACTIVE));
        properties.setMinIdle(config.getIntProperty(MINIDLE, DEFAULT_MINIDLE));
        properties.setMaxWait(config.getIntProperty(MAXWAIT, DEFAULT_MAXWAIT));
        properties.setInitialSize(config.getIntProperty(INITIALSIZE, DEFAULT_INITIALSIZE));

        properties.setRemoveAbandonedTimeout(
                config.getIntProperty(REMOVEABANDONEDTIMEOUT, DEFAULT_REMOVEABANDONEDTIMEOUT));
        properties.setRemoveAbandoned(config.getBooleanProperty(REMOVEABANDONED, DEFAULT_REMOVEABANDONED));
        properties.setLogAbandoned(config.getBooleanProperty(LOGABANDONED, DEFAULT_LOGABANDONED));

        properties.setConnectionProperties(config.getProperty(CONNECTIONPROPERTIES, DEFAULT_CONNECTIONPROPERTIES));
        properties.setValidatorClassName(config.getProperty(VALIDATORCLASSNAME, DEFAULT_VALIDATORCLASSNAME));

        String initSQL = config.getProperty(INIT_SQL);
        if (initSQL != null && !initSQL.isEmpty())
            properties.setInitSQL(initSQL);

        String initSQL2 = config.getProperty(INIT_SQL2);
        if (initSQL2 != null && !initSQL2.isEmpty())
            properties.setInitSQL(initSQL2);

        // This are current hard coded as default value
        properties.setJmxEnabled(DEFAULT_JMXENABLED);
        properties.setJdbcInterceptors(DEFAULT_JDBCINTERCEPTORS);

        return properties;
    }

    private void testConnection(org.apache.tomcat.jdbc.pool.DataSource dataSource) throws SQLException {
        if (dataSource == null)
            return;

        Connection con = null;
        try {
            con = dataSource.getConnection();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (con != null)
                try {
                    con.close();
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                }
        }
    }

}
