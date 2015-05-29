package com.ctrip.platform.dal.async.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.BatchInsertSp3Task;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalDetailResults;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.task.DalAsyncCallback;

public class BatchInsertSp3AsyncTaskTest {

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
		BatchInsertSp3Task<People> test = new BatchInsertSp3Task<>();
		PeopleParser parser = new PeopleParser();
		test.initialize(parser);
		
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
			DalAsyncCallback callback = new DalAsyncCallback();
			hints.asyncExecution().setDalAsyncCallback(callback);
			test.execute(hints.inShard(0), getPojosFields(p, parser));
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
