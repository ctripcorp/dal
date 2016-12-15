package com.ctrip.platform.dal.dao;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
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
		tearDownShard();
	}
	
	public void tearDownShard() throws Exception {
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2; j++) {
				String tableName = TABLE_NAME + "_" + j;
				client.update("DELETE FROM " + tableName, new StatementParameters(), new DalHints().inShard(i));
			}
		}
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
			assertTrue(id1.intValue() > 0);
			//----------
			keyHolder = new KeyHolder();
			hints = new DalHints();
			hints.setKeyHolder(keyHolder);
			
			test.execute(hints.inShard(0), parser.getFields(p1));
			Number id2 = keyHolder.getKey();
			assertTrue(id2.intValue() > 0);
			assertTrue(id2.intValue() - id1.intValue() == 1);
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testExecuteShard() {
		SingleInsertSpaTask<People> test = new SingleInsertSpaTask<>();
		PeopleParser parser = new PeopleParser("SimpleDbTableShard");
		DalTableDao<People> dao = new DalTableDao<>(parser);
		test.initialize(parser);

		try {
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 2; j++) {
					DalHints hints = new DalHints().inShard(i).inTableShard(j);
					int c = dao.count("1=1", new StatementParameters(), hints).intValue();
					assertTrue(c == 0);
					
					People p1 = new People();
				 	p1.setPeopleID((long)i);
				 	p1.setName("test");
				 	p1.setCityID(j);
				 	p1.setProvinceID(-1);
				 	p1.setCountryID(i);
					
					KeyHolder keyHolder = new KeyHolder();
					hints = new DalHints();
					hints.setKeyHolder(keyHolder);
					
					test.execute(hints, parser.getFields(p1));
					Number id1 = keyHolder.getKey();
					assertTrue(id1.intValue() > 0);
					//----------
					keyHolder = new KeyHolder();
					hints = new DalHints();
					hints.setKeyHolder(keyHolder);
					
					test.execute(hints, parser.getFields(p1));
					Number id2 = keyHolder.getKey();
					assertTrue(id2.intValue() > 0);
					assertTrue(id2.intValue() - id1.intValue() == 1);

					hints = new DalHints().inShard(i).inTableShard(j);
					c = dao.count("1=1", new StatementParameters(), hints).intValue();
					assertTrue(c == 2);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testExecuteShardByDao() {
		PeopleParser parser = new PeopleParser("SimpleDbTableShard");
		DalTableDao<People> dao = new DalTableDao<>(parser);

		try {
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 2; j++) {
					DalHints hints = new DalHints().inShard(i).inTableShard(j);
					int c = dao.count("1=1", new StatementParameters(), hints).intValue();
					assertTrue(c == 0);
					
					People p1 = new People();
				 	p1.setPeopleID((long)i);
				 	p1.setName("test");
				 	p1.setCityID(j);
				 	p1.setProvinceID(-1);
				 	p1.setCountryID(i);
					
					KeyHolder keyHolder = new KeyHolder();
					hints = new DalHints();
					hints.setKeyHolder(keyHolder);
					
					dao.insert(hints, p1);
					Number id1 = keyHolder.getKey();
					assertTrue(id1.intValue() > 0);
					//----------
					keyHolder = new KeyHolder();
					hints = new DalHints();
					hints.setKeyHolder(keyHolder);
					
					dao.insert(hints, p1);
					Number id2 = keyHolder.getKey();
					assertTrue(id2.intValue() > 0);
					assertTrue(id2.intValue() - id1.intValue() == 1);

					hints = new DalHints().inShard(i).inTableShard(j);
					c = dao.count("1=1", new StatementParameters(), hints).intValue();
					assertTrue(c == 2);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testExecuteShardByDao2() {
		PeopleParser parser = new PeopleParser("SimpleDbTableShard");
		DalTableDao<People> dao = new DalTableDao<>(parser);

		try {
			List<People> pAll = new ArrayList<>();
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 2; j++) {
					DalHints hints = new DalHints().inShard(i).inTableShard(j);
					int c = dao.count("1=1", new StatementParameters(), hints).intValue();
					assertTrue(c == 0);
					
					for(int k = 0; k < 3; k++) {
						People p1 = new People();
					 	p1.setPeopleID((long)i);
					 	p1.setName("test");
					 	p1.setCityID(j);
					 	p1.setProvinceID(-1);
					 	p1.setCountryID(i);
					 	pAll.add(p1);
					}
				}
			}

			KeyHolder keyHolder = new KeyHolder();
			DalHints hints = new DalHints();
			hints.setKeyHolder(keyHolder);
			dao.insert(hints, pAll);
			
			assertTrue(keyHolder.size() == 12);

			int prev = -1;
			int index = 0;
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 2; j++) {
					hints = new DalHints().inShard(i).inTableShard(j);
					int c = dao.count("1=1", new StatementParameters(), hints).intValue();
					Assert.assertTrue(c == 3);
					
					for(int k = 0; k < 3; k++) {
//						assertTrue(keyHolder.getKey(index).intValue() > prev);
						prev= keyHolder.getKey(index++).intValue();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
}
