package com.ctrip.datasource;

import java.sql.Connection;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.datasource.locator.DataSourceLocator;

public class DataSourceLocatorTest {

	@Test
	public void testGetMySqlDataSource() throws Exception {
		DataSource ds = DataSourceLocator.newInstance().getDataSource("dao_test");
		Assert.assertNotNull(ds);
		Connection conn = ds.getConnection();
		Assert.assertNotNull(conn);
		Assert.assertEquals("dao_test", conn.getCatalog());
		conn.close();
	}
	
	@Test
	public void testGetSqlServerDataSource() throws Exception {
		DataSource ds = DataSourceLocator.newInstance().getDataSource("HotelPubDB");
		Assert.assertNotNull(ds);
		Connection conn = ds.getConnection();
		Assert.assertNotNull(conn);
		Assert.assertEquals("HotelPubDB", conn.getCatalog());
		conn.close();
	}
		
	@Test
	public void testGetDataSource() throws Exception {
		DataSource ds = DataSourceLocator.newInstance().getDataSource("dao_test_1");
		Assert.assertNotNull(ds);
		Connection conn = ds.getConnection();
		Assert.assertNotNull(conn);
		Assert.assertEquals("dao_test", conn.getCatalog());
		conn.close();
		
		DataSource ds2 = DataSourceLocator.newInstance().getDataSource("AbacusDB_SELECT_1");
		Assert.assertNotNull(ds2);
		Connection conn2 = ds2.getConnection();
		Assert.assertNotNull(conn2);
		Assert.assertEquals("AbacusDB", conn2.getCatalog());
		conn2.close();
	}

}
