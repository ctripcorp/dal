package com.ctrip.platform.dal.tester.datasource;

import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;
import com.ctrip.platform.dal.dao.configure.DatabaseConfigParser;
import com.ctrip.platform.dal.dao.configure.DefaultConnectionStringParser;

public class ConnectionStringParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test 
	public void testDecrypt() {
		ConnectionStringParser parser = new DefaultConnectionStringParser();
		Map<String,String[]> props = DatabaseConfigParser.newInstance(parser).getDBAllInOneConfig();
		String []prop = props.get("AbacusDB_SEC");
		Assert.assertNotNull(prop);
		Assert.assertEquals("", prop[0]);
		Assert.assertEquals("", prop[1]);
		Assert.assertEquals("", prop[2]);
	}

}
