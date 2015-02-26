package com.ctrip.datasource.configure;

import org.apache.tomcat.jdbc.pool.PoolProperties;

public class DatabasePoolConifg {
	
	private String name = null;
	private PoolProperties poolProperties = null;
	private String option = null;
	
	public DatabasePoolConifg() {
		poolProperties = new PoolProperties();
		poolProperties.setTestWhileIdle(DatabasePoolConfigParser.DEFUALT_TESTWHILEIDLE);
		poolProperties.setTestOnBorrow(DatabasePoolConfigParser.DEFUALT_TESTONBORROW);
		poolProperties.setTestOnReturn(DatabasePoolConfigParser.DEFUALT_TESTONRETURN);
		poolProperties.setValidationQuery(DatabasePoolConfigParser.DEFUALT_VALIDATIONQUERY);
		poolProperties.setValidationInterval(DatabasePoolConfigParser.DEFUALT_VALIDATIONINTERVAL);
		poolProperties.setTimeBetweenEvictionRunsMillis(DatabasePoolConfigParser.DEFUALT_TIMEBETWEENEVICTIONRUNSMILLIS);
		poolProperties.setMaxActive(DatabasePoolConfigParser.DEFUALT_MAXACTIVE);
		poolProperties.setMinIdle(DatabasePoolConfigParser.DEFUALT_MINIDLE);
		poolProperties.setMaxWait(DatabasePoolConfigParser.DEFUALT_MAXWAIT);
		poolProperties.setInitialSize(DatabasePoolConfigParser.DEFUALT_INITIALSIZE);
		poolProperties.setRemoveAbandonedTimeout(DatabasePoolConfigParser.DEFUALT_REMOVEABANDONEDTIMEOUT);
		poolProperties.setRemoveAbandoned(DatabasePoolConfigParser.DEFUALT_REMOVEABANDONED);
		poolProperties.setLogAbandoned(DatabasePoolConfigParser.DEFUALT_LOGABANDONED);
		poolProperties.setMinEvictableIdleTimeMillis(DatabasePoolConfigParser.DEFUALT_MINEVICTABLEIDLETIMEMILLIS);
		poolProperties.setConnectionProperties(DatabasePoolConfigParser.DEFUALT_CONNECTIONPROPERTIES);
		poolProperties.setJmxEnabled(DatabasePoolConfigParser.DEFUALT_JMXENABLED);
		poolProperties.setJdbcInterceptors(DatabasePoolConfigParser.DEFUALT_JDBCINTERCEPTORS);
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
