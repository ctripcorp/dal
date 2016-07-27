package com.ctrip.platform.dal.dao.configure;

import org.apache.tomcat.jdbc.pool.PoolProperties;

public class DatabasePoolConifg {
	
	private String name = null;
	private PoolProperties poolProperties = null;
	private String option = null;
	
	public DatabasePoolConifg() {
		poolProperties = new PoolProperties();
		poolProperties.setTestWhileIdle(DatabasePoolConfigParser.DEFAULT_TESTWHILEIDLE);
		poolProperties.setTestOnBorrow(DatabasePoolConfigParser.DEFAULT_TESTONBORROW);
		poolProperties.setTestOnReturn(DatabasePoolConfigParser.DEFAULT_TESTONRETURN);
		poolProperties.setValidationQuery(DatabasePoolConfigParser.DEFAULT_VALIDATIONQUERY);
		poolProperties.setValidationInterval(DatabasePoolConfigParser.DEFAULT_VALIDATIONINTERVAL);
		poolProperties.setTimeBetweenEvictionRunsMillis(DatabasePoolConfigParser.DEFAULT_TIMEBETWEENEVICTIONRUNSMILLIS);
		poolProperties.setMaxActive(DatabasePoolConfigParser.DEFAULT_MAXACTIVE);
		poolProperties.setMinIdle(DatabasePoolConfigParser.DEFAULT_MINIDLE);
		poolProperties.setMaxWait(DatabasePoolConfigParser.DEFAULT_MAXWAIT);
		poolProperties.setInitialSize(DatabasePoolConfigParser.DEFAULT_INITIALSIZE);
		poolProperties.setRemoveAbandonedTimeout(DatabasePoolConfigParser.DEFAULT_REMOVEABANDONEDTIMEOUT);
		poolProperties.setRemoveAbandoned(DatabasePoolConfigParser.DEFAULT_REMOVEABANDONED);
		poolProperties.setLogAbandoned(DatabasePoolConfigParser.DEFAULT_LOGABANDONED);
		poolProperties.setMinEvictableIdleTimeMillis(DatabasePoolConfigParser.DEFAULT_MINEVICTABLEIDLETIMEMILLIS);
		poolProperties.setConnectionProperties(DatabasePoolConfigParser.DEFAULT_CONNECTIONPROPERTIES);
		poolProperties.setJmxEnabled(DatabasePoolConfigParser.DEFAULT_JMXENABLED);
		poolProperties.setJdbcInterceptors(DatabasePoolConfigParser.DEFAULT_JDBCINTERCEPTORS);
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
