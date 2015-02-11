package com.ctrip.datasource.locator;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import com.ctrip.framework.clogging.agent.metrics.IMetric;
import com.ctrip.framework.clogging.agent.metrics.MetricManager;

public class DataSourceLocator {
	
	private static final Log log = LogFactory.getLog(DataSourceLocator.class);
	
	private static IMetric metricLogger = MetricManager.getMetricer();
	
	private static final String DBPOOL_CONFIG = "datasource.xml";
	
	private static final String DataSource_Type = "arch.dal.datasource.type";
	
	private static volatile DataSourceLocator datasourceLocator = new DataSourceLocator();
	
	private Context envContext = null;
	
	private Map<String,DataSource> localDataSource = null;
	
	private DataSourceLocator() {
		try {
			Context initContext = new InitialContext();
			envContext = (Context) initContext.lookup("java:/comp/env/jdbc/");

			if (envContext == null) {
				initLocalDataSourceFactory();
			} else {
				log.error("The current datasource type is jndi.");
			}
		} catch (NamingException e) {
			initLocalDataSourceFactory();
		}
		if (envContext != null && contextExist()) {
			throw new RuntimeException("JNDI datasource and local datasource is conflicting, "
					+ "you must choose only one to use. ");
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initLocalDataSourceFactory(){
		try {
			Class dsfClass = Class.forName("com.ctrip.datasource.LocalDataSourceProvider");
			localDataSource = (Map<String,DataSource>)dsfClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
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
	 * @return
	 * @throws NamingException
	 */
	public DataSource getDataSource(String name) throws Exception {
		if(envContext!=null){
			try {
				//Tag Name默认会加上appid和hostip，所以这个不需要额外加
				metricLogger.log(DataSource_Type, 1L);
			} catch(Throwable e) {
				e.printStackTrace();
			}
			try {
				return (DataSource)envContext.lookup(name);
			} catch (NamingException e) {
				throw e;
			}
		}
		
		if(localDataSource!=null){
			try {
				//Tag Name默认会加上appid和hostip，所以这个不需要额外加
				metricLogger.log(DataSource_Type, 0L);
			} catch(Throwable e) {
				e.printStackTrace();
			}
			try {
				return localDataSource.get(name);
			} catch (Exception e) {
				throw e;
			} 
		}
		
		return null;
		
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
