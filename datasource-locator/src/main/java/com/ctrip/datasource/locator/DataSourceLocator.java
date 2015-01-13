package com.ctrip.datasource.locator;

//import java.util.concurrent.atomic.AtomicBoolean;

import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DataSourceLocator {
	private static volatile DataSourceLocator datasourceLocator = new DataSourceLocator();
	//private static AtomicBoolean exists = new AtomicBoolean(false);
	private Context envContext=null;
	private Map<String,DataSource> localDataSource=null;
	
	private DataSourceLocator(){
			try {
				Context initContext= new InitialContext();
				envContext  = (Context)initContext.lookup("java:/comp/env");
				
				if(envContext==null){
					initLocalDataSourceFactory();
				}
				
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				initLocalDataSourceFactory();
			} 
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initLocalDataSourceFactory(){
		try {
			Class dsfClass = Class.forName("com.ctrip.datasource.LocalDataSourceProvider");
			//Thread.currentThread().getContextClassLoader()
			//Class dsfClass = this.getClass().getClassLoader().loadClass("com.ctrip.datasource.LocalDataSourceProvider");
			localDataSource=(Map<String,DataSource>)dsfClass.newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static DataSourceLocator newInstance(){
		if(datasourceLocator==null){
			synchronized(DataSourceLocator.class){
				if(datasourceLocator==null){
					datasourceLocator=new DataSourceLocator();
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
				//e.printStackTrace();
				throw e;
			}
		}
		
		if(localDataSource!=null){
			try {
				return localDataSource.get(name);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
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
