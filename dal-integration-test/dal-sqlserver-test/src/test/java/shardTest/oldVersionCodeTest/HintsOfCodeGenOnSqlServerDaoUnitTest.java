package shardTest.oldVersionCodeTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.sql.SQLException;

import org.junit.*;

import static org.junit.Assert.*;

import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;

/**
 * JUnit test of PeopleGenDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class HintsOfCodeGenOnSqlServerDaoUnitTest {

	private static final String DATA_BASE = "SimpleShardByDBOnSqlServer";

	private static DalClient client = null;
	private static HintsOfCodeGenOnSqlServerDao dao = null;
	
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
		dao = new HintsOfCodeGenOnSqlServerDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
		dao.test_def_update(new DalHints().inShard(0));
		dao.test_def_update(new DalHints().inShard(1));

		List<HintsOfCodeGenOnSqlServer> daoPojos1 = new ArrayList<HintsOfCodeGenOnSqlServer>(
				3);
		for (int i = 0; i < 3; i++) {
			HintsOfCodeGenOnSqlServer daoPojo = new HintsOfCodeGenOnSqlServer();
			//daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Initial_Shard_0" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos1.add(daoPojo);
		}
		dao.insert(new DalHints().inShard(0), daoPojos1);

		List<HintsOfCodeGenOnSqlServer> daoPojos2 = new ArrayList<HintsOfCodeGenOnSqlServer>(
				3);
		for (int i = 0; i < 3; i++) {
			HintsOfCodeGenOnSqlServer daoPojo = new HintsOfCodeGenOnSqlServer();
			//daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Initial_Shard_1" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos2.add(daoPojo);
		}
		dao.insert(new DalHints().inShard(1), daoPojos2);
	}

	@After
	public void tearDown() throws Exception {
		dao.test_def_update(new DalHints().inShard(0));
		dao.test_def_update(new DalHints().inShard(1));

	} 
	
	
//	@Test
//	public void testCount() throws Exception {
//		int ret = dao.count(new DalHints());
//	}
//	
//	@Test
//	public void testDelete1() throws Exception {
//	    DalHints hints = new DalHints();
//		PeopleGenSimpleShardBySqlServerLlj daoPojo = null;
//		int ret = dao.delete(hints, daoPojo); 
//	}
//	
//	@Test
//	public void testDelete2() throws Exception {
//		DalHints hints = new DalHints();
//		List<PeopleGenSimpleShardBySqlServerLlj> daoPojos = null;
//		int[] affected = dao.delete(hints, daoPojos);
//	}
//	
//	@Test
//	public void testBatchDelete() throws Exception {
//		DalHints hints = new DalHints();
//		List<PeopleGenSimpleShardBySqlServerLlj> daoPojos = null;
//		int[] affected = dao.batchDelete(hints, daoPojos);
//	}
//	
//	@Test
//	public void testGetAll() throws Exception {
//		List<PeopleGenSimpleShardBySqlServerLlj> list = dao.getAll(new DalHints());
//	}
//	
//	@Test
//	public void testInsert1() throws Exception {
//		DalHints hints = new DalHints();
//		PeopleGenSimpleShardBySqlServerLlj daoPojo = null;
//		int affected = dao.insert(hints, daoPojo);
//	}
//	
//	@Test
//	public void testInsert2() throws Exception {
//		DalHints hints = new DalHints();
//		List<PeopleGenSimpleShardBySqlServerLlj> daoPojos = null;
//		int[] affected = dao.insert(hints, daoPojos);
//	}
//	
//	@Test
//	public void testInsert3() throws Exception {
//		DalHints hints = new DalHints();
//		KeyHolder keyHolder = new KeyHolder();
//		PeopleGenSimpleShardBySqlServerLlj daoPojo = null;
//		int affected = dao.insert(hints, keyHolder, daoPojo);
//	}
//	
//	@Test
//	public void testInsert4() throws Exception {
//		DalHints hints = new DalHints();
//		KeyHolder keyHolder = new KeyHolder();
//		List<PeopleGenSimpleShardBySqlServerLlj> daoPojos = null;
//		int[] affected = dao.insert(hints, keyHolder, daoPojos);
//	}
//	
//	@Test
//	public void testInsert5() throws Exception {
//		DalHints hints = new DalHints();
//		List<PeopleGenSimpleShardBySqlServerLlj> daoPojos = null;
//		int[] affected = dao.insert(hints, daoPojos);
//	}
//	
//	@Test
//	public void testQueryByPage() throws Exception {
//		DalHints hints = new DalHints();
//		int pageSize = 100;
//		int pageNo = 1;
//		List<PeopleGenSimpleShardBySqlServerLlj> list = dao.queryByPage(pageSize, pageNo, hints);
//	}
//	
//	@Test
//	public void testQueryByPk1() throws Exception {
//		Number id = null;
//		DalHints hints = new DalHints();
//		PeopleGenSimpleShardBySqlServerLlj ret = dao.queryByPk(id, hints);
//	}
//	
//	@Test
//	public void testQueryByPk2() throws Exception {
//		PeopleGenSimpleShardBySqlServerLlj pk = null;
//		DalHints hints = new DalHints();
//		PeopleGenSimpleShardBySqlServerLlj ret = dao.queryByPk(pk, hints);
//	}
//	
//	@Test
//	public void testUpdate1() throws Exception {
//		DalHints hints = new DalHints();
//		PeopleGenSimpleShardBySqlServerLlj daoPojo = null;
//		int ret = dao.update(hints, daoPojo);
//	}
//	
//	@Test
//	public void testUpdate2() throws Exception {
//		DalHints hints = new DalHints();
//		List<PeopleGenSimpleShardBySqlServerLlj> daoPojos = null;
//		int[] ret = dao.update(hints, daoPojos);
//	}
//	
//	@Test
//	public void testBatchUpdate() throws Exception {
//		DalHints hints = new DalHints();
//		List<PeopleGenSimpleShardBySqlServerLlj> daoPojos = null;
//		int[] ret = dao.batchUpdate(hints, daoPojos);
//	}
	
	@Test
	public void testtest_build_delete_allShard_callback() throws Exception {
		 Integer CityID = 20;// Test value here
		 DalHints hints = new DalHints();
		 DefaultResultCallback callback = new DefaultResultCallback();
		 int ret = dao.test_build_delete_allShard_callback(CityID, hints,callback);
		 assertEquals(0, ret);
			
			callback.waitForDone();
			ret = (Integer) callback.getResult(); // 异步返回结果

			assertEquals(-2, ret);
			
			assertEquals(2, dao.count(new DalHints().inShard(0)));
			assertEquals(2, dao.count(new DalHints().inShard(1)));
	}
	
	@Test
	public void testtest_build_delete_shards_async() throws Exception {
		 Integer CityID = 20;// Test value here
		 DalHints hints = new DalHints();
		 Set<String> shards = new HashSet<>();
			shards.add("0");
			shards.add("1");
		 int ret = dao.test_build_delete_shards_async(CityID, hints,shards);
		 assertEquals(0, ret);
		 
		 Future<Integer> fr = (Future<Integer>) hints
					.getAsyncResult();
			ret = fr.get(); // 异步返回结果
			assertEquals(-2, ret);
			
			assertEquals(2, dao.count(new DalHints().inShard(0)));
			assertEquals(2, dao.count(new DalHints().inShard(1)));
	}

	@Test
	public void testtest_build_insert_allShard_callback() throws Exception {
		 Integer CityID = 50;// Test value here
		 String Name = "insert_allShard_callback";// Test value here
		 Integer ProvinceID = 60;// Test value here
		 Integer CountryID = 70;// Test value here
		 DalHints hints = new DalHints();
		 DefaultResultCallback callback = new DefaultResultCallback();
		 int ret = dao.test_build_insert_allShard_callback(CityID, Name, ProvinceID, CountryID,
		 hints,callback);
		 assertEquals(0, ret);
			
			callback.waitForDone();
			ret = (Integer) callback.getResult(); // 异步返回结果

			assertEquals(-2, ret);
			
			assertEquals("insert_allShard_callback", dao.queryByPk(4l, new DalHints().inShard(0)).getName());
			assertEquals("insert_allShard_callback", dao.queryByPk(4l, new DalHints().inShard(1)).getName());
	}
	
	@Test
	public void testtest_build_insert_shards_async() throws Exception {
		 Integer CityID = 50;// Test value here
		 String Name = "insert_shards_async";// Test value here
		 Integer ProvinceID = 60;// Test value here
		 Integer CountryID = 70;// Test value here
		 DalHints hints = new DalHints();
		 Set<String> shards = new HashSet<>();
			shards.add("0");
			shards.add("1");
		 DefaultResultCallback callback = new DefaultResultCallback();
		 int ret = dao.test_build_insert_shards_async(CityID, Name, ProvinceID, CountryID,
		 hints,shards);
		 assertEquals(0, ret);
		 
		 Future<Integer> fr = (Future<Integer>) hints
					.getAsyncResult();
			ret = fr.get(); // 异步返回结果
			assertEquals(-2, ret);
			
			assertEquals("insert_shards_async", dao.queryByPk(4l, new DalHints().inShard(0)).getName());
			assertEquals("insert_shards_async", dao.queryByPk(4l, new DalHints().inShard(1)).getName());
	}

	@Test
	public void testtest_build_update_allShard_callback() throws Exception {
		 String Name = "update_allShard_callback";// Test value here
		 Integer CityID = 21;// Test value here
		 
		 DalHints hints = new DalHints();

		 DefaultResultCallback callback = new DefaultResultCallback();
		 int ret = dao.test_build_update_allShard_callback(Name, CityID, hints,callback);
		 assertEquals(0, ret);
			
			callback.waitForDone();
			ret = (Integer) callback.getResult(); // 异步返回结果

			assertEquals(-2, ret);
			
			assertEquals("update_allShard_callback", dao.queryByPk(2l, new DalHints().inShard(0)).getName());
			assertEquals("update_allShard_callback", dao.queryByPk(2l, new DalHints().inShard(1)).getName());
	}
	
	@Test
	public void testtest_build_update_shards_async() throws Exception {
		 String Name = "update_shards_async";// Test value here
		 Integer CityID = 21;// Test value here
		 
		 DalHints hints = new DalHints();
		 
		 Set<String> shards = new HashSet<>();
			shards.add("0");
			shards.add("1");
			
			int ret = dao
					.test_build_update_shards_async(Name,CityID, hints, shards);
			
			 assertEquals(0, ret);
			 
			 Future<Integer> fr = (Future<Integer>) hints
						.getAsyncResult();
				ret = fr.get(); // 异步返回结果
				assertEquals(-2, ret);
				
				assertEquals("update_shards_async", dao.queryByPk(2l, new DalHints().inShard(0)).getName());
				assertEquals("update_shards_async", dao.queryByPk(2l, new DalHints().inShard(1)).getName());
			
	}

	@Test
	public void testtest_build_query_shards_async() throws Exception {
		Integer CityID = 20;// Test value here

		DalHints hints = new DalHints();

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		List<HintsOfCodeGenOnSqlServer> ret = dao
				.test_build_query_shards_async(CityID, hints, shards);

		assertNull(ret);
		Future<List<HintsOfCodeGenOnSqlServer>> fr = (Future<List<HintsOfCodeGenOnSqlServer>>) hints
				.getAsyncResult();
		ret = fr.get(); // 异步返回结果
		assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_build_query_allShard_callback() throws Exception {
		Integer CityID = 20;// Test value here

		DalHints hints = new DalHints();

		DefaultResultCallback callback = new DefaultResultCallback();
		

		List<HintsOfCodeGenOnSqlServer> ret = dao
				.test_build_query_allShard_callback(CityID, hints, callback);

		assertNull(ret);
		
		callback.waitForDone();
		ret = (List<HintsOfCodeGenOnSqlServer>) callback.getResult(); // 异步返回结果

		assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_def_query_allShard_callback() throws Exception {
		Integer CityID = 22;// Test value here
		DalHints hints = new DalHints();
		DefaultResultCallback callback = new DefaultResultCallback();
		List<HintsOfCodeGenOnSqlServer> ret = dao.test_def_query_allShard_callback(CityID, hints,callback);
        assertNull(ret);
		
		callback.waitForDone();
		ret = (List<HintsOfCodeGenOnSqlServer>) callback.getResult(); // 异步返回结果

		assertEquals(2, ret.size());
	}

	@Test
	public void testtest_def_query_shards_async() throws Exception {
		Integer CityID = 22;// Test value here
		DalHints hints = new DalHints();
		
		 Set<String> shards = new HashSet<>();
			shards.add("0");
			shards.add("1");
			
		List<HintsOfCodeGenOnSqlServer> ret = dao.test_def_query_shards_async(CityID, hints,shards);
		assertNull(ret);
		Future<List<HintsOfCodeGenOnSqlServer>> fr = (Future<List<HintsOfCodeGenOnSqlServer>>) hints
				.getAsyncResult();
		ret = fr.get(); // 异步返回结果
		assertEquals(2, ret.size());
	}

	@Test
	public void testBaseClient() throws Exception{
		List<HintsOfCodeGenOnSqlServer> ret=dao.testBaseClient();
		assertEquals(1,ret.size());
	}
}
