package com.ctrip.platform.dal.dao.configure;


/**
 * A list of properties that are configurable for a connection pool.
 */
public interface PoolPropertiesConfigure {

    Boolean getTestWhileIdle();

    Boolean getTestOnBorrow();

    Boolean getTestOnReturn();

    String getValidationQuery();

    Integer getValidationQueryTimeout();

    Long getValidationInterval();

    Integer getTimeBetweenEvictionRunsMillis();

    Integer getMinEvictableIdleTimeMillis();

    Integer getMaxAge();

    Integer getMaxActive();

    Integer getMinIdle();

    Integer getMaxWait();

    Integer getInitialSize();

    Integer getRemoveAbandonedTimeout();

    Boolean getRemoveAbandoned();

    Boolean getLogAbandoned();

    String getConnectionProperties();

    String getValidatorClassName();

    String getInitSQL();

    String getJdbcInterceptors();
}
