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
        DataSourceConfigure configure = DataSourceConfigureParser.getInstance().getDataSourceConfigure(name);
        PoolProperties poolProperties = new PoolProperties();

        poolProperties.setTestWhileIdle(
                configure.getBooleanProperty(TESTWHILEIDLE, DataSourceConfigureParser.DEFAULT_TESTWHILEIDLE));
        poolProperties.setTestOnBorrow(
                configure.getBooleanProperty(TESTONBORROW, DataSourceConfigureParser.DEFAULT_TESTONBORROW));
        poolProperties.setTestOnReturn(
                configure.getBooleanProperty(TESTONRETURN, DataSourceConfigureParser.DEFAULT_TESTONRETURN));

        poolProperties.setValidationQuery(
                configure.getProperty(VALIDATIONQUERY, DataSourceConfigureParser.DEFAULT_VALIDATIONQUERY));
        poolProperties.setValidationQueryTimeout(configure.getIntProperty(VALIDATIONQUERYTIMEOUT,
                DataSourceConfigureParser.DEFAULT_VALIDATIONQUERYTIMEOUT));
        poolProperties.setValidationInterval(
                configure.getLongProperty(VALIDATIONINTERVAL, DataSourceConfigureParser.DEFAULT_VALIDATIONINTERVAL));

        poolProperties.setTimeBetweenEvictionRunsMillis(configure.getIntProperty(TIMEBETWEENEVICTIONRUNSMILLIS,
                DataSourceConfigureParser.DEFAULT_TIMEBETWEENEVICTIONRUNSMILLIS));
        poolProperties.setMinEvictableIdleTimeMillis(DataSourceConfigureParser.DEFAULT_MINEVICTABLEIDLETIMEMILLIS);

        poolProperties.setMaxAge(configure.getIntProperty(MAX_AGE, DataSourceConfigureParser.DEFAULT_MAXAGE));
        poolProperties.setMaxActive(configure.getIntProperty(MAXACTIVE, DataSourceConfigureParser.DEFAULT_MAXACTIVE));
        poolProperties.setMinIdle(configure.getIntProperty(MINIDLE, DataSourceConfigureParser.DEFAULT_MINIDLE));
        poolProperties.setMaxWait(configure.getIntProperty(MAXWAIT, DataSourceConfigureParser.DEFAULT_MAXWAIT));
        poolProperties
                .setInitialSize(configure.getIntProperty(INITIALSIZE, DataSourceConfigureParser.DEFAULT_INITIALSIZE));

        poolProperties.setRemoveAbandonedTimeout(configure.getIntProperty(REMOVEABANDONEDTIMEOUT,
                DataSourceConfigureParser.DEFAULT_REMOVEABANDONEDTIMEOUT));
        poolProperties.setRemoveAbandoned(
                configure.getBooleanProperty(REMOVEABANDONED, DataSourceConfigureParser.DEFAULT_REMOVEABANDONED));
        poolProperties.setLogAbandoned(
                configure.getBooleanProperty(LOGABANDONED, DataSourceConfigureParser.DEFAULT_LOGABANDONED));

        poolProperties.setConnectionProperties(
                configure.getProperty(CONNECTIONPROPERTIES, DataSourceConfigureParser.DEFAULT_CONNECTIONPROPERTIES));
        poolProperties.setValidatorClassName(
                configure.getProperty(VALIDATORCLASSNAME, DataSourceConfigureParser.DEFAULT_VALIDATORCLASSNAME));

        poolProperties.setJmxEnabled(DataSourceConfigureParser.DEFAULT_JMXENABLED);
        poolProperties.setJdbcInterceptors(DataSourceConfigureParser.DEFAULT_JDBCINTERCEPTORS);

        return new DatabasePoolConfig(poolProperties);
    }

}
