package com.ctrip.datasource.locator;

import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.ctrip.framework.clogging.agent.metrics.IMetric;
import com.ctrip.framework.clogging.agent.metrics.MetricManager;

public class DataSourceLocator {
	
	private static volatile DataSourceLocator datasourceLocator = new DataSourceLocator();
	
	private static IMetric metricLogger = MetricManager.getMetricer();
	
	private static final String DataSource_Type = "arch.dal.datasource.type";
	
	private Context envContext = null;
	
	private Map<String,DataSource> localDataSource = null;
	
	private DataSourceLocator() {
		try {
			Context initContext = new InitialContext();
			envContext = (Context) initContext.lookup("java:/comp/env");

			if (envContext == null) {
				initLocalDataSourceFactory();
			} else {
				try {
					//Tag Name默认会加上appid和hostip，所以这个不需要额外加
					metricLogger.log(DataSource_Type, 1L);
				} catch(Throwable e) {
					e.printStackTrace();
				}
			}
		} catch (NamingException e) {
			initLocalDataSourceFactory();
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
	public DataSource getDataSource(String name) throws Exception{
		if(envContext!=null){
			try {
				return (DataSource)envContext.lookup("jdbc/"+name);
			} catch (NamingException e) {
				throw e;
			}
		}
		
		if(localDataSource!=null){
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
}
