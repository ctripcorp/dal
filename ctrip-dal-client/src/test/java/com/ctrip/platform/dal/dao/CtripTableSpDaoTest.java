package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CtripTableSpDaoTest {

	private final static String DATABASE_NAME = "SimpleShard";
	
	private final static String TABLE_NAME = "People";
	
	private static DalClient client;
	private static DalTableDao<People> dao;
	
	static {
		try {
			DalClientFactory.initClientFactory();
			client = DalClientFactory.getClient(DATABASE_NAME);
			dao = new DalTableDao<>(new PeopleParser(), new CtripSpTaskFactory<People>());
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
		hints.setDetailResults(new DalDetailResults<int[]>());
		dao.insert(hints.inShard(0), p);
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
		hints.setDetailResults(new DalDetailResults<int[]>());
		try {
			dao.combinedInsert(hints.inShard(0), null, p);
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
		hints.setDetailResults(new DalDetailResults<int[]>());
		dao.batchInsert(hints.inShard(0), p);
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
		hints.setDetailResults(new DalDetailResults<int[]>());
		dao.delete(hints.inShard(0), p);
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
		hints.setDetailResults(new DalDetailResults<int[]>());
		dao.batchDelete(hints.inShard(0), p);
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
		hints.setDetailResults(new DalDetailResults<int[]>());
		dao.update(hints.inShard(0), p);
		
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
		hints.setDetailResults(new DalDetailResults<int[]>());
		dao.batchUpdate(hints.inShard(0), p);
		
		for(People p1: p)
			assertEquals("test", dao.queryByPk(p1, hints).getName());
	}
	
	private int getCount(int shardId) throws SQLException {
		return dao.query("1=1", new StatementParameters(), new DalHints().inShard(shardId)).size();
	}
}
