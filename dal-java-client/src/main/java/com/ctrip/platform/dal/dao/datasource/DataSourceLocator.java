package com.ctrip.platform.dal.dao.datasource;

import java.util.Set;

import javax.naming.NamingException;
import javax.sql.DataSource;

import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;


public class DataSourceLocator {
	
	private static volatile DataSourceLocator datasourceLocator = null;
	
	private LocalDataSourceProvider localDataSource = null;
	
	private DataSourceLocator(ConnectionStringParser parser) {
		localDataSource = new LocalDataSourceProvider(parser);
	}
	
	public static DataSourceLocator newInstance(ConnectionStringParser parser){
		if(datasourceLocator==null){
			synchronized(DataSourceLocator.class){
				if(datasourceLocator==null){
					datasourceLocator = new DataSourceLocator(parser);
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
		return localDataSource.get(name);
	}
	
	public Set<String> getDBNames(){
		return localDataSource.keySet();
	}
	
}
