package com.ctrip.platform.dal.sql.logging;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CommonUtilTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDesEncrypt() {
		Assert.assertEquals("VnsVK8ZdnkmTwqXTP+zi1g==", CommonUtil.desEncrypt("key1=value1"));
	}

}
