package com.ctrip.datasource.configure;

import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatabaseConfigParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testSize() {
		Map<String,String[]> props = DatabaseConfigParser.newInstance().getDBAllInOneConfig();
		Assert.assertEquals(12, props.size());
	}
	
	@Test 
	public void testDecrypt() {
		Map<String,String[]> props = DatabaseConfigParser.newInstance().getDBAllInOneConfig();
		String []prop = props.get("AbacusDB_SEC");
		Assert.assertNotNull(prop);
		Assert.assertEquals("jdbc:sqlserver://devdb.dev.sh.ctriptravel.com:28747;DatabaseName=AbacusDB", prop[0]);
		Assert.assertEquals("uws_AllInOneKey_dev", prop[1]);
		Assert.assertEquals("!QAZ@WSX1qaz2wsx", prop[2]);
	}

	@Test
	public void testNormal() throws Exception {
		Map<String,String[]> props = DatabaseConfigParser.newInstance().getDBAllInOneConfig();
		String []prop = props.get("dao_test");
		Assert.assertNotNull(prop);
		Assert.assertEquals("jdbc:mysql://192.168.83.132:3306/dao_test?"
				+ "useUnicode=true&characterEncoding=UTF-8&rewriteBatchedStatements=true"
				+ "&allowMultiQueries=true", prop[0]);
		Assert.assertEquals("root", prop[1]);
		Assert.assertEquals("platform_2014", prop[2]);
	}

}
