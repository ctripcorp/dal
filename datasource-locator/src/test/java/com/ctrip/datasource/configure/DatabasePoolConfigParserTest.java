package com.ctrip.datasource.configure;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatabasePoolConfigParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void test() {
		String location = DatabasePoolConfigParser.getInstance().getDatabaseConfigLocation();
		Assert.assertEquals("$classpath", location);
	}

	@Test
	public void test1() {
		DatabasePoolConifg config = DatabasePoolConfigParser.getInstance().getDatabasePoolConifg("dao_test");
		Assert.assertEquals("dao_test", config.getName());
		Assert.assertEquals(500, config.getMaxWait());
		Assert.assertEquals("rewriteBatchedStatements=true;allowMultiQueries=true", config.getOption());
	}
	
	@Test
	public void test2() {
		DatabasePoolConifg config = DatabasePoolConfigParser.getInstance().getDatabasePoolConifg("dao_test_select");
		Assert.assertEquals("dao_test_select", config.getName());
		Assert.assertEquals(true, config.isTestWhileIdle());
		Assert.assertEquals(true, config.isTestOnBorrow());
		Assert.assertEquals("SELECT 1", config.getValidationQuery());
		Assert.assertEquals(30000, config.getValidationInterval());
		Assert.assertEquals(30000, config.getTimeBetweenEvictionRunsMillis());
		Assert.assertEquals(100, config.getMaxActive());
		Assert.assertEquals(10, config.getMinIdle());
		Assert.assertEquals(1000, config.getMaxWait());
		Assert.assertEquals(10, config.getInitialSize());
		Assert.assertEquals(60, config.getRemoveAbandonedTimeout());
		Assert.assertEquals(true, config.isRemoveAbandoned());
		Assert.assertEquals(true, config.isLogAbandoned());
		Assert.assertEquals(30000, config.getMinEvictableIdleTimeMillis());
		Assert.assertEquals("rewriteBatchedStatements=true;allowMultiQueries=true", config.getOption());
	}
	
	@Test
	public void test3() {
		DatabasePoolConifg config = DatabasePoolConfigParser.getInstance().getDatabasePoolConifg("HotelPubDB");
		Assert.assertEquals("HotelPubDB", config.getName());
		Assert.assertEquals(10000, config.getMaxWait());
		Assert.assertEquals("sendTimeAsDateTime=false", config.getOption());
	}
	
	@Test
	public void test4() {
		DatabasePoolConifg config = DatabasePoolConfigParser.getInstance().getDatabasePoolConifg("dao_test_1");
		Assert.assertEquals("dao_test_1", config.getName());
		Assert.assertEquals(500, config.getMaxWait());
		Assert.assertEquals("rewriteBatchedStatements=true;allowMultiQueries=true", config.getConnectionProperties());
	}

}
