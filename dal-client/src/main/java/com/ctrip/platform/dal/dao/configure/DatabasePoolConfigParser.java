package com.ctrip.platform.dal.dao.configure;

import org.apache.tomcat.jdbc.pool.PoolProperties;

@Deprecated
public class DatabasePoolConfigParser implements DataSourceConfigureConstants {
    private static DatabasePoolConfigParser databasePoolConfigParser = null;

    public synchronized static DatabasePoolConfigParser getInstance() {
        if (databasePoolConfigParser == null) {
            databasePoolConfigParser = new DatabasePoolConfigParser();
        }
        return databasePoolConfigParser;
    }

    public DatabasePoolConfig getDatabasePoolConfig(String name) {
        DataSourceConfigure configure = DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure(name);
        PoolProperties poolProperties = new PoolProperties();

        poolProperties.setTestWhileIdle(configure.getBooleanProperty(TESTWHILEIDLE, DEFAULT_TESTWHILEIDLE));
        poolProperties.setTestOnBorrow(configure.getBooleanProperty(TESTONBORROW, DEFAULT_TESTONBORROW));
        poolProperties.setTestOnReturn(configure.getBooleanProperty(TESTONRETURN, DEFAULT_TESTONRETURN));

        poolProperties.setValidationQuery(configure.getProperty(VALIDATIONQUERY, DEFAULT_VALIDATIONQUERY));
        poolProperties.setValidationQueryTimeout(
                configure.getIntProperty(VALIDATIONQUERYTIMEOUT, DEFAULT_VALIDATIONQUERYTIMEOUT));
        poolProperties.setValidationInterval(configure.getLongProperty(VALIDATIONINTERVAL, DEFAULT_VALIDATIONINTERVAL));

        poolProperties.setTimeBetweenEvictionRunsMillis(
                configure.getIntProperty(TIMEBETWEENEVICTIONRUNSMILLIS, DEFAULT_TIMEBETWEENEVICTIONRUNSMILLIS));
        poolProperties.setMinEvictableIdleTimeMillis(DEFAULT_MINEVICTABLEIDLETIMEMILLIS);

        poolProperties.setMaxAge(configure.getIntProperty(MAX_AGE, DEFAULT_MAXAGE));
        poolProperties.setMaxActive(configure.getIntProperty(MAXACTIVE, DEFAULT_MAXACTIVE));
        poolProperties.setMinIdle(configure.getIntProperty(MINIDLE, DEFAULT_MINIDLE));
        poolProperties.setMaxWait(configure.getIntProperty(MAXWAIT, DEFAULT_MAXWAIT));
        poolProperties.setInitialSize(configure.getIntProperty(INITIALSIZE, DEFAULT_INITIALSIZE));

        poolProperties.setRemoveAbandonedTimeout(
                configure.getIntProperty(REMOVEABANDONEDTIMEOUT, DEFAULT_REMOVEABANDONEDTIMEOUT));
        poolProperties.setRemoveAbandoned(configure.getBooleanProperty(REMOVEABANDONED, DEFAULT_REMOVEABANDONED));
        poolProperties.setLogAbandoned(configure.getBooleanProperty(LOGABANDONED, DEFAULT_LOGABANDONED));

        poolProperties
                .setConnectionProperties(configure.getProperty(CONNECTIONPROPERTIES, DEFAULT_CONNECTIONPROPERTIES));
        poolProperties.setValidatorClassName(configure.getProperty(VALIDATORCLASSNAME, DEFAULT_VALIDATORCLASSNAME));

        poolProperties.setJmxEnabled(DEFAULT_JMXENABLED);
        poolProperties.setJdbcInterceptors(configure.getProperty(JDBC_INTERCEPTORS, DEFAULT_JDBCINTERCEPTORS));

        return new DatabasePoolConfig(poolProperties);
    }

}
