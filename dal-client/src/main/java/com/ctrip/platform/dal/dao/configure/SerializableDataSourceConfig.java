package com.ctrip.platform.dal.dao.configure;

/**
 * Created by taochen on 2019/8/22.
 */
public class SerializableDataSourceConfig implements IDataSourceConfigure, FirstAidKit {
    private String userName;

    private String password;

    private String connectionUrl;

    private String driverClass;

    private Boolean testWhileIdle;

    private Boolean testOnBorrow;

    private Boolean testOnReturn;

    private String validationQuery;

    private Integer validationQueryTimeout;

    private Long validationInterval;

    private Integer minEvictableIdleTimeMillis;

    private Integer timeBetweenEvictionRunsMillis;

    private Integer maxAge;

    private Integer maxActive;

    private Integer minIdle;

    private Integer maxWait;

    private Integer initialSize;

    private Integer removeAbandonedTimeout;

    private Boolean removeAbandoned;

    private Boolean logAbandoned;

    private String connectionProperties;

    private String validatorClassName;

    private String initSQL;

    private String jdbcInterceptors;

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getConnectionUrl() {
        return connectionUrl;
    }

    @Override
    public String getDriverClass() {
        return driverClass;
    }

    @Override
    public Boolean getTestWhileIdle() {
        return testWhileIdle;
    }

    @Override
    public Boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    @Override
    public Boolean getTestOnReturn() {
        return testOnReturn;
    }

    @Override
    public String getValidationQuery() {
        return validationQuery;
    }

    @Override
    public Integer getValidationQueryTimeout() {
        return validationQueryTimeout;
    }

    @Override
    public Long getValidationInterval() {
        return validationInterval;
    }

    @Override
    public Integer getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    @Override
    public Integer getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    @Override
    public Integer getMaxAge() {
        return maxAge;
    }

    @Override
    public Integer getMaxActive() {
        return maxActive;
    }

    @Override
    public Integer getMinIdle() {
        return minIdle;
    }

    @Override
    public Integer getMaxWait() {
        return maxWait;
    }

    @Override
    public Integer getInitialSize() {
        return initialSize;
    }

    @Override
    public Integer getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    @Override
    public Boolean getRemoveAbandoned() {
        return removeAbandoned;
    }

    @Override
    public Boolean getLogAbandoned() {
        return logAbandoned;
    }

    @Override
    public String getConnectionProperties() {
        return connectionProperties;
    }

    @Override
    public String getValidatorClassName() {
        return validatorClassName;
    }

    @Override
    public String getInitSQL() {
        return initSQL;
    }

    @Override
    public String getJdbcInterceptors() {
        return jdbcInterceptors;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public void setTestWhileIdle(Boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public void setTestOnReturn(Boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public void setValidationQueryTimeout(Integer validationQueryTimeout) {
        this.validationQueryTimeout = validationQueryTimeout;
    }

    public void setValidationInterval(Long validationInterval) {
        this.validationInterval = validationInterval;
    }

    public void setMinEvictableIdleTimeMillis(Integer minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(Integer timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public void setMaxWait(Integer maxWait) {
        this.maxWait = maxWait;
    }

    public void setInitialSize(Integer initialSize) {
        this.initialSize = initialSize;
    }

    public void setRemoveAbandonedTimeout(Integer removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
    }

    public void setRemoveAbandoned(Boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public void setLogAbandoned(Boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }

    public void setConnectionProperties(String connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    public void setValidatorClassName(String validatorClassName) {
        this.validatorClassName = validatorClassName;
    }

    public void setInitSQL(String initSQL) {
        this.initSQL = initSQL;
    }

    public void setJdbcInterceptors(String jdbcInterceptors) {
        this.jdbcInterceptors = jdbcInterceptors;
    }

    public static SerializableDataSourceConfig valueOf(DataSourceConfigure dataSourceConfigure) {
        if (dataSourceConfigure == null) {
            return null;
        }
        SerializableDataSourceConfig serializableDataSourceConfig = new SerializableDataSourceConfig();
        serializableDataSourceConfig.setUserName(dataSourceConfigure.getUserName());
        serializableDataSourceConfig.setPassword(dataSourceConfigure.getPassword());
        serializableDataSourceConfig.setConnectionUrl(dataSourceConfigure.getConnectionUrl());
        serializableDataSourceConfig.setDriverClass(dataSourceConfigure.getDriverClass());
        serializableDataSourceConfig.setTestOnBorrow(dataSourceConfigure.getTestOnBorrow());
        serializableDataSourceConfig.setTestOnReturn(dataSourceConfigure.getTestOnReturn());
        serializableDataSourceConfig.setTestWhileIdle(dataSourceConfigure.getTestWhileIdle());
        serializableDataSourceConfig.setValidationQuery(dataSourceConfigure.getValidationQuery());
        serializableDataSourceConfig.setValidationQueryTimeout(dataSourceConfigure.getValidationQueryTimeout());
        serializableDataSourceConfig.setValidationInterval(dataSourceConfigure.getValidationInterval());
        serializableDataSourceConfig.setValidatorClassName(dataSourceConfigure.getValidatorClassName());
        serializableDataSourceConfig.setMinEvictableIdleTimeMillis(dataSourceConfigure.getMinEvictableIdleTimeMillis());
        serializableDataSourceConfig.setTimeBetweenEvictionRunsMillis(dataSourceConfigure.getTimeBetweenEvictionRunsMillis());
        serializableDataSourceConfig.setMaxAge(dataSourceConfigure.getMaxAge());
        serializableDataSourceConfig.setMaxActive(dataSourceConfigure.getMaxActive());
        serializableDataSourceConfig.setMinIdle(dataSourceConfigure.getMinIdle());
        serializableDataSourceConfig.setMaxWait(dataSourceConfigure.getMaxWait());
        serializableDataSourceConfig.setInitialSize(dataSourceConfigure.getInitialSize());
        serializableDataSourceConfig.setRemoveAbandonedTimeout(dataSourceConfigure.getRemoveAbandonedTimeout());
        serializableDataSourceConfig.setRemoveAbandoned(dataSourceConfigure.getRemoveAbandoned());
        serializableDataSourceConfig.setLogAbandoned(dataSourceConfigure.getLogAbandoned());
        serializableDataSourceConfig.setConnectionProperties(dataSourceConfigure.getConnectionProperties());
        serializableDataSourceConfig.setInitSQL(dataSourceConfigure.getInitSQL());
        serializableDataSourceConfig.setJdbcInterceptors(dataSourceConfigure.getJdbcInterceptors());
        return serializableDataSourceConfig;
    }
}
