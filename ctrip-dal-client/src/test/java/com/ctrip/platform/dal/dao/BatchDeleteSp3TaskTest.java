package com.ctrip.platform.dal.dao;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BatchDeleteSp3TaskTest {

	private final static String DATABASE_NAME_MYSQL = "SimpleShard";
	
	private final static String TABLE_NAME = "People";
	
	private static DalClient client;
	static {
		try {
			DalClientFactory.initClientFactory();
			client = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
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
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		insertSqls = new String[3];
		for(int i = 0; i < 3; i ++) {
			int id = i;
			insertSqls[i] = "INSERT INTO " + TABLE_NAME +"[PeopleID], [Name] ,[CityID] ,[ProvinceID] ,[CountryID])"
					+ " VALUES(" + id + ", " + "test name" + ", " + i + ",1, 1)";
		}
		client.batchUpdate(insertSqls, hints);
	}

	@After
	public void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			client.update(sql, parameters, hints.inShard(0));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testExecute() {
		BatchDeleteSp3Task<People> test = new BatchDeleteSp3Task<>();
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
	
	private <T> List<Map<String, ?>> getPojosFields(List<T> daoPojos) {
		List<Map<String, ?>> pojoFields = new LinkedList<Map<String, ?>>();
		if (null == daoPojos || daoPojos.size() < 1)
			return pojoFields;
		
		for (T pojo: daoPojos){
			pojoFields.add(parser.getFields(pojo));
		}
		
		return pojoFields;
	}

}
