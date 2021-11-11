package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.configure.DalExtendedPoolProperties;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.util.Map;
import java.util.Properties;

public class PoolPropertiesHelper implements DataSourceConfigureConstants {
    private volatile static PoolPropertiesHelper helper = null;

    public synchronized static PoolPropertiesHelper getInstance() {
        if (helper == null) {
            helper = new PoolPropertiesHelper();
        }
        return helper;
    }

    public PoolProperties convert(DataSourceConfigure config) {
        DalExtendedPoolProperties properties = new DalExtendedPoolProperties();

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
        properties.setDefaultAutoCommit(config.getBooleanProperty(DEFAULT_AUTOCOMMIT, DEFAULT_AUTO_COMMIT));
        properties.setLogValidationErrors(config.getBooleanProperty(LOGVALIDATIONERRORS, DEFAULT_LOGVALIDATIONERRORS));

        properties.setValidationQuery(config.getProperty(VALIDATIONQUERY, DEFAULT_VALIDATIONQUERY));
        properties.setValidationQueryTimeout(config.getValidationQueryTimeout());
        properties.setValidationInterval(config.getLongProperty(VALIDATIONINTERVAL, DEFAULT_VALIDATIONINTERVAL));

        properties.setTimeBetweenEvictionRunsMillis(
                config.getIntProperty(TIMEBETWEENEVICTIONRUNSMILLIS, DEFAULT_TIMEBETWEENEVICTIONRUNSMILLIS));
        properties.setMinEvictableIdleTimeMillis(
                config.getIntProperty(MINEVICTABLEIDLETIMEMILLIS, DEFAULT_MINEVICTABLEIDLETIMEMILLIS));

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
        properties.setJdbcInterceptors(config.getProperty(JDBC_INTERCEPTORS, DEFAULT_JDBCINTERCEPTORS));

        properties.setSessionWaitTimeout(config.getSessionWaitTimeout());

        properties.setDataSourceId(config.getDataSourceId());
        properties.setHost(config.getHost());

        return properties;
    }

    public String propertiesToString(Properties properties) {
        String result = null;
        try {
            if (properties != null && !properties.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    sb.append(entry.getKey().toString() + "=" + entry.getValue().toString() + ",");
                }
                result = sb.substring(0, sb.length() - 1);
            }
        } catch (Throwable e) {
        }
        return result;
    }

    public String mapToString(Map<String, String> map) {
        String result = null;
        try {
            if (map != null && map.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    sb.append(entry.getKey() + "=" + entry.getValue() + ",");
                }
                result = sb.substring(0, sb.length() - 1);
            }
        } catch (Throwable e) {
        }
        return result;
    }

}
