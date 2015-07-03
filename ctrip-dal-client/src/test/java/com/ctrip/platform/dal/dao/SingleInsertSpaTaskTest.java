package com.ctrip.platform.dal.dao;

import static org.junit.Assert.fail;

import java.sql.SQLException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SingleInsertSpaTaskTest {
	private final static String DATABASE_NAME = "SimpleShard";
	
	private final static String TABLE_NAME = "People";
	
	private static DalClient client;
	static {
		try {
			DalClientFactory.initClientFactory();
			client = DalClientFactory.getClient(DATABASE_NAME);
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
		client.update("DELETE FROM " + TABLE_NAME, new StatementParameters(), new DalHints().inShard(0));
	}

	@After
	public void tearDown() throws Exception {
		client.update("DELETE FROM " + TABLE_NAME, new StatementParameters(), new DalHints().inShard(0));
	}
	
	@Test
	public void testExecute() {
		SingleInsertSpaTask<People> test = new SingleInsertSpaTask<>();
		PeopleParser parser = new PeopleParser();
		test.initialize(parser);
		
		People p1 = new People();
	 	p1.setPeopleID((long)1);
	 	p1.setName("test");
	 	p1.setCityID(-1);
	 	p1.setProvinceID(-1);
	 	p1.setCountryID(-1);

		try {
			KeyHolder keyHolder = new KeyHolder();
			DalHints hints = new DalHints();
			hints.setKeyHolder(keyHolder);
			
			test.execute(hints.inShard(0), parser.getFields(p1));
			Number id1 = keyHolder.getKey();
			Assert.assertTrue(id1.intValue() > 0);
			//----------
			keyHolder = new KeyHolder();
			hints = new DalHints();
			hints.setKeyHolder(keyHolder);
			
			test.execute(hints.inShard(0), parser.getFields(p1));
			Number id2 = keyHolder.getKey();
			Assert.assertTrue(id2.intValue() > 0);
			Assert.assertTrue(id2.intValue() - id1.intValue() == 1);
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
}
