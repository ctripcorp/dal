package com.ctrip.platform.dal.dao.datasource;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;
import com.ctrip.platform.dal.dao.configure.DatabaseConfigParser;
import com.ctrip.platform.dal.dao.configure.DatabasePoolConfigParser;
import com.ctrip.platform.dal.dao.configure.DatabasePoolConifg;

public class LocalDataSourceProvider {

	private static final Log log = LogFactory.getLog(LocalDataSourceProvider.class);
	
	private Map<String,String[]> props = null;
	
	private static final ConcurrentHashMap<String,DataSource> cache = new ConcurrentHashMap<String,DataSource>();
	
	public LocalDataSourceProvider(ConnectionStringParser parser) {
		props = DatabaseConfigParser.newInstance(parser).getDBAllInOneConfig();
	}
	
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
				cache.put(name, ds);
			} catch (Throwable e) {
				String msg = "Creating DataSource "+name+" error:"+e.getMessage();
				log.error(msg, e);
				throw new RuntimeException(msg, e);
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
			throw new SQLException("the Database.Config file does not hava any configure infomation for " + name);
		}
		
		PoolProperties p = poolConfig.getPoolProperties();
		
		p.setUrl(prop[0]);
        p.setUsername(prop[1]);
        p.setPassword(prop[2]);
        p.setDriverClassName(prop[3]);
        
        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource(p);
		
        ds.createPool();
        
        log.info("Datasource[name=" + name + ", Driver=" + prop[3] + "] created.");
		
		return ds;

	}
	

}