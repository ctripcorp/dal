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

public abstract class BaseBatchDeleteTest {

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
        setOptionTest();

		DalHints hints = new DalHints();
		String[] insertSqls = null;
		insertSqls = new String[6];
		insertSqls[0] = "SET IDENTITY_INSERT "+ TABLE_NAME + " ON";
		insertSqls[1] = "DELETE FROM " + TABLE_NAME;
		for(int i = 0; i < 3; i ++) {
			int id = i;
			insertSqls[i+2] = "INSERT INTO " + TABLE_NAME +" ([PeopleID], [Name], [CityID], [ProvinceID], [CountryID])"
					+ " VALUES(" + id + ", " + "'test name' , 1, 1, 1)";
		}
		insertSqls[5] = "SET IDENTITY_INSERT "+ TABLE_NAME + " OFF";
		client.batchUpdate(insertSqls, hints.inShard(0));
		
		setUpShard();
	}

	public void setUpShard(){
		try {
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 2; j++) {
					String tableName = TABLE_NAME + "_" + j;
					DalHints hints = new DalHints().inShard(i);
					String[] insertSqls = null;
					insertSqls = new String[6];
					insertSqls[0] = "SET IDENTITY_INSERT "+ tableName + " ON";
					insertSqls[1] = "DELETE FROM " + tableName;
					for(int k = 0; k < 3; k ++) {
						int id = k;
						insertSqls[k+2] = "INSERT INTO " + tableName +" ([PeopleID], [Name], [CityID], [ProvinceID], [CountryID])"
								+ " VALUES(" + id + ", " + "'test name' , " + j + ", 1, " + i + ")";
					}
					insertSqls[5] = "SET IDENTITY_INSERT "+ tableName + " OFF";
					client.batchUpdate(insertSqls, hints);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
        return (BulkTask<int[], T>)new CtripTaskFactory().createBatchDeleteTask(parser);
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
					List<People> p = dao.query("1=1", new StatementParameters(), hints);
					Assert.assertTrue(p.size() == 3);
					test.execute(hints, getPojosFields(p, parser), null);
					
					hints = new DalHints().inShard(i).inTableShard(j);
					int c = dao.count("1=1", new StatementParameters(), hints).intValue();
					Assert.assertTrue(c == 0);
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
					List<People> p = dao.query("1=1", new StatementParameters(), hints);
					Assert.assertTrue(p.size() == 3);
					dao.batchDelete(hints, p);
					
					hints = new DalHints().inShard(i).inTableShard(j);
					int c = dao.count("1=1", new StatementParameters(), hints).intValue();
					Assert.assertTrue(c == 0);
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
					List<People> p1 = dao.query("1=1", new StatementParameters(), hints);
					Assert.assertTrue(p1.size() == 3);
					p.addAll(p1);
				}
			}
			dao.batchDelete(new DalHints(), p);
					
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 2; j++) {
					DalHints hints = new DalHints().inShard(i).inTableShard(j);
					int c = dao.count("1=1", new StatementParameters(), hints).intValue();
					Assert.assertTrue(c == 0);
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
