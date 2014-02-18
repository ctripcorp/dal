package com.ctrip.platform.dal.daogen.utils;

import javax.sql.DataSource;

import junit.framework.TestCase;

public class DataSourceLRUCacheTest extends TestCase {
	
	public void testGetDataSource() throws InterruptedException{
		
		DataSource mysqlInitDs = DataSourceLRUCache.newInstance().getDataSource(5);
		
		assertNull(mysqlInitDs);
		
		DataSource sqlserverInitDs = DataSourceLRUCache.newInstance().getDataSource(10);
		
		assertNull(sqlserverInitDs);
		
		mysqlInitDs = DataSourceLRUCache.newInstance().putDataSource(5);
		
		assertNotNull(mysqlInitDs);
		
		sqlserverInitDs = DataSourceLRUCache.newInstance().putDataSource(10);
		
		assertNotNull(sqlserverInitDs);
		
		//设置超时为6秒s
		DataSourceLRUCache.newInstance().setTimeout(6*1000);
		
		Thread.sleep(4*1000);
		
		mysqlInitDs = DataSourceLRUCache.newInstance().getDataSource(5);
		assertNotNull(mysqlInitDs);
		
		Thread.sleep(4*1000);
		
		mysqlInitDs = DataSourceLRUCache.newInstance().getDataSource(5);
		assertNotNull(mysqlInitDs);
		
		sqlserverInitDs = DataSourceLRUCache.newInstance().getDataSource(10);
		
		assertNull(sqlserverInitDs);
		
	}

}
