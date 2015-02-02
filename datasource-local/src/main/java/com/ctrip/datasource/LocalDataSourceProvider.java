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

public class LocalDataSourceProvider<K extends CharSequence,V extends DataSource> extends ConcurrentHashMap<K,V>{

	
	private static final long serialVersionUID = -5752249323568785554L;

	private static final Log log = LogFactory.getLog(LocalDataSourceProvider.class);
	
	private final Map<String,String[]> props = DatabaseConfigParser.newInstance().getDBAllInOneConfig();
	
	
	@SuppressWarnings("unchecked")
	public Set<K> keySet(){
		return (Set<K>) props.keySet();
	}
		
	/**
	 * override
	 * @param data source name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public V get(Object name) {
		
		V ds = super.get(name); 
		
		if(ds==null){
			try {
				ds = (V)createDataSource(name);
				V d = this.putIfAbsent((K)name, ds);
				if(d!=null){
					ds=d;
				}
			} catch (SQLException e) {
				log.error("Creating DataSource "+name+" error:"+e.getMessage(), e);
			}
		}
		
		return ds;
		
	}
	
	private DataSource createDataSource(Object name) throws SQLException{
		
		DatabasePoolConifg poolConfig = DatabasePoolConfigParser.getInstance().getDatabasePoolConifg((String)name);
		if (poolConfig == null) {
			poolConfig = new DatabasePoolConifg();
		}
		
		String[] prop = props.get(name);
		
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
