package com.ctrip.datasource.locator;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import com.ctrip.datasource.LocalDataSourceProvider;
import com.ctrip.framework.clogging.agent.metrics.IMetric;
import com.ctrip.framework.clogging.agent.metrics.MetricManager;

public class DataSourceLocator {
	
	private static final Log log = LogFactory.getLog(DataSourceLocator.class);
	
	private static IMetric metricLogger = MetricManager.getMetricer();
	
	private static final String DBPOOL_CONFIG = "datasource.xml";
	
	private static final String DATASOURCE_TYPE = "arch.dal.datasource.type";
	
	private static final Map<String, String> TAG_JNDI = new HashMap<String, String>();
	
	private static final Map<String, String> TAG_LOCAL = new HashMap<String, String>();
	
	private static volatile DataSourceLocator datasourceLocator = new DataSourceLocator();
	
	private Context envContext = null;
	
	private LocalDataSourceProvider localDataSource = null;
	
	static {
		TAG_JNDI.put("source", "jndi");
		TAG_LOCAL.put("source", "local");
	}
	
	private DataSourceLocator() {
		try {
			Context initContext = new InitialContext();
			envContext = (Context) initContext.lookup("java:/comp/env/jdbc/");

			if (envContext == null) {
				localDataSource = new LocalDataSourceProvider();
			} else {
				log.error("The current datasource type is jndi.");
			}
		} catch (NamingException e) {
			localDataSource = new LocalDataSourceProvider();
		}
		if (envContext != null && contextExist()) {
			throw new RuntimeException("JNDI datasource and local datasource are both exist, "
					+ "you need to remove JNDI settings from server.xml and use local instead.");
		}
	}
	
	public static DataSourceLocator newInstance(){
		if(datasourceLocator==null){
			synchronized(DataSourceLocator.class){
				if(datasourceLocator==null){
					datasourceLocator = new DataSourceLocator();
				}
			}
		}
		return datasourceLocator;
	}
	
	/**
	 * Get DataSource by real db source name
	 * @param name
	 * @return DataSource
	 * @throws NamingException
	 */
	public DataSource getDataSource(String name) throws Exception {
		if(envContext!=null) {
			metric(TAG_JNDI);
			return (DataSource)envContext.lookup(name);
		}
		
		if(localDataSource!=null) {
			metric(TAG_LOCAL);
			return localDataSource.get(name);
		}
		
		return null;
		
	}
	
	private void metric(Map<String, String> tag) {
		try {
			//Tag Name默认会加上appid和hostip，所以这个不需要额外加
			metricLogger.log(DATASOURCE_TYPE, 1L, tag);
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
	public Set<String> getDBNames(){
		if(localDataSource!=null){
			return localDataSource.keySet();
		}
		return null;
	}
	
	private boolean contextExist() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = DataSourceLocator.class.getClassLoader();
		}
		URL url = classLoader.getResource(DBPOOL_CONFIG);
		if (url == null) {
			return false;
		} else {
			File file = new File(url.getFile());
			if (file.exists()) {
				return true;
			} else {
				return false;
			}
		}
	}
}