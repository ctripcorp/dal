package com.ctrip.platform.dal.codegen;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

/**
 * JUnit test of FreePersonDaoDao class.
**/
public class FreePersonDaoDaoUnitTest {

	private static final String DATA_BASE = "MySqlSimpleShard";
	//ShardColModShardStrategy;columns=CountryID;mod=2;tableColumns=CityID;tableMod=4;separator=_;shardedTables=person

	private static DalTableDao<Person> pdao;
	private static FreePersonDaoDao dao = null;
	private static DalClient client = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/**
		* Initialize DalClientFactory.
		* The Dal.config can be specified from class-path or local file path.
		* One of follow three need to be enabled.
		**/
		//DalClientFactory.initPrivateFactory(); //Load from class-path connections.properties
		DalClientFactory.initClientFactory(); // load from class-path Dal.config
		//DalClientFactory.initClientFactory("E:/DalMult.config"); // load from the specified Dal.config file path
		
		client = DalClientFactory.getClient(DATA_BASE);
		dao = new FreePersonDaoDao();
		DalParser<Person> parser = new DalDefaultJpaParser<>(Person.class, "MySqlSimpleShard", "PERSON");
		pdao = new DalTableDao<>(parser);
	}
	
	
	@Before
	public void setUp() throws Exception {
		tearDown();
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 4; j++) {
				try {
					pdao.insert(new DalHints().enableIdentityInsert(), createPojo(i, j, j+1));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private Person createPojo(int countryID, int cityID, int id) {
		Person daoPojo;

		daoPojo = new Person();
		daoPojo.setCountryID(countryID);
		daoPojo.setCityID(cityID);
		daoPojo.setName("Test");
		daoPojo.setPeopleID(id);
		
		return daoPojo;
	}
	
	@After
	public void tearDown() throws Exception {
		for(int i = 0; i < 2; i++) {
			pdao.delete(new DalHints().inShard(i), pdao.query("1=1", new StatementParameters(), new DalHints().inShard(i)));
		}
	} 
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	
	@Test
	public void testupdate() throws Exception {
		String name = "Abc";// Test value here
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);

		List<Integer> countryIds = new ArrayList<>();
		countryIds.add(0);
		countryIds.add(1);
		countryIds.add(2);
		countryIds.add(3);
	    
		int ret = dao.update(name, cityIds, countryIds, new DalHints());
	    assertEquals(3, ret);
	    
	    DalHints hints = new DalHints();
		ret = dao.update(name, cityIds, countryIds, hints.inShard(1));
	    assertEquals(3, ret);

	    hints = new DalHints();
		ret = dao.update(name, cityIds, countryIds, hints.inShard(0));
	    assertEquals(3, ret);

	    hints = new DalHints();
		ret = dao.update(name, cityIds, countryIds, hints.shardBy("countryID"));
	    assertEquals(6, ret);
	    
	    hints = new DalHints();
		ret = dao.update(name, cityIds, countryIds, hints.inAllShards());
	    assertEquals(6, ret);

	    hints = new DalHints();
		ret = dao.update(name, cityIds, countryIds, hints.inAllShards().asyncExecution());
	    assertEquals(0, ret);
	    assertEquals(6, hints.getIntResult());

	    hints = new DalHints();
	    Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
		ret = dao.update(name, cityIds, countryIds, hints.inShards(shards));
	    assertEquals(6, ret);
	    
	    hints = new DalHints();
	    shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
		ret = dao.update(name, cityIds, countryIds, hints.inShards(shards).asyncExecution());
		assertEquals(0, ret);
	    assertEquals(6, hints.getIntResult());
	}
	
	@Test
	public void testfindFreeFirst() throws Exception {
		String name = "Test";// Test value here
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);
		DalHints hints = new DalHints();
		
		FreeEntityPojo ret;
		try {
			hints = new DalHints();
			ret = dao.findFreeFirst(name, cityIds, hints);
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		ret = dao.findFreeFirst(name, cityIds, hints.inShard(1));
		assertNotNull(ret);
		
		hints = new DalHints();
		ret = dao.findFreeFirst(name, cityIds, hints.inShard(0));
		assertNotNull(ret);
		
		hints = new DalHints();
		ret = dao.findFreeFirst(name, cityIds, hints.inAllShards());
		assertNotNull(ret);
		
		hints = new DalHints();
		Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    ret = dao.findFreeFirst(name, cityIds, hints.inShards(shards));
	    assertNotNull(ret);
		
		// async
		hints = new DalHints();
		hints = new DalHints();
		ret = dao.findFreeFirst(name, cityIds, hints.asyncExecution());
		assertNull(ret);
		try{
			hints.getResult();
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		ret = dao.findFreeFirst(name, cityIds, hints.inShard(1).asyncExecution());
		assertNull(ret);
		assertNotNull(hints.getResult());
		
		hints = new DalHints();
		ret = dao.findFreeFirst(name, cityIds, hints.inShard(0).asyncExecution());
		assertNull(ret);
		assertNotNull(hints.getResult());
		
		hints = new DalHints();
		ret = dao.findFreeFirst(name, cityIds, hints.inAllShards().asyncExecution());
		assertNull(ret);
		assertNotNull(hints.getResult());
		
		hints = new DalHints();
		shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    ret = dao.findFreeFirst(name, cityIds, hints.inShards(shards).asyncExecution());
		assertNull(ret);
		assertNotNull(hints.getResult());
	}
	
	@Test
	public void testfindFreeList() throws Exception {
		String name = "Test";// Test value here
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);
		DalHints hints = new DalHints();
		
		List<FreeEntityPojo> ret;
		try {
			hints = new DalHints();
			ret = dao.findFreeList(name, cityIds, hints);
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		ret = dao.findFreeList(name, cityIds, hints.inShard(1));
		assertEquals(3, ret.size());
		
		hints = new DalHints();
		ret = dao.findFreeList(name, cityIds, hints.inShard(0));
		assertEquals(3, ret.size());
		
		hints = new DalHints();
		ret = dao.findFreeList(name, cityIds, hints.inAllShards());
		assertEquals(6, ret.size());
		
		hints = new DalHints();
		Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    ret = dao.findFreeList(name, cityIds, hints.inShards(shards));
	    assertEquals(6, ret.size());
		
		// async
		hints = new DalHints();
		hints = new DalHints();
		ret = dao.findFreeList(name, cityIds, hints.asyncExecution());
		assertNull(ret);
		try{
			hints.getResult();
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		ret = dao.findFreeList(name, cityIds, hints.inShard(1).asyncExecution());
		assertNull(ret);
		assertEquals(3, ((List)hints.getResult()).size());
		
		hints = new DalHints();
		ret = dao.findFreeList(name, cityIds, hints.inShard(0).asyncExecution());
		assertNull(ret);
		assertEquals(3, ((List)hints.getResult()).size());
		
		hints = new DalHints();
		ret = dao.findFreeList(name, cityIds, hints.inAllShards().asyncExecution());
		assertNull(ret);
		assertEquals(6, ((List)hints.getResult()).size());
		
		hints = new DalHints();
		shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    ret = dao.findFreeList(name, cityIds, hints.inShards(shards).asyncExecution());
		assertNull(ret);
		assertEquals(6, ((List)hints.getResult()).size());
	}
	
	@Test
	public void testfindFreeListPage() throws Exception {
		String name = "Test";// Test value here
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);
		DalHints hints = new DalHints();
		
		List<FreeEntityPojo> ret;
		try {
			hints = new DalHints();
			ret = dao.findFreeListPage(name, cityIds, 2, 2, hints);
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		ret = dao.findFreeListPage(name, cityIds, 2, 2, hints.inShard(1));
		assertEquals(1, ret.size());
		
		hints = new DalHints();
		ret = dao.findFreeListPage(name, cityIds, 2, 2, hints.inShard(0));
		assertEquals(1, ret.size());
		
		hints = new DalHints();
		ret = dao.findFreeListPage(name, cityIds, 2, 2, hints.inAllShards());
		assertEquals(2, ret.size());
		
		hints = new DalHints();
		Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    ret = dao.findFreeListPage(name, cityIds, 2, 2, hints.inShards(shards));
	    assertEquals(2, ret.size());
		
		// async
		hints = new DalHints();
		hints = new DalHints();
		ret = dao.findFreeListPage(name, cityIds, 2, 2, hints.asyncExecution());
		assertNull(ret);
		try{
			hints.getResult();
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		ret = dao.findFreeListPage(name, cityIds, 2, 2, hints.inShard(1).asyncExecution());
		assertNull(ret);
		assertEquals(1, ((List)hints.getResult()).size());
		
		hints = new DalHints();
		ret = dao.findFreeListPage(name, cityIds, 2, 2, hints.inShard(0).asyncExecution());
		assertNull(ret);
		assertEquals(1, ((List)hints.getResult()).size());
		
		hints = new DalHints();
		ret = dao.findFreeListPage(name, cityIds, 2, 2, hints.inAllShards().asyncExecution());
		assertNull(ret);
		assertEquals(2, ((List)hints.getResult()).size());
		
		hints = new DalHints();
		shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    ret = dao.findFreeListPage(name, cityIds, 2, 2, hints.inShards(shards).asyncExecution());
		assertNull(ret);
		assertEquals(2, ((List)hints.getResult()).size());
	}
	
	@Test
	public void testfindFreeSingle() throws Exception {
		String name = "Test";// Test value here
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(22);
		cityIds.add(33);
		DalHints hints = new DalHints();
		
		FreeEntityPojo ret;
		try {
			hints = new DalHints();
			ret = dao.findFreeSingle(name, cityIds, hints);
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		ret = dao.findFreeSingle(name, cityIds, hints.inShard(1));
		assertNotNull(ret);
		
		hints = new DalHints();
		ret = dao.findFreeSingle(name, cityIds, hints.inShard(0));
		assertNotNull(ret);
		
		hints = new DalHints();
		try {
			ret = dao.findFreeSingle(name, cityIds, hints.inAllShards());
			fail();
		} catch (Exception e1) {
		}
		
		hints = new DalHints();
		Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
		try {
		    ret = dao.findFreeSingle(name, cityIds, hints.inShards(shards));
			fail();
		} catch (Exception e1) {
		}
		
		// async
		hints = new DalHints();
		hints = new DalHints();
		ret = dao.findFreeSingle(name, cityIds, hints.asyncExecution());
		assertNull(ret);
		try{
			hints.getResult();
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		ret = dao.findFreeSingle(name, cityIds, hints.inShard(1).asyncExecution());
		assertNull(ret);
		assertNotNull(hints.getResult());
		
		hints = new DalHints();
		ret = dao.findFreeSingle(name, cityIds, hints.inShard(0).asyncExecution());
		assertNull(ret);
		assertNotNull(hints.getResult());
		
		hints = new DalHints();
		ret = dao.findFreeSingle(name, cityIds, hints.inAllShards().asyncExecution());
		assertNull(ret);
		try{
			hints.getResult();
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    ret = dao.findFreeSingle(name, cityIds, hints.inShards(shards).asyncExecution());
		assertNull(ret);
		try{
			hints.getResult();
			fail();
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testfindFreeFieldFirst() throws Exception {
		String name = "Test";// Test value here
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);
		DalHints hints = new DalHints();
		
		String ret;
		try {
			hints = new DalHints();
			ret = dao.findFreeFieldFirst(name, cityIds, hints);
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		ret = dao.findFreeFieldFirst(name, cityIds, hints.inShard(1));
		assertEquals("Test", ret);
		
		hints = new DalHints();
		ret = dao.findFreeFieldFirst(name, cityIds, hints.inShard(0));
		assertEquals("Test", ret);
		
		hints = new DalHints();
		ret = dao.findFreeFieldFirst(name, cityIds, hints.inAllShards());
		assertEquals("Test", ret);
		
		hints = new DalHints();
		Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    ret = dao.findFreeFieldFirst(name, cityIds, hints.inShards(shards));
	    assertEquals("Test", ret);
		
		// async
		hints = new DalHints();
		hints = new DalHints();
		ret = dao.findFreeFieldFirst(name, cityIds, hints.asyncExecution());
		assertNull(ret);
		try{
			hints.getResult();
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		ret = dao.findFreeFieldFirst(name, cityIds, hints.inShard(1).asyncExecution());
		assertNull(ret);
		assertEquals("Test", hints.getResult());
		
		hints = new DalHints();
		ret = dao.findFreeFieldFirst(name, cityIds, hints.inShard(0).asyncExecution());
		assertNull(ret);
		assertEquals("Test", hints.getResult());
		
		hints = new DalHints();
		ret = dao.findFreeFieldFirst(name, cityIds, hints.inAllShards().asyncExecution());
		assertNull(ret);
		assertEquals("Test", hints.getResult());
		
		hints = new DalHints();
		shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    ret = dao.findFreeFieldFirst(name, cityIds, hints.inShards(shards).asyncExecution());
		assertNull(ret);
		assertEquals("Test", hints.getResult());
	}
	
	@Test
	public void testfindFreeFieldList() throws Exception {
		String name = "Test";// Test value here
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);
		DalHints hints = new DalHints();
		
		List<String> ret;
		try {
			hints = new DalHints();
			ret = dao.findFreeFieldList(name, cityIds, hints);
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		ret = dao.findFreeFieldList(name, cityIds, hints.inShard(1));
		assertEquals(3, ret.size());
		
		hints = new DalHints();
		ret = dao.findFreeFieldList(name, cityIds, hints.inShard(0));
		assertEquals(3, ret.size());
		
		hints = new DalHints();
		ret = dao.findFreeFieldList(name, cityIds, hints.inAllShards());
		assertEquals(6, ret.size());
		
		hints = new DalHints();
		Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    ret = dao.findFreeFieldList(name, cityIds, hints.inShards(shards));
	    assertEquals(6, ret.size());
		
		// async
		hints = new DalHints();
		hints = new DalHints();
		ret = dao.findFreeFieldList(name, cityIds, hints.asyncExecution());
		assertNull(ret);
		try{
			hints.getResult();
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		ret = dao.findFreeFieldList(name, cityIds, hints.inShard(1).asyncExecution());
		assertNull(ret);
		assertEquals(3, ((List)hints.getResult()).size());
		
		hints = new DalHints();
		ret = dao.findFreeFieldList(name, cityIds, hints.inShard(0).asyncExecution());
		assertNull(ret);
		assertEquals(3, ((List)hints.getResult()).size());
		
		hints = new DalHints();
		ret = dao.findFreeFieldList(name, cityIds, hints.inAllShards().asyncExecution());
		assertNull(ret);
		assertEquals(6, ((List)hints.getResult()).size());
		
		hints = new DalHints();
		shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    ret = dao.findFreeFieldList(name, cityIds, hints.inShards(shards).asyncExecution());
		assertNull(ret);
		assertEquals(6, ((List)hints.getResult()).size());
	}
	
	@Test
	public void testfindFreeFieldListPage() throws Exception {
		String name = "Test";// Test value here
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);
		DalHints hints = new DalHints();
		
		List<String> ret;
		try {
			hints = new DalHints();
			ret = dao.findFreeFieldListPage(name, cityIds, 2, 2, hints);
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		ret = dao.findFreeFieldListPage(name, cityIds, 2, 2, hints.inShard(1));
		assertEquals(1, ret.size());
		
		hints = new DalHints();
		ret = dao.findFreeFieldListPage(name, cityIds, 2, 2, hints.inShard(0));
		assertEquals(1, ret.size());
		
		hints = new DalHints();
		ret = dao.findFreeFieldListPage(name, cityIds, 2, 2, hints.inAllShards());
		assertEquals(2, ret.size());
		
		hints = new DalHints();
		Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    ret = dao.findFreeFieldListPage(name, cityIds, 2, 2, hints.inShards(shards));
	    assertEquals(2, ret.size());
		
		// async
		hints = new DalHints();
		hints = new DalHints();
		ret = dao.findFreeFieldListPage(name, cityIds, 2, 2, hints.asyncExecution());
		assertNull(ret);
		try{
			hints.getResult();
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		ret = dao.findFreeFieldListPage(name, cityIds, 2, 2, hints.inShard(1).asyncExecution());
		assertNull(ret);
		assertEquals(1, ((List)hints.getResult()).size());
		
		hints = new DalHints();
		ret = dao.findFreeFieldListPage(name, cityIds, 2, 2, hints.inShard(0).asyncExecution());
		assertNull(ret);
		assertEquals(1, ((List)hints.getResult()).size());
		
		hints = new DalHints();
		ret = dao.findFreeFieldListPage(name, cityIds, 2, 2, hints.inAllShards().asyncExecution());
		assertNull(ret);
		assertEquals(2, ((List)hints.getResult()).size());
		
		hints = new DalHints();
		shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    ret = dao.findFreeFieldListPage(name, cityIds, 2, 2, hints.inShards(shards).asyncExecution());
		assertNull(ret);
		assertEquals(2, ((List)hints.getResult()).size());
	}
	
	@Test
	public void testfindFreeFieldSingle() throws Exception {
		String name = "Test";// Test value here
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(22);
		cityIds.add(33);
		DalHints hints = new DalHints();
		
		String ret;
		try {
			hints = new DalHints();
			ret = dao.findFreeFieldSingle(name, cityIds, hints);
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		ret = dao.findFreeFieldSingle(name, cityIds, hints.inShard(1));
		assertEquals("Test", ret);
		
		hints = new DalHints();
		ret = dao.findFreeFieldSingle(name, cityIds, hints.inShard(0));
		assertEquals("Test", ret);
		
		hints = new DalHints();
		try {
			ret = dao.findFreeFieldSingle(name, cityIds, hints.inAllShards());
			fail();
		} catch (Exception e1) {
		}
		
		hints = new DalHints();
		Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
		try {
		    ret = dao.findFreeFieldSingle(name, cityIds, hints.inShards(shards));
			fail();
		} catch (Exception e1) {
		}
		
		// async
		hints = new DalHints();
		hints = new DalHints();
		ret = dao.findFreeFieldSingle(name, cityIds, hints.asyncExecution());
		assertNull(ret);
		try{
			hints.getResult();
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		ret = dao.findFreeFieldSingle(name, cityIds, hints.inShard(1).asyncExecution());
		assertNull(ret);
		assertEquals("Test", hints.getResult());
		
		hints = new DalHints();
		ret = dao.findFreeFieldSingle(name, cityIds, hints.inShard(0).asyncExecution());
		assertNull(ret);
		assertEquals("Test", hints.getResult());
		
		hints = new DalHints();
		ret = dao.findFreeFieldSingle(name, cityIds, hints.inAllShards().asyncExecution());
		assertNull(ret);
		try{
			hints.getResult();
			fail();
		} catch (Exception e) {
		}
		
		hints = new DalHints();
		shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    ret = dao.findFreeFieldSingle(name, cityIds, hints.inShards(shards).asyncExecution());
		assertNull(ret);
		try{
			hints.getResult();
			fail();
		} catch (Exception e) {
		}
	}

}
