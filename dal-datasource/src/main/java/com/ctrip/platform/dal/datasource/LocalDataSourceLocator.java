package com.ctrip.platform.dal.datasource;

import java.util.Set;

import javax.sql.DataSource;

public class LocalDataSourceLocator
{
  private static volatile LocalDataSourceLocator datasourceLocator = new LocalDataSourceLocator();
  private LocalDataSourceProvider localDataSource = null;
  
  private LocalDataSourceLocator()
  {
	  localDataSource = new LocalDataSourceProvider(); 
  }
  
  
  public static LocalDataSourceLocator newInstance()
  {
    if (datasourceLocator == null) {
      synchronized (LocalDataSourceLocator.class)
      {
        if (datasourceLocator == null) {
          datasourceLocator = new LocalDataSourceLocator();
        }
      }
    }
    return datasourceLocator;
  }
  
  public boolean refresh(String config){
	  return this.localDataSource == null ? false : this.localDataSource.refresh(config);
  }
  
  public DataSource getDataSource(String name)
    throws Exception
  {
    if (this.localDataSource != null) {
      try
      {
        return (DataSource)this.localDataSource.get(name);
      }
      catch (Exception e)
      {
        throw e;
      }
    }
    return null;
  }
  
  public Set<String> getDBNames()
  {
    if (this.localDataSource != null) {
      return this.localDataSource.keySet();
    }
    return null;
  }
}

