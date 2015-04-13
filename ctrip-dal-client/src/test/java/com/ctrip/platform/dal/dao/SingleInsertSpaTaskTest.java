package com.ctrip.platform.dal.dao;

import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SingleInsertSpaTaskTest {
	static {
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
	public void testExecute() {
		SingleInsertSpaTask<People> test = new SingleInsertSpaTask<>(new String[]{"PeopleID"}, null);
		PeopleParser parser = new PeopleParser();
		test.initialize(parser);
		
		People p1 = new People();
	 	p1.setPeopleID((long)1);
	 	p1.setName("test");
	 	p1.setCityID(-1);
	 	p1.setProvinceID(-1);
	 	p1.setCountryID(-1);

		try {
			test.execute(new DalHints().inShard(0), parser.getFields(p1));
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
