package com.ctrip.platform.dal.async.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalDetailResults;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;
import com.ctrip.platform.dal.dao.task.DalAsyncCallback;

public class CtripTableSpAsyncDaoTest {

	private final static String DATABASE_NAME = "SimpleShard";
	
	private final static String TABLE_NAME = "People";
	
	private static DalClient client;
	private static DalTableDao<People> dao;
	
	static {
		try {
			DalClientFactory.initClientFactory();
			client = DalClientFactory.getClient(DATABASE_NAME);
			dao = new DalTableDao<>(new PeopleParser());
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
	}

	@After
	public void tearDown() throws Exception {
		client.update("DELETE FROM " + TABLE_NAME, new StatementParameters(), new DalHints().inShard(0));
	}
	
	@Test
	public void testInsert() throws Exception {
		List<People> p = new ArrayList<>();
		
		int oldCount = getCount(0);
		for(int i = 0; i < 3; i++) {
			People p1 = new People();
		 	p1.setPeopleID((long)i);
		 	p1.setName("test");
		 	p1.setCityID(-1);
		 	p1.setProvinceID(-1);
		 	p1.setCountryID(-1);
		 	p.add(p1);
		}
		
		DalHints hints = new DalHints();
		DalAsyncCallback callback = new DalAsyncCallback();
		hints.asyncExecution().setDalAsyncCallback(callback);
		dao.insert(hints.inShard(0), p);
		int[]affected = (int[]) callback.getFuture().get();
		assertEquals(3, affected.length);
		assertEquals(6, getCount(0));
	}
	
	@Test
	public void testCombinedInsert() throws Exception {
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
		
		DalHints hints = new DalHints();
		DalAsyncCallback callback = new DalAsyncCallback();
		hints.asyncExecution().setDalAsyncCallback(callback);
		try {
			dao.combinedInsert(hints.inShard(0), null, p);
			int affected = (Integer) hints.getDalAsyncCallback().getFuture().get();
			assertEquals(3, affected);
			fail();
		} catch (Exception e) {
		}
		assertEquals(3, getCount(0));
	}

	@Test
	public void testBatchInsert() throws Exception {
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
		
		DalHints hints = new DalHints();
		DalAsyncCallback callback = new DalAsyncCallback();
		hints.asyncExecution().setDalAsyncCallback(callback);
		dao.batchInsert(hints.inShard(0), p);
		int[]affected = (int[]) callback.getFuture().get();
		assertEquals(3, affected.length);
		assertEquals(6, getCount(0));
	}
	
	@Test
	public void testDelete() throws Exception {
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
		
		DalHints hints = new DalHints();
		DalAsyncCallback callback = new DalAsyncCallback();
		hints.asyncExecution().setDalAsyncCallback(callback);
		dao.delete(hints.inShard(0), p);
		int[]affected = (int[]) callback.getFuture().get();
		assertEquals(3, affected.length);
		assertEquals(0, getCount(0));
	}
	
	@Test
	public void testBatchDelete() throws Exception {
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
		
		DalHints hints = new DalHints();
		DefaultResultCallback callback = new DefaultResultCallback();
		hints.callbackWith(callback);
		dao.batchDelete(hints.inShard(0), p);
		
		Thread.sleep(100);
		Integer i = (Integer)callback.getResult();
		
		assertEquals(0, getCount(0));
	}
	
	@Test
	public void testUpdate() throws Exception {
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
		
		DalHints hints = new DalHints();
		DalAsyncCallback callback = new DalAsyncCallback();
		hints.asyncExecution().setDalAsyncCallback(callback);
		dao.update(hints.inShard(0), p);
		callback.getFuture().get();
		for(People p1: p)
			assertEquals("test", dao.queryByPk(p1, hints).getName());
	}
	
	@Test
	public void testBatchUpdate() throws Exception {
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
		
		DalHints hints = new DalHints();
		DalAsyncCallback callback = new DalAsyncCallback();
		hints.asyncExecution().setDalAsyncCallback(callback);
		dao.batchUpdate(hints.inShard(0), p);
		
		callback.getFuture().get();
		for(People p1: p)
			assertEquals("test", dao.queryByPk(p1, hints).getName());
	}
	
	private int getCount(int shardId) throws SQLException {
		return dao.query("1=1", new StatementParameters(), new DalHints().inShard(shardId)).size();
	}
}
