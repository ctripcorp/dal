package com.ctrip.platform.dal.dao.datasource;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.configure.DatabasePoolConfigParser;
import com.ctrip.platform.dal.dao.configure.DatabasePoolConifg;
import com.ctrip.platform.dal.dao.configure.DefaultDataSourceConfigureProvider;


public class DataSourceLocator {
	private static final Logger logger = LoggerFactory.getLogger(DataSourceLocator.class);
	
	private static final ConcurrentHashMap<String,DataSource> cache = new ConcurrentHashMap<String,DataSource>();
	
	private DataSourceConfigureProvider provider;
	
	public DataSourceLocator(DataSourceConfigureProvider provider) {
		this.provider = provider;
	}
	
	/**
	 * This is used for initialize datasource for thirdparty framework
	 */
	public DataSourceLocator() {
		this(new DefaultDataSourceConfigureProvider());
	}
	
	/**
	 * Get DataSource by real db source name
	 * @param name
	 * @return DataSource
	 * @throws NamingException
	 */
	public DataSource getDataSource(String name) throws Exception {
		DataSource ds = cache.get(name); 
		
		if (ds != null) {
			return ds;
		}
		
		synchronized (this.getClass()) {
			ds = cache.get(name); 
			if (ds != null) {
				return ds;
			}
			try {
				ds = createDataSource(name);
				cache.put(name, ds);
			} catch (Throwable e) {
				String msg = "Creating DataSource "+name+" error:"+e.getMessage();
				logger.error(msg, e);
				throw new RuntimeException(msg, e);
			}
		}
		return ds;
	}
	
	private DataSource createDataSource(String name) throws SQLException {
		DatabasePoolConifg poolConfig = DatabasePoolConfigParser.getInstance().getDatabasePoolConifg(name);
		DataSourceConfigure config = provider.getDataSourceConfigure(name);
		
		if (config == null && poolConfig == null) {
			throw new SQLException("Can not find any connection configure for " + name);
		}
		
		if (poolConfig == null) {
			// Create default connection pool configure
			poolConfig = new DatabasePoolConifg();
		}
		
		PoolProperties p = poolConfig.getPoolProperties();
		
		/**
		 * It is assumed that user name/password/url/driver class name are provided in pool config
		 * If not, it should be provided by the config provider
		 */
		if (config != null) {
			p.setUrl(config.getConnectionUrl());
	        p.setUsername(config.getUserName());
	        p.setPassword(config.getPassword());
	        p.setDriverClassName(config.getDriverClass());
		}
		
        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource(p);
		
        ds.createPool();
        
        logger.info("Datasource[name=" + name + ", Driver=" + p.getDriverClassName() + "] created.");
		
		return ds;
	}
}
