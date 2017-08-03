package com.ctrip.platform.dal.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;

public abstract class BaseCtripTableSpDaoTest {

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
		dao.insert(hints.inShard(0), p);
		assertEquals(6, getCount(0));
	}
	
	@Test
	public void testInsertAsync() throws Exception {
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
		dao.insert(hints.inShard(0).asyncExecution(), p);
		wait(hints);
		assertEquals(6, getCount(0));
	}
	
	@Test
	public void testInsertCallback() throws Exception {
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
		
		dao.insert(hints.inShard(0).callbackWith(callback), p);
		wait(callback);
		assertEquals(6, getCount(0));
	}
	
	private void wait(DalHints hints) throws Exception {
		hints.getAsyncResult().get();
	}

	private void wait(DefaultResultCallback callback) throws Exception {
		while(callback.getResult() == null)
			Thread.sleep(1);
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
		try {
			dao.combinedInsert(hints.inShard(0), null, p);
			fail();
		} catch (Exception e) {
		}
		assertEquals(3, getCount(0));
	}

	@Test
	public void testCombinedInsertAsync() throws Exception {
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
		
		DalHints hints = new DalHints().asyncExecution();
		try {
			dao.combinedInsert(hints.inShard(0), null, p);
			wait(hints);
			fail();
		} catch (Exception e) {
		}
		assertEquals(3, getCount(0));
	}

	@Test
	public void testCombinedInsertCallback() throws Exception {
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
		try {
			dao.combinedInsert(hints.inShard(0).callbackWith(callback), null, p);
			wait(hints);
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
		dao.batchInsert(hints.inShard(0), p);
		assertEquals(6, getCount(0));
	}
	
	@Test
	public void testBatchInsertAsync() throws Exception {
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
		
		DalHints hints = new DalHints().asyncExecution();
		dao.batchInsert(hints.inShard(0), p);
		wait(hints);
		assertEquals(6, getCount(0));
	}
	
	@Test
	public void testBatchInsertCallback() throws Exception {
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
		
		dao.batchInsert(hints.inShard(0).callbackWith(callback), p);
		wait(callback);
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
		dao.delete(hints.inShard(0), p);
		assertEquals(0, getCount(0));
	}
	
	@Test
	public void testDeleteAsync() throws Exception {
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
		
		DalHints hints = new DalHints().asyncExecution();
		dao.delete(hints.inShard(0), p);
		wait(hints);
		assertEquals(0, getCount(0));
	}
	
	@Test
	public void testDeleteCallback() throws Exception {
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

		dao.delete(hints.inShard(0).callbackWith(callback), p);
		wait(callback);
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
		dao.batchDelete(hints.inShard(0), p);
		assertEquals(0, getCount(0));
	}
	
	@Test
	public void testBatchDeleteAsync() throws Exception {
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
		
		DalHints hints = new DalHints().asyncExecution();
		dao.batchDelete(hints.inShard(0), p);
		wait(hints);
		assertEquals(0, getCount(0));
	}
	
	@Test
	public void testBatchDeleteCallback() throws Exception {
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

		dao.batchDelete(hints.inShard(0).callbackWith(callback), p);
		wait(callback);
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
		dao.update(hints.inShard(0), p);
		
		for(People p1: p)
			assertEquals("test", dao.queryByPk(p1, hints).getName());
	}
	
	@Test
	public void testUpdateAsync() throws Exception {
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
		
		DalHints hints = new DalHints().asyncExecution();
		dao.update(hints.inShard(0), p);
		wait(hints);
		
		for(People p1: p)
			assertEquals("test", dao.queryByPk(p1, new DalHints().inShard(0)).getName());
	}
	
	@Test
	public void testUpdateCallback() throws Exception {
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
		
		dao.update(hints.inShard(0).callbackWith(callback), p);
		wait(callback);
		for(People p1: p)
			assertEquals("test", dao.queryByPk(p1, new DalHints().inShard(0)).getName());
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
		dao.batchUpdate(hints.inShard(0), p);
		
		for(People p1: p)
			assertEquals("test", dao.queryByPk(p1, hints).getName());
	}
	
	@Test
	public void testBatchUpdateAsync() throws Exception {
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
		
		DalHints hints = new DalHints().asyncExecution();
		dao.batchUpdate(hints.inShard(0), p);
		wait(hints);
		
		for(People p1: p)
			assertEquals("test", dao.queryByPk(p1, new DalHints().inShard(0)).getName());
	}

	@Test
	public void testBatchUpdateCallback() throws Exception {
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

		dao.batchUpdate(hints.inShard(0).callbackWith(callback), p);
		wait(callback);
		
		for(People p1: p)
			assertEquals("test", dao.queryByPk(p1, new DalHints().inShard(0)).getName());
	}

	private int getCount(int shardId) throws SQLException {
		return dao.query("1=1", new StatementParameters(), new DalHints().inShard(shardId)).size();
	}
}
