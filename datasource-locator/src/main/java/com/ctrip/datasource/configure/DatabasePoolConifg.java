package com.ctrip.datasource.configure;

import org.apache.tomcat.jdbc.pool.PoolProperties;

public class DatabasePoolConifg {
	
	private String name = null;
	private PoolProperties poolProperties = null;
	private String option = null;
	
	public DatabasePoolConifg() {
		poolProperties = new PoolProperties();
		poolProperties.setTestWhileIdle(true);
		poolProperties.setTestOnBorrow(false);
		poolProperties.setTestOnReturn(false);
		poolProperties.setValidationQuery("SELECT 1");
		poolProperties.setValidationInterval(30000);
		poolProperties.setTimeBetweenEvictionRunsMillis(30000);
		poolProperties.setMaxActive(100);
		poolProperties.setMinIdle(10);
		poolProperties.setMaxWait(10000);
		poolProperties.setInitialSize(10);
		poolProperties.setRemoveAbandonedTimeout(60);
		poolProperties.setRemoveAbandoned(true);
		poolProperties.setLogAbandoned(true);
		poolProperties.setMinEvictableIdleTimeMillis(30000);
		poolProperties.setConnectionProperties(null);
		poolProperties.setJmxEnabled(true);
		poolProperties.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
		          "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public PoolProperties getPoolProperties() {
		return poolProperties;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	
}
