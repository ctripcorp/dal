package com.ctrip.platform.dal.async.dao;

import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.SingleInsertSpaTask;
import com.ctrip.platform.dal.dao.StatementParameters;

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
			test.execute(new DalHints().inShard(0).asyncExecuteCUD(), parser.getFields(p1));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
}
