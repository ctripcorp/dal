package SimpleShardByDBOnSqlserver;

import static org.junit.Assert.assertEquals;

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
import com.ctrip.platform.dal.dao.KeyHolder;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

/**
 * JUnit test of PeoplePeopleGenSimpleShardBySqlServerLljDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class PeoplePeopleGenSimpleShardBySqlServerLljDaoUnitTest {

	private static final String DATA_BASE = "SimpleShardByDBOnSqlserver";

	private static DalClient client = null;
	private static PeoplePeopleGenSimpleShardBySqlServerLljDao dao = null;
	private static Transaction t;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		t = Cat.newTransaction("Dal Java Client Test", "PeoplePeopleGenSimpleShardBySqlServerLljDaoUnitTest");
		
		/**
		* Initialize DalClientFactory.
		* The Dal.config can be specified from class-path or local file path.
		* One of follow three need to be enabled.
		**/
		//DalClientFactory.initPrivateFactory(); //Load from class-path connections.properties
		DalClientFactory.initClientFactory(); // load from class-path Dal.config
		//DalClientFactory.initClientFactory("E:/DalMult.config"); // load from the specified Dal.config file path
		client = DalClientFactory.getClient(DATA_BASE);
		dao = new PeoplePeopleGenSimpleShardBySqlServerLljDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		t.complete();
	}
	
	@Before
	public void setUp() throws Exception {
		dao.test_def_update(new DalHints().inShard(0));
		dao.test_def_update(new DalHints().inShard(1));

		
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos1 = new ArrayList<PeoplePeopleGenSimpleShardBySqlServerLlj>(3);
		for(int i=0;i<3;i++)
		{
			PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo = new PeoplePeopleGenSimpleShardBySqlServerLlj();
			daoPojo.setPeopleID(Long.valueOf(i)+1);  
			daoPojo.setName("Initial_Shard_0"+i);
			daoPojo.setCityID(i+20);
			daoPojo.setProvinceID(i+30);
			daoPojo.setCountryID(i+40);
			daoPojos1.add(daoPojo);
		}
		dao.insert(new DalHints().inShard(0), daoPojos1);
		
		
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos2 = new ArrayList<PeoplePeopleGenSimpleShardBySqlServerLlj>(3);
		for(int i=0;i<3;i++)
		{
			PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo = new PeoplePeopleGenSimpleShardBySqlServerLlj();
			daoPojo.setPeopleID(Long.valueOf(i)+1);  
			daoPojo.setName("Initial_Shard_1"+i);
			daoPojo.setCityID(i+20);
			daoPojo.setProvinceID(i+30);
			daoPojo.setCountryID(i+40);
			daoPojos2.add(daoPojo);
		}
		dao.insert(new DalHints().inShard(1), daoPojos2);
		
	}

	@After
	public void tearDown() throws Exception {
		dao.test_def_update(new DalHints().inShard(0));
		dao.test_def_update(new DalHints().inShard(1));
	} 
	
	
	@Test
	public void testCount() throws Exception {
		int ret1 = dao.count(new DalHints().inShard(0));
		assertEquals(3,ret1);
		int ret2 = dao.count(new DalHints().inShard(1));
		assertEquals(3,ret2);
	}
	
	@Test
	public void testDelete1() throws Exception {
	    DalHints hints = new DalHints();
		PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo = null;
		int ret = dao.delete(hints, daoPojo); 
	}
	
	@Test
	public void testDelete2() throws Exception {
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos1 = new ArrayList<PeoplePeopleGenSimpleShardBySqlServerLlj>(3);
		for(int i=0;i<3;i++)
		{
			PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo = new PeoplePeopleGenSimpleShardBySqlServerLlj();
			daoPojo.setPeopleID(Long.valueOf(i)+1);  
			daoPojo.setName("Initial_Shard_0"+i);
			daoPojo.setCityID(i+20);
			daoPojo.setProvinceID(i+30);
			daoPojo.setCountryID(i+40);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.delete(new DalHints().inShard(0), daoPojos1);
		assertEquals(3, affected.length);
		
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos2 = new ArrayList<PeoplePeopleGenSimpleShardBySqlServerLlj>(3);
		for(int i=0;i<3;i++)
		{
			PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo = new PeoplePeopleGenSimpleShardBySqlServerLlj();
			daoPojo.setPeopleID(Long.valueOf(i)+1);  
			daoPojo.setName("Initial_Shard_1"+i);
			daoPojo.setCityID(i+20);
			daoPojo.setProvinceID(i+30);
			daoPojo.setCountryID(i+40);
			daoPojos2.add(daoPojo);
		}
		affected = dao.delete(new DalHints().inShard(1), daoPojos2);
		assertEquals(3, affected.length);
	}
	
	@Test
	public void testBatchDelete() throws Exception {
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos1 = new ArrayList<PeoplePeopleGenSimpleShardBySqlServerLlj>(3);
		for(int i=0;i<3;i++)
		{
			PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo = new PeoplePeopleGenSimpleShardBySqlServerLlj();
			daoPojo.setPeopleID(Long.valueOf(i)+1);  
			daoPojo.setName("Initial_Shard_0"+i);
			daoPojo.setCityID(i+20);
			daoPojo.setProvinceID(i+30);
			daoPojo.setCountryID(i+40);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.batchDelete(new DalHints().inShard(0), daoPojos1);
		assertEquals(3, affected.length);
		
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos2 = new ArrayList<PeoplePeopleGenSimpleShardBySqlServerLlj>(3);
		for(int i=0;i<3;i++)
		{
			PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo = new PeoplePeopleGenSimpleShardBySqlServerLlj();
			daoPojo.setPeopleID(Long.valueOf(i)+1);  
			daoPojo.setName("Initial_Shard_1"+i);
			daoPojo.setCityID(i+20);
			daoPojo.setProvinceID(i+30);
			daoPojo.setCountryID(i+40);
			daoPojos2.add(daoPojo);
		}
		affected = dao.batchDelete(new DalHints().inShard(1), daoPojos2);
		assertEquals(3, affected.length);
	}
	
	@Test
	public void testGetAll() throws Exception {
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> list = dao.getAll(new DalHints().inShard(0));
		assertEquals(3,list.size());
		
		list = dao.getAll(new DalHints().inShard(1));
		assertEquals(3,list.size());
	}
	
	@Test
	public void testInsert1() throws Exception {
		DalHints hints = new DalHints();
		PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo = null;
		int affected = dao.insert(hints, daoPojo);
	}
	
	@Test
	public void testInsert2() throws Exception {
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos1 = new ArrayList<PeoplePeopleGenSimpleShardBySqlServerLlj>(3);
		for(int i=0;i<3;i++)
		{
			PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo = new PeoplePeopleGenSimpleShardBySqlServerLlj();
			daoPojo.setPeopleID(Long.valueOf(i)+1);  
			daoPojo.setName("Initial_Shard_0"+i);
			daoPojo.setCityID(i+20);
			daoPojo.setProvinceID(i+30);
			daoPojo.setCountryID(i+40);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.insert(new DalHints().inShard(0), daoPojos1);
		assertEquals(3, affected.length);
		
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos2 = new ArrayList<PeoplePeopleGenSimpleShardBySqlServerLlj>(3);
		for(int i=0;i<3;i++)
		{
			PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo = new PeoplePeopleGenSimpleShardBySqlServerLlj();
			daoPojo.setPeopleID(Long.valueOf(i)+1);  
			daoPojo.setName("Initial_Shard_1"+i);
			daoPojo.setCityID(i+20);
			daoPojo.setProvinceID(i+30);
			daoPojo.setCountryID(i+40);
			daoPojos2.add(daoPojo);
		}
		affected = dao.insert(new DalHints().inShard(1), daoPojos2);
		assertEquals(3, affected.length);
	}
	
	@Test
	public void testInsert3() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo = null;
		int affected = dao.insert(hints, keyHolder, daoPojo);
	}
	
	@Test
	public void testInsert4() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos = null;
		int[] affected = dao.insert(hints, keyHolder, daoPojos);
	}
	
	@Test
	public void testInsert5() throws Exception {
		DalHints hints = new DalHints();
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos = null;
		int[] affected = dao.insert(hints, daoPojos);
	}
	
	
	
	@Test
	public void testCombinedInsert2() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos = null;
		int affected = dao.combinedInsert(hints, keyHolder, daoPojos);
	}
	
	@Test
	public void testQueryByPage() throws Exception {
		DalHints hints = new DalHints();
		int pageSize = 100;
		int pageNo = 1;
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> list = dao.queryByPage(pageSize, pageNo, hints);
	}
	
	@Test
	public void testQueryByPk1() throws Exception {
		Long peopleID=1l;
		DalHints hints1 = new DalHints();
		PeoplePeopleGenSimpleShardBySqlServerLlj ret1 = dao.queryByPk(peopleID, hints1.inShard(0));
		assertEquals("Initial_Shard_00",ret1.getName());
		
		
		DalHints hints2 = new DalHints();
		PeoplePeopleGenSimpleShardBySqlServerLlj ret2 = dao.queryByPk(peopleID, hints2.inShard(1));
		assertEquals("Initial_Shard_10",ret2.getName());
	}
	
	@Test
	public void testQueryByPk2() throws Exception {
		PeoplePeopleGenSimpleShardBySqlServerLlj pk = new PeoplePeopleGenSimpleShardBySqlServerLlj();
		pk.setPeopleID(2l);
		DalHints hints1 = new DalHints();
		PeoplePeopleGenSimpleShardBySqlServerLlj ret1 = dao.queryByPk(pk, hints1.inShard(0));
		assertEquals("Initial_Shard_01",ret1.getName());
		
		
	
		DalHints hints2 = new DalHints();
		PeoplePeopleGenSimpleShardBySqlServerLlj ret2 = dao.queryByPk(pk, hints2.inShard(1));
		assertEquals("Initial_Shard_11",ret2.getName());
	}
	
	@Test
	public void testUpdate1() throws Exception {
		DalHints hints = new DalHints();
		PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo = null;
		int ret = dao.update(hints, daoPojo);
	}
	
	@Test
	public void testUpdate2() throws Exception {
		DalHints hints1 = new DalHints();
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos1 = new ArrayList<PeoplePeopleGenSimpleShardBySqlServerLlj>(2);
		for(int i=0;i<2;i++)
		{
			PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo = new PeoplePeopleGenSimpleShardBySqlServerLlj();
			daoPojo.setPeopleID(Long.valueOf(i)+1);  
			daoPojo.setName("Update_Shard_0"+i);
			daoPojo.setCityID(i+20);
			daoPojo.setProvinceID(i+30);
			daoPojo.setCountryID(i+40);
			daoPojos1.add(daoPojo);
		}
		hints1.inShard(0);
		int[] ret0 = dao.update(hints1, daoPojos1);
		assertEquals(2, ret0.length);
		
		Long peopleID=1l;
		PeoplePeopleGenSimpleShardBySqlServerLlj ret1 = dao.queryByPk(peopleID, new DalHints().inShard(0));
		assertEquals("Update_Shard_00",ret1.getName());
		
	    peopleID=2l;
	    PeoplePeopleGenSimpleShardBySqlServerLlj ret2 = dao.queryByPk(peopleID, new DalHints().inShard(0));
		assertEquals("Update_Shard_01",ret2.getName());
		
		DalHints hints2 = new DalHints();
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos2 = new ArrayList<PeoplePeopleGenSimpleShardBySqlServerLlj>(2);
		for(int i=0;i<2;i++)
		{
			PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo = new PeoplePeopleGenSimpleShardBySqlServerLlj();
			daoPojo.setPeopleID(Long.valueOf(i)+1);  
			daoPojo.setName("Update_Shard_1"+i);
			daoPojo.setCityID(i+20);
			daoPojo.setProvinceID(i+30);
			daoPojo.setCountryID(i+40);
			daoPojos2.add(daoPojo);
		}
		hints2.inShard(1);
		int[] ret3 = dao.update(hints2, daoPojos2);
		assertEquals(2, ret3.length);
		
		peopleID=1l;
		PeoplePeopleGenSimpleShardBySqlServerLlj ret4 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertEquals("Update_Shard_10",ret4.getName());
		
	    peopleID=2l;
	    PeoplePeopleGenSimpleShardBySqlServerLlj ret5 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertEquals("Update_Shard_11",ret5.getName());
	}
	
	@Test
	public void testBatchUpdate() throws Exception {
		DalHints hints1 = new DalHints();
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos1 = new ArrayList<PeoplePeopleGenSimpleShardBySqlServerLlj>(2);
		for(int i=0;i<2;i++)
		{
			PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo = new PeoplePeopleGenSimpleShardBySqlServerLlj();
			daoPojo.setPeopleID(Long.valueOf(i)+1);  
			daoPojo.setName("Update_Shard_0"+i);
			daoPojo.setCityID(i+20);
			daoPojo.setProvinceID(i+30);
			daoPojo.setCountryID(i+40);
			daoPojos1.add(daoPojo);
		}
		hints1.inShard(0);
		int[] ret0 = dao.batchUpdate(hints1, daoPojos1);
		assertEquals(2, ret0.length);
		
		Long peopleID=1l;
		PeoplePeopleGenSimpleShardBySqlServerLlj ret1 = dao.queryByPk(peopleID, new DalHints().inShard(0));
		assertEquals("Update_Shard_00",ret1.getName());
		
	    peopleID=2l;
	    PeoplePeopleGenSimpleShardBySqlServerLlj ret2 = dao.queryByPk(peopleID, new DalHints().inShard(0));
		assertEquals("Update_Shard_01",ret2.getName());
		
		DalHints hints2 = new DalHints();
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos2 = new ArrayList<PeoplePeopleGenSimpleShardBySqlServerLlj>(2);
		for(int i=0;i<2;i++)
		{
			PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo = new PeoplePeopleGenSimpleShardBySqlServerLlj();
			daoPojo.setPeopleID(Long.valueOf(i)+1);  
			daoPojo.setName("Update_Shard_1"+i);
			daoPojo.setCityID(i+20);
			daoPojo.setProvinceID(i+30);
			daoPojo.setCountryID(i+40);
			daoPojos2.add(daoPojo);
		}
		hints2.inShard(1);
		int[] ret3 = dao.batchUpdate(hints2, daoPojos2);
		assertEquals(2, ret3.length);
		
		peopleID=1l;
		PeoplePeopleGenSimpleShardBySqlServerLlj ret4 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertEquals("Update_Shard_10",ret4.getName());
		
	    peopleID=2l;
	    PeoplePeopleGenSimpleShardBySqlServerLlj ret5 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertEquals("Update_Shard_11",ret5.getName());
	}
	
	@Test
	public void testtest_build_insert() throws Exception {
		Integer CityID = 101;// Test value here
		String Name = "InsertInAllShards";// Test value here
		Integer ProvinceID = 100;// Test value here
		Integer CountryID = 100;// Test value here
	    int ret = dao.test_build_insert(CityID, Name, ProvinceID, CountryID, new DalHints().inAllShards().sequentialExecute());
	    assertEquals(-2, ret);
	 
	    Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    
	    CityID = 200;
	    Name = "InsertBYShards";
	    ret = dao.test_build_insert(CityID, Name, ProvinceID, CountryID, new DalHints().inShards(shards).sequentialExecute());
	    assertEquals(-2, ret);
	}
	
	@Test
	public void testtest_build_query() throws Exception {
		Integer CityID =20;// Test value here
	    List<PeoplePeopleGenSimpleShardBySqlServerLlj> ret = dao.test_build_query(CityID, new DalHints().inAllShards().sequentialExecute());
	    assertEquals(2, ret.size());
	    
	    Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    
	    CityID =21;// Test value here
	    ret = dao.test_build_query(CityID, new DalHints().inShards(shards).sequentialExecute());
	    assertEquals(2, ret.size());
	    
	}
	
	@Test
	public void testtest_build_update() throws Exception {
		String Name = "UpdateInAllShards";// Test value here
		Integer CityID = 20;// Test value here
	    int ret = dao.test_build_update(Name, CityID, new DalHints().inAllShards().sequentialExecute());
	    assertEquals(-2, ret);
	    
	    Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    
	    Name = "UpdateByShards";// Test value here
		CityID = 22;// Test value here
	    ret = dao.test_build_update(Name, CityID, new DalHints().inShards(shards).sequentialExecute());
	    assertEquals(-2, ret);
	}

	@Test
	public void testtest_build_delete() throws Exception {
		Integer CityID = 20;// Test value here
	    int ret = dao.test_build_delete(CityID, new DalHints().inAllShards().sequentialExecute());
	    assertEquals(-2, ret);
	    
	    Set<String> shards = new HashSet<>();
	    shards.add("0");
	    shards.add("1");
	    CityID = 21;
	    ret = dao.test_build_delete(CityID, new DalHints().inShards(shards).sequentialExecute());
	    assertEquals(-2, ret);
	}
	
	//自定义，查询
	@Test
	public void testtest_def_query() throws Exception {
		DalHints hints1 = new DalHints();
		//hints1.inShard(0);
		Integer CityID = 20;
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> ret1 = dao.test_def_query(CityID, hints1.inShard(0));
		assertEquals(1,ret1.size());
		
		DalHints hints2 = new DalHints();
		//hints2.inShard(1);
		CityID = 22;
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> ret2 = dao.test_def_query(CityID, hints2.inShard(1));
		assertEquals(1,ret2.size());
	}
	
	//自定义，增删改
	@Test
	public void testtest_def_update() throws Exception {
		int ret1 = dao.test_def_update(new DalHints().inShard(0));
		assertEquals(-1,ret1);
		int ret2 = dao.test_def_update(new DalHints().inShard(1));
		assertEquals(-1,ret2);
	}

}
