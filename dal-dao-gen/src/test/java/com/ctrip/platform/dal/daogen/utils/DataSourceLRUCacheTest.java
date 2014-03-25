package com.ctrip.platform.dal.daogen.utils;

import javax.sql.DataSource;

import junit.framework.TestCase;

public class DataSourceLRUCacheTest extends TestCase {
	
	public void testGetDataSource() throws InterruptedException{
		
//		DataSource mysqlInitDs = DataSourceLRUCache.newInstance().getDataSource(14);
//		
//		assertNull(mysqlInitDs);
//		
//		DataSource sqlserverInitDs = DataSourceLRUCache.newInstance().getDataSource(15);
//		
//		assertNull(sqlserverInitDs);
//		
//		mysqlInitDs = DataSourceLRUCache.newInstance().putDataSource(14);
//		
//		assertNotNull(mysqlInitDs);
//		
//		sqlserverInitDs = DataSourceLRUCache.newInstance().putDataSource(15);
//		
//		assertNotNull(sqlserverInitDs);
//		
//		//设置超时为6秒s
//		DataSourceLRUCache.newInstance().setTimeout(6*1000);
//		
//		Thread.sleep(4*1000);
//		
//		mysqlInitDs = DataSourceLRUCache.newInstance().getDataSource(14);
//		assertNotNull(mysqlInitDs);
//		
//		Thread.sleep(4*1000);
//		
//		mysqlInitDs = DataSourceLRUCache.newInstance().getDataSource(14);
//		assertNotNull(mysqlInitDs);
//		
//		sqlserverInitDs = DataSourceLRUCache.newInstance().getDataSource(15);
//		
//		assertNull(sqlserverInitDs);
		
	}

}
