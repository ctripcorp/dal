package com.ctrip.platform.dal.tester.datasource;

import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ctrip.datasource.configure.CtripConnectionStringParser;
import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;
import com.ctrip.platform.dal.dao.configure.DatabaseConfigParser;

public class DatabaseConfigParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testSize() {
		ConnectionStringParser parser = new CtripConnectionStringParser();
		Map<String,String[]> props = DatabaseConfigParser.newInstance(parser).getDBAllInOneConfig();
		Assert.assertNotNull(props);
	}
	
	@Test 
	public void testDecrypt() {
		ConnectionStringParser parser = new CtripConnectionStringParser();
		Map<String,String[]> props = DatabaseConfigParser.newInstance(parser).getDBAllInOneConfig();
		String []prop = props.get("AbacusDB_SEC");
		Assert.assertNotNull(prop);
		Assert.assertEquals("jdbc:sqlserver://devdb.dev.sh.ctriptravel.com:28747;DatabaseName=AbacusDB", prop[0]);
		Assert.assertEquals("uws_AllInOneKey_dev", prop[1]);
		Assert.assertEquals("!QAZ@WSX1qaz2wsx", prop[2]);
	}

	@Test
	public void testNormal() throws Exception {
		ConnectionStringParser parser = new CtripConnectionStringParser();
		Map<String,String[]> props = DatabaseConfigParser.newInstance(parser).getDBAllInOneConfig();
		String []prop = props.get("dao_test");
		Assert.assertNotNull(prop);
		Assert.assertEquals("jdbc:mysql://DST56614:3306/dao_test?useUnicode=true&characterEncoding=UTF-8&rewriteBatchedStatements=true&allowMultiQueries=true", prop[0]);
		Assert.assertEquals("root", prop[1]);
		Assert.assertEquals("!QAZ@WSX1qaz2wsx", prop[2]);
	}

}
