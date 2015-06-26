package com.ctrip.platform.dal.tester.datasource;

import java.sql.Connection;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.datasource.configure.CtripConnectionStringParser;
import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;

public class DataSourceLocatorTest {

	@Test
	public void testGetMySqlDataSource() throws Exception {
		ConnectionStringParser parser = new CtripConnectionStringParser();
		DataSource ds = DataSourceLocator.newInstance(parser).getDataSource("dao_test");
		Assert.assertNotNull(ds);
		Connection conn = ds.getConnection();
		Assert.assertNotNull(conn);
		Assert.assertEquals("dao_test", conn.getCatalog());
		conn.close();
	}
	
	@Test
	public void testGetSqlServerDataSource() throws Exception {
		ConnectionStringParser parser = new CtripConnectionStringParser();
		DataSource ds = DataSourceLocator.newInstance(parser).getDataSource("HotelPubDB");
		Assert.assertNotNull(ds);
		Connection conn = ds.getConnection();
		Assert.assertNotNull(conn);
		Assert.assertEquals("HotelPubDB", conn.getCatalog());
		conn.close();
	}
		
	@Test
	public void testGetDataSource() throws Exception {
		ConnectionStringParser parser = new CtripConnectionStringParser();
		DataSource ds = DataSourceLocator.newInstance(parser).getDataSource("dao_test_1");
		Assert.assertNotNull(ds);
		Connection conn = ds.getConnection();
		Assert.assertNotNull(conn);
		Assert.assertEquals("dao_test_1", conn.getCatalog());
		conn.close();
		
		DataSource ds2 = DataSourceLocator.newInstance(parser).getDataSource("AbacusDB_SELECT_1");
		Assert.assertNotNull(ds2);
		Connection conn2 = ds2.getConnection();
		Assert.assertNotNull(conn2);
		Assert.assertEquals("AbacusDB", conn2.getCatalog());
		conn2.close();
	}

}
