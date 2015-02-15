package com.ctrip.datasource;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.ctrip.datasource.configure.DatabaseConfigParser;
import com.ctrip.datasource.configure.DatabasePoolConfigParser;
import com.ctrip.datasource.configure.DatabasePoolConifg;

public class LocalDataSourceProvider {

	private static final Log log = LogFactory.getLog(LocalDataSourceProvider.class);
	
	private final Map<String,String[]> props = DatabaseConfigParser.newInstance().getDBAllInOneConfig();
	
	private static final ConcurrentHashMap<String,DataSource> cache = new ConcurrentHashMap<String,DataSource>();
	
	public Set<String> keySet(){
		return props.keySet();
	}
		
	public DataSource get(String name) {
		
		DataSource ds = cache.get(name); 
		
		if (ds != null) {
			return ds;
		}
		
		synchronized (LocalDataSourceProvider.class) {
			ds = cache.get(name); 
			if (ds != null) {
				return ds;
			}
			try {
				ds = createDataSource(name);
				DataSource d = cache.putIfAbsent(name, ds);
				if(d != null){
					ds = d;
				}
			} catch (Throwable e) {
				log.error("Creating DataSource "+name+" error:"+e.getMessage(), e);
			}
		}
		
		return ds;
		
	}
	
	private DataSource createDataSource(String name) throws SQLException {
		
		DatabasePoolConifg poolConfig = DatabasePoolConfigParser.getInstance().getDatabasePoolConifg(name);
		if (poolConfig == null) {
			poolConfig = new DatabasePoolConifg();
		}
		
		String[] prop = props.get(name);
		
		if (prop == null) {
			throw new SQLException("the all-in-one file does not hava any configure infomation for " + name);
		}
		
		PoolProperties p = new PoolProperties();
		
		p.setUrl(prop[0]);
        p.setUsername(prop[1]);
        p.setPassword(prop[2]);
        p.setDriverClassName(prop[3]);
        p.setJmxEnabled(true);
        
        p.setTestWhileIdle(poolConfig.isTestWhileIdle());
        p.setTestOnBorrow(poolConfig.isTestOnBorrow());
        p.setValidationQuery(poolConfig.getValidationQuery());
        p.setTestOnReturn(poolConfig.isTestOnReturn());
        p.setValidationInterval(poolConfig.getValidationInterval());
        p.setTimeBetweenEvictionRunsMillis(poolConfig.getTimeBetweenEvictionRunsMillis());
        p.setMaxActive(poolConfig.getMaxActive());
        p.setInitialSize(poolConfig.getInitialSize());
        p.setMaxWait(poolConfig.getMaxWait());
        p.setRemoveAbandonedTimeout(poolConfig.getRemoveAbandonedTimeout());
        p.setMinEvictableIdleTimeMillis(poolConfig.getMinEvictableIdleTimeMillis());
        p.setMinIdle(poolConfig.getMinIdle());
        p.setLogAbandoned(poolConfig.isLogAbandoned());
        p.setRemoveAbandoned(poolConfig.isRemoveAbandoned());
        p.setConnectionProperties(poolConfig.getConnectionProperties());
        
        p.setJdbcInterceptors(
          "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
          "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource(p);
		
        ds.createPool();
        
        log.info("Datasource[name=" + name + ", Driver=" + prop[3] + "] created.");
		
		return ds;

	}
	

}