package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.common.enums.DBModel;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.helper.EncryptionHelper;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class DataSourceConfigure extends AbstractDataSourceConfigure
        implements DataSourceConfigureConstants, DalConnectionStringConfigure, DalPoolPropertiesConfigure {
    private String name;
    private Properties properties = new Properties();
    private String version;
    private DalConnectionString connectionString;

    private static final String MYSQL_URL_PREFIX = "jdbc:mysql://";

    public DataSourceConfigure() {
    }

    public DataSourceConfigure(String name) {
        this.name = name;
    }

    public DataSourceConfigure(String name, Properties properties) {
        this(name);
        this.properties = properties;
    }

    public DataSourceConfigure(String name, Map<String, String> propertyMap) {
        this(name);
        merge(propertyMap);
    }

    public void merge(Properties properties) {
        for (Object keyObj : properties.keySet()) {
            String key = (String) keyObj;
            setProperty(key, properties.getProperty(key));
        }
    }

    public void merge(Map<String, String> propertyMap) {
        if (propertyMap != null) {
            for (Map.Entry<String, String> entry : propertyMap.entrySet())
                properties.setProperty(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUserName() {
        return getProperty(USER_NAME);
    }

    public void setUserName(String userName) {
        setProperty(USER_NAME, userName);
    }

    @Override
    public String getPassword() {
        return getProperty(PASSWORD);
    }

    public void setPassword(String password) {
        setProperty(PASSWORD, password);
    }

    @Override
    public String getConnectionUrl() {
        return getProperty(CONNECTION_URL);
    }

    public void setConnectionUrl(String connectionUrl) {
        setProperty(CONNECTION_URL, connectionUrl);
    }

    @Override
    public String getDriverClass() {
        return getProperty(DRIVER_CLASS_NAME);
    }

    public void setDriverClass(String driverClass) {
        setProperty(DRIVER_CLASS_NAME, driverClass);
    }

    @Override
    public String getVersion() {
        return version;
    }


    public void setHostName(String hostName) {
        setProperty(HOST_NAME, hostName);
    }

    @Override
    public String getHostName() {
        return getProperty(HOST_NAME);
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public DalConnectionString getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(DalConnectionString connectionString) {
        this.connectionString = connectionString;
    }


    public Properties getProperties() {
        return properties;
    }

    public Map<String, String> getPoolProperties() {
        return new HashMap<>((Map) properties);
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }


    public String getProperty(String key) {
        return properties.getProperty(key);
    }


    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }


    public int getIntProperty(String key, int defaultValue) {
        return properties.containsKey(key) ? Integer.parseInt(getProperty(key)) : defaultValue;
    }


    public long getLongProperty(String key, long defaultValue) {
        return properties.containsKey(key) ? Long.parseLong(getProperty(key)) : defaultValue;
    }


    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return properties.containsKey(key) ? Boolean.parseBoolean(getProperty(key)) : defaultValue;
    }


    public Boolean getTestWhileIdle() {
        return getBooleanProperty(TESTWHILEIDLE, DEFAULT_TESTWHILEIDLE);
    }

    public Boolean getTestOnBorrow() {
        return getBooleanProperty(TESTONBORROW, DEFAULT_TESTONBORROW);
    }

    public Boolean getTestOnReturn() {
        return getBooleanProperty(TESTONRETURN, DEFAULT_TESTONRETURN);
    }

    public String getValidationQuery() {
        return getProperty(VALIDATIONQUERY, DEFAULT_VALIDATIONQUERY);
    }

    public Integer getValidationQueryTimeout() {
        return getIntProperty(VALIDATIONQUERYTIMEOUT, DEFAULT_VALIDATIONQUERYTIMEOUT);
    }

    public Long getValidationInterval() {
        return getLongProperty(VALIDATIONINTERVAL, DEFAULT_VALIDATIONINTERVAL);
    }

    public Integer getTimeBetweenEvictionRunsMillis() {
        return getIntProperty(TIMEBETWEENEVICTIONRUNSMILLIS, DEFAULT_TIMEBETWEENEVICTIONRUNSMILLIS);
    }

    public Integer getMinEvictableIdleTimeMillis() {
        return getIntProperty(MINEVICTABLEIDLETIMEMILLIS, DEFAULT_MINEVICTABLEIDLETIMEMILLIS);
    }

    public Integer getMaxAge() {
        return getIntProperty(MAX_AGE, DEFAULT_MAXAGE);
    }

    public Integer getMaxActive() {
        return getIntProperty(MAXACTIVE, DEFAULT_MAXACTIVE);
    }

    public Integer getMinIdle() {
        return getIntProperty(MINIDLE, DEFAULT_MINIDLE);
    }

    public Integer getMaxWait() {
        return getIntProperty(MAXWAIT, DEFAULT_MAXWAIT);
    }

    public Integer getInitialSize() {
        return getIntProperty(INITIALSIZE, DEFAULT_INITIALSIZE);
    }

    public Integer getRemoveAbandonedTimeout() {
        return getIntProperty(REMOVEABANDONEDTIMEOUT, DEFAULT_REMOVEABANDONEDTIMEOUT);
    }

    public Boolean getRemoveAbandoned() {
        return getBooleanProperty(REMOVEABANDONED, DEFAULT_REMOVEABANDONED);
    }

    public Boolean getLogAbandoned() {
        return getBooleanProperty(LOGABANDONED, DEFAULT_LOGABANDONED);
    }

    public String getConnectionProperties() {
        return getProperty(CONNECTIONPROPERTIES, DEFAULT_CONNECTIONPROPERTIES);
    }

    public String getValidatorClassName() {
        return getProperty(VALIDATORCLASSNAME, DEFAULT_VALIDATORCLASSNAME);
    }

    public String getOption() {
        return getProperty(OPTION, DEFAULT_CONNECTIONPROPERTIES);
    }

    @Override
    public String getDBToken() {
        return getProperty(DB_TOKEN);
    }

    @Override
    public Integer getCallMysqlApiPeriod() {
        return getIntProperty(CALL_MYSQL_API_PERIOD, DEFAULT_CALL_MYSQL_API_PERIOD);
    }

    @Override
    public DBModel getDBModel() {
        return DBModel.toDBModel(getProperty(DB_MODEL, DEFAULT_DB_MODEL));
    }


    public String getInitSQL() {
        String initSQL = getProperty(INIT_SQL);
        if (initSQL != null && !initSQL.isEmpty())
            return initSQL;

        String initSQL2 = getProperty(INIT_SQL2);
        if (initSQL2 != null && !initSQL2.isEmpty())
            return initSQL2;

        return null;
    }

    // This are current hard coded as default value
    public boolean getJmxEnabled() {
        return DEFAULT_JMXENABLED;
    }

    public String getJdbcInterceptors() {
        return getProperty(JDBC_INTERCEPTORS, DEFAULT_JDBCINTERCEPTORS);
    }

    public String toConnectionUrl() {
        return String.format("{ConnectionUrl:%s,Version:%s,CRC:%s}", getConnectionUrl(), version, getCRC());
    }

    public Properties toProperties() {
        Properties p = new Properties();
        Set<String> set = new HashSet<>();
        set.add(USER_NAME);
        set.add(PASSWORD);
        set.add(CONNECTION_URL);
        set.add(DRIVER_CLASS_NAME);
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            if (!set.contains(entry.getKey())) {
                p.setProperty(entry.getKey().toString(), entry.getValue().toString());
            }
        }

        return p;
    }

    public boolean dynamicPoolPropertiesEnabled() {
        if (properties == null || properties.isEmpty())
            return false;

        String value = properties.getProperty(ENABLE_DYNAMIC_POOL_PROPERTIES);
        if (value == null)
            return false;

        return Boolean.parseBoolean(value);
    }

    public DatabaseCategory getDatabaseCategory() {
        return DatabaseCategory.matchWithConnectionUrl(getConnectionUrl());
    }

    // Rule: username concat password,and then take 8 characters of md5 code from beginning
    private String getCRC() {
        String crc = null;
        String userName = getUserName();
        String pass = getPassword();
        try {
            userName.concat(pass);
            crc = EncryptionHelper.getCRC(userName);
        } catch (Throwable e) {
        }
        return crc;
    }

    public synchronized DataSourceConfigure clone() {
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure(name);
        Properties p = new Properties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            p.setProperty(entry.getKey().toString(), entry.getValue().toString());
        }
        dataSourceConfigure.setProperties(p);
        dataSourceConfigure.setVersion(version);
        dataSourceConfigure.setConnectionString(connectionString == null ? null : connectionString.clone());
        return dataSourceConfigure;
    }

    public static DataSourceConfigure valueOf(IDataSourceConfigure configure) {
        if (configure instanceof DataSourceConfigure)
            return (DataSourceConfigure) configure;
        else {
            DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
            Properties properties = new Properties();
            properties.setProperty(USER_NAME, configure.getUserName());
            properties.setProperty(PASSWORD, configure.getPassword());
            String connectionUrl = configure.getConnectionUrl();
            if (connectionUrl == null)
                throw new DalRuntimeException("connetion url cannot be null");
            properties.setProperty(CONNECTION_URL, connectionUrl);
            HostAndPort hostAndPort = ConnectionStringParser.parseHostPortFromURL(connectionUrl);
            if (StringUtils.isEmpty(hostAndPort.getHost())) {
                properties.setProperty(HOST_NAME, "UnKnown");
            }
            else {
                properties.setProperty(HOST_NAME, hostAndPort.getHost());
            }

            if (configure.getDriverClass() != null)
                properties.setProperty(DRIVER_CLASS_NAME, configure.getDriverClass());
            if (configure.getTestWhileIdle() != null)
                properties.setProperty(TESTWHILEIDLE, String.valueOf(configure.getTestWhileIdle()));
            if (configure.getTestOnBorrow() != null)
                properties.setProperty(TESTONBORROW, String.valueOf(configure.getTestOnBorrow()));
            if (configure.getTestOnReturn() != null)
                properties.setProperty(TESTONRETURN, String.valueOf(configure.getTestOnReturn()));
            if (configure.getValidationQuery() != null)
                properties.setProperty(VALIDATIONQUERY, configure.getValidationQuery());
            if (configure.getValidationQueryTimeout() != null)
                properties.setProperty(VALIDATIONQUERYTIMEOUT, String.valueOf(configure.getValidationQueryTimeout()));
            if (configure.getValidationInterval() != null)
                properties.setProperty(VALIDATIONINTERVAL, String.valueOf(configure.getValidationInterval()));
            if (configure.getTimeBetweenEvictionRunsMillis() != null)
                properties.setProperty(TIMEBETWEENEVICTIONRUNSMILLIS, String.valueOf(configure.getTimeBetweenEvictionRunsMillis()));
            if (configure.getMaxAge() != null)
                properties.setProperty(MAX_AGE, String.valueOf(configure.getMaxAge()));
            if (configure.getMaxActive() != null)
                properties.setProperty(MAXACTIVE, String.valueOf(configure.getMaxActive()));
            if (configure.getMinIdle() != null)
                properties.setProperty(MINIDLE, String.valueOf(configure.getMinIdle()));
            if (configure.getMaxWait() != null)
                properties.setProperty(MAXWAIT, String.valueOf(configure.getMaxWait()));
            if (configure.getInitialSize() != null)
                properties.setProperty(INITIALSIZE, String.valueOf(configure.getInitialSize()));
            if (configure.getRemoveAbandonedTimeout() != null)
                properties.setProperty(REMOVEABANDONEDTIMEOUT, String.valueOf(configure.getRemoveAbandonedTimeout()));
            if (configure.getRemoveAbandoned() != null)
                properties.setProperty(REMOVEABANDONED, String.valueOf(configure.getRemoveAbandoned()));
            if (configure.getLogAbandoned() != null)
                properties.setProperty(LOGABANDONED, String.valueOf(configure.getLogAbandoned()));
            if (configure.getMinEvictableIdleTimeMillis() != null)
                properties.setProperty(MINEVICTABLEIDLETIMEMILLIS, String.valueOf(configure.getMinEvictableIdleTimeMillis()));
            if (configure.getConnectionProperties() != null)
                properties.setProperty(CONNECTIONPROPERTIES, configure.getConnectionProperties());
            if (configure.getInitSQL() != null)
                properties.setProperty(INIT_SQL, configure.getInitSQL());
            if (configure.getValidatorClassName() != null)
                properties.setProperty(VALIDATORCLASSNAME, configure.getValidatorClassName());
            if (configure.getJdbcInterceptors() != null)
                properties.setProperty(JDBC_INTERCEPTORS, configure.getJdbcInterceptors());
            dataSourceConfigure.setProperties(properties);
            return dataSourceConfigure;
        }
    }

    public void replaceURL(String ip, int port) {
        String newConnectionUrl = ConnectionStringParser.replaceHostAndPort(getConnectionUrl(),ip,String.valueOf(port));
        setConnectionUrl(newConnectionUrl);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DataSourceConfigure) {
            DataSourceConfigure ref = (DataSourceConfigure) obj;
            return (equals(ref.getConnectionUrl(), ref.getConnectionUrl()) &&
                    equals(ref.getUserName(), ref.getUserName()) &&
                    equals(ref.getPassword(), ref.getPassword()) &&
                    equals(ref.getDriverClass(), ref.getDriverClass()) &&
                    equals(ref.getTestOnBorrow(), ref.getTestOnBorrow()) &&
                    equals(ref.getTestOnReturn(), ref.getTestOnReturn()) &&
                    equals(ref.getTestWhileIdle(), ref.getTestWhileIdle()) &&
                    equals(ref.getValidationInterval(), ref.getValidationInterval()) &&
                    equals(ref.getValidationQuery(), ref.getValidationQuery()) &&
                    equals(ref.getValidationQueryTimeout(), ref.getValidationQueryTimeout()) &&
                    equals(ref.getValidatorClassName(), ref.getValidatorClassName()) &&
                    equals(ref.getMaxActive(), ref.getMaxActive()) &&
                    equals(ref.getMaxAge(), ref.getMaxAge()) &&
                    equals(ref.getMaxWait(), ref.getMaxWait()) &&
                    equals(ref.getMinIdle(), ref.getMinIdle()) &&
                    equals(ref.getTimeBetweenEvictionRunsMillis(), ref.getTimeBetweenEvictionRunsMillis()) &&
                    equals(ref.getMinEvictableIdleTimeMillis(), ref.getMinEvictableIdleTimeMillis()) &&
                    equals(ref.getInitialSize(), ref.getInitialSize()) &&
                    equals(ref.getInitSQL(), ref.getInitSQL()) &&
                    equals(ref.getLogAbandoned(), ref.getLogAbandoned()) &&
                    equals(ref.getRemoveAbandoned(), ref.getRemoveAbandoned()) &&
                    equals(ref.getRemoveAbandonedTimeout(), ref.getRemoveAbandonedTimeout()) &&
                    equals(ref.getJdbcInterceptors(), ref.getJdbcInterceptors()) &&
                    equals(ref.getConnectionProperties(), ref.getConnectionProperties()) &&
                    equals(ref.getJmxEnabled(), ref.getJmxEnabled()));
        }
        return false;
    }

    private boolean equals(Object obj1, Object obj2) {
        return (obj1 != null && obj1.equals(obj2)) || (obj1 == null && obj2 == null);
    }

    @Override
    public int hashCode() {
        return new HashCodeGenerator().
                append(getConnectionUrl()).
                append(getUserName()).
                append(getPassword()).
                append(getDriverClass()).
                append(getTestOnBorrow()).
                append(getTestOnReturn()).
                append(getTestWhileIdle()).
                append(getValidationInterval()).
                append(getValidationQuery()).
                append(getValidationQueryTimeout()).
                append(getValidatorClassName()).
                append(getMaxActive()).
                append(getMaxAge()).
                append(getMaxWait()).
                append(getMinIdle()).
                append(getTimeBetweenEvictionRunsMillis()).
                append(getMinEvictableIdleTimeMillis()).
                append(getInitialSize()).
                append(getInitSQL()).
                append(getLogAbandoned()).
                append(getRemoveAbandoned()).
                append(getRemoveAbandonedTimeout()).
                append(getJdbcInterceptors()).
                append(getConnectionProperties()).
                append(getJmxEnabled()).
                generate();
    }

    private static class HashCodeGenerator {
        private int hashCode = 0;
        public HashCodeGenerator append(Object obj) {
            hashCode = hashCode * 31 + (obj != null ? obj.hashCode() : 0);
            return this;
        }
        public int generate() {
            return hashCode;
        }
    }

}
