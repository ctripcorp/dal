package com.ctrip.platform.dal.dao.unittests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DalQueryDaoSqlServerTest {
	
	static{
		System.out.println("static");
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("before");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("after");
	}

	@Test
	public void test() {
		System.out.println("test");
	}

}
