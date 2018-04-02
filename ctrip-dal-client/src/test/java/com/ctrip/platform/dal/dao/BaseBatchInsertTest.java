package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.task.BulkTask;

public abstract class BaseBatchInsertTest {
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
	    CtripTaskFactory.callSpbySqlServerSyntax = false;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	    setOptionTest();
		client.update("DELETE FROM " + TABLE_NAME, new StatementParameters(), new DalHints().inShard(0));
	}

	@After
	public void tearDown() throws Exception {
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
	
    public abstract void setOptionTest();

	private <T> BulkTask<int[], T> getTest(DalParser<T> parser) {
	    return (BulkTask<int[], T>)new CtripTaskFactory().createBatchInsertTask(parser);
	}
        
	@Test
	public void testExecute() {
		PeopleParser parser = new PeopleParser();
        BulkTask<int[], People> test = getTest(parser);
		
		List<People> p = new ArrayList<>();
		
		for(int i = 0; i < 3; i++) {
			People p1 = new People();
		 	p1.setPeopleID((long)i);
		 	p1.setName("test");
		 	p1.setCityID(-1);
		 	p1.setProvinceID(-1);
		 	p1.setCountryID(-1);
		 	p.add(p1);
		}
		
		try {
			DalHints hints = new DalHints();
			test.execute(hints.inShard(0), getPojosFields(p, parser), null);
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testExecuteShard() {
		PeopleParser parser = new PeopleParser("SimpleDbTableShard");
		DalTableDao<People> dao = new DalTableDao<>(parser);
		BulkTask<int[], People> test = getTest(parser);
		
		try {
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 2; j++) {
					DalHints hints = new DalHints().inShard(i).inTableShard(j);
					int c = dao.count("1=1", new StatementParameters(), hints).intValue();
					Assert.assertTrue(c == 0);
					
					List<People> p = new ArrayList<>();
					for(int k = 0; k < 3; k++) {
						People p1 = new People();
						// for SPT case, PK can not be same for TVP value
						p1.setPeopleID((long)k);//p1.setPeopleID((long)i);
					 	p1.setName("test");
					 	p1.setCityID(j);
					 	p1.setProvinceID(-1);
					 	p1.setCountryID(i);
					 	p.add(p1);
					}

					hints = new DalHints().inShard(i).inTableShard(j);
					test.execute(hints, getPojosFields(p, parser), null);
					
					hints = new DalHints().inShard(i).inTableShard(j);
					c = dao.count("1=1", new StatementParameters(), hints).intValue();
					Assert.assertTrue(c == 3);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail();
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
					Assert.assertTrue(c == 0);
					
					List<People> p = new ArrayList<>();
					for(int k = 0; k < 3; k++) {
						People p1 = new People();
                        // for SPT case, PK can not be same for TVP value
                        p1.setPeopleID((long)k);//p1.setPeopleID((long)i);
					 	p1.setName("test");
					 	p1.setCityID(j);
					 	p1.setProvinceID(-1);
					 	p1.setCountryID(i);
					 	p.add(p1);
					}

					dao.batchInsert(new DalHints(), p);
					
					hints = new DalHints().inShard(i).inTableShard(j);
					c = dao.count("1=1", new StatementParameters(), hints).intValue();
					Assert.assertTrue(c == 3);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testExecuteShardByDao2() {
		PeopleParser parser = new PeopleParser("SimpleDbTableShard");
		DalTableDao<People> dao = new DalTableDao<>(parser);

		try {
			List<People> p = new ArrayList<>();
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 2; j++) {
					DalHints hints = new DalHints().inShard(i).inTableShard(j);
					int c = dao.count("1=1", new StatementParameters(), hints).intValue();
					Assert.assertTrue(c == 0);
					
					for(int k = 0; k < 3; k++) {
						People p1 = new People();
                        // for SPT case, PK can not be same for TVP value
                        p1.setPeopleID((long)k);//p1.setPeopleID((long)i);
					 	p1.setName("test");
					 	p1.setCityID(j);
					 	p1.setProvinceID(-1);
					 	p1.setCountryID(i);
					 	p.add(p1);
					}
				}
			}
			dao.batchInsert(new DalHints(), p);
			
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 2; j++) {
					DalHints hints = new DalHints().inShard(i).inTableShard(j);
					int c = dao.count("1=1", new StatementParameters(), hints).intValue();
					Assert.assertTrue(c == 3);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	private <T> Map<Integer, Map<String, ?>> getPojosFields(List<T> daoPojos, DalParser<T> parser) {
		Map<Integer, Map<String, ?>> pojoFields = new HashMap<>();
		if (null == daoPojos || daoPojos.size() < 1)
			return pojoFields;
		
		int i = 0;
		for (T pojo: daoPojos){
			pojoFields.put(i++, parser.getFields(pojo));
		}
		
		return pojoFields;
	}
}