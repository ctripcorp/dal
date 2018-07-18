package com.ctrip.platform.dal.daogen.entity;

import com.ctrip.platform.dal.daogen.enums.DatabaseType;
import com.ctrip.platform.dal.daogen.utils.DataSourceUtil;

public class Resource {
    private String name;
    private String auth = "Container";
    private String type = "javax.sql.DataSource";
    private String factory = "com.ctrip.datasource.pool.CtripDataSourceFactory";
    private boolean testWhileIdle = true;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;
    private String validationQuery = "SELECT 1";
    private int validationInterval = 30000;
    private int timeBetweenEvictionRunsMillis = 30000;
    private int maxActive = 100;
    private int minIdle = 10;
    private int maxWait = 10000;
    private int initialSize = 10;
    private int removeAbandonedTimeout = 60;
    private boolean removeAbandoned = true;
    private boolean logAbandoned = true;
    private int minEvictableIdleTimeMillis = 30000;
    private boolean jmxEnabled = true;
    private String jdbcInterceptors = "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
            + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer";

    private String userName;
    private String password;
    private String dbAddress;
    private String dbPort;
    private String dbCatalog;
    private String connectionUrl;
    private String driverClassName;

    public Resource(String name) {
        this.name = name;
    }

    public Resource(String name, String userName, String password, String dbAddress, String dbPort, String dbCatalog,
            String driverClassName) {
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.dbAddress = dbAddress;
        this.dbPort = dbPort;
        this.dbCatalog = dbCatalog;
        this.driverClassName = driverClassName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public int getValidationInterval() {
        return validationInterval;
    }

    public void setValidationInterval(int validationInterval) {
        this.validationInterval = validationInterval;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public int getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
    }

    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public boolean isLogAbandoned() {
        return logAbandoned;
    }

    public void setLogAbandoned(boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }

    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public boolean isJmxEnabled() {
        return jmxEnabled;
    }

    public void setJmxEnabled(boolean jmxEnabled) {
        this.jmxEnabled = jmxEnabled;
    }

    public String getJdbcInterceptors() {
        return jdbcInterceptors;
    }

    public void setJdbcInterceptors(String jdbcInterceptors) {
        this.jdbcInterceptors = jdbcInterceptors;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbAddress() {
        return dbAddress;
    }

    public void setDbAddress(String dbAddress) {
        this.dbAddress = dbAddress;
    }

    public String getDbPort() {
        return dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbCatalog() {
        return dbCatalog;
    }

    public void setDbCatalog(String dbCatalog) {
        this.dbCatalog = dbCatalog;
    }

    public String getConnectionUrl() throws Exception {
        return DataSourceUtil.getConnectionUrl(getDbAddress(), getDbPort(), getDbCatalog(), this.driverClassName);
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getDriverClassName() throws Exception {
        return DataSourceUtil.getDriverClass(driverClassName);
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public boolean isOptionAppend() {
        return DatabaseType.SQLServer.getValue().equals(driverClassName);
    }
}
