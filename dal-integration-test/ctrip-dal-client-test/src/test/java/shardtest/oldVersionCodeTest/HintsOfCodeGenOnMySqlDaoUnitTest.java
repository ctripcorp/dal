package shardtest.oldVersionCodeTest;

import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;
import dao.shard.oldVersionCode.HintsOfCodeGenOnMySqlDao;
import entity.MysqlPersonTable;
import org.junit.*;
import testUtil.DalHintsChecker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * JUnit test of PersonGenDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class HintsOfCodeGenOnMySqlDaoUnitTest {

	private static final String DATA_BASE = "ShardColModShardByDBOnMysql";

	private static DalClient client = null;
	private static HintsOfCodeGenOnMySqlDao dao = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = DalClientFactory.getClient(DATA_BASE);
		dao = new HintsOfCodeGenOnMySqlDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
		dao.test_def_update(new DalHints().inShard(0));
		dao.test_def_update(new DalHints().inShard(1));

		KeyHolder keyHolder = new KeyHolder();
		List<MysqlPersonTable> daoPojos1 = new ArrayList<MysqlPersonTable>(
				3);

		for (int i = 0; i < 3; i++) {
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setAge(i + 20);
			daoPojo.setName("Initial_Shard_0" + i);
			daoPojos1.add(daoPojo);
		}
		dao.insert(new DalHints().inShard(0), keyHolder, daoPojos1);

		List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(
				3);
		for (int i = 0; i < 3; i++) {
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setAge(i + 20);
			daoPojo.setName("Initial_Shard_1" + i);
			daoPojos2.add(daoPojo);
		}
		dao.insert(new DalHints().inShard(1), keyHolder, daoPojos2);
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
//		PersonGenSimpleShardByDbOnMySqlLlj daoPojo = null;
//		int ret = dao.delete(hints, daoPojo); 
//	}
//	
//	@Test
//	public void testDelete2() throws Exception {
//		DalHints hints = new DalHints();
//		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos = null;
//		int[] affected = dao.delete(hints, daoPojos);
//	}
//	
//	@Test
//	public void testBatchDelete() throws Exception {
//		DalHints hints = new DalHints();
//		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos = null;
//		int[] affected = dao.batchDelete(hints, daoPojos);
//	}
//	
//	@Test
//	public void testGetAll() throws Exception {
//		List<PersonGenSimpleShardByDbOnMySqlLlj> list = dao.getAll(new DalHints());
//	}
//	
//	@Test
//	public void testInsert1() throws Exception {
//		DalHints hints = new DalHints();
//		PersonGenSimpleShardByDbOnMySqlLlj daoPojo = null;
//		int affected = dao.insert(hints, daoPojo);
//	}
//	
//	@Test
//	public void testInsert2() throws Exception {
//		DalHints hints = new DalHints();
//		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos = null;
//		int[] affected = dao.insert(hints, daoPojos);
//	}
//	
//	@Test
//	public void testInsert3() throws Exception {
//		DalHints hints = new DalHints();
//		KeyHolder keyHolder = new KeyHolder();
//		PersonGenSimpleShardByDbOnMySqlLlj daoPojo = null;
//		int affected = dao.insert(hints, keyHolder, daoPojo);
//	}
//	
//	@Test
//	public void testInsert4() throws Exception {
//		DalHints hints = new DalHints();
//		KeyHolder keyHolder = new KeyHolder();
//		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos = null;
//		int[] affected = dao.insert(hints, keyHolder, daoPojos);
//	}
//	
//	@Test
//	public void testInsert5() throws Exception {
//		DalHints hints = new DalHints();
//		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos = null;
//		int[] affected = dao.insert(hints, daoPojos);
//	}
//	
//	@Test
//	public void testCombinedInsert1() throws Exception {
//		DalHints hints = new DalHints();
//		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos = null;
//		int affected = dao.combinedInsert(hints, daoPojos);
//	}
//	
//	@Test
//	public void testCombinedInsert2() throws Exception {
//		DalHints hints = new DalHints();
//		KeyHolder keyHolder = new KeyHolder();
//		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos = null;
//		int affected = dao.combinedInsert(hints, keyHolder, daoPojos);
//	}
//	
//	@Test
//	public void testQueryByPage() throws Exception {
//		DalHints hints = new DalHints();
//		int pageSize = 100;
//		int pageNo = 1;
//		List<PersonGenSimpleShardByDbOnMySqlLlj> list = dao.queryByPage(pageSize, pageNo, hints);
//	}
//	
//	@Test
//	public void testQueryByPk1() throws Exception {
//		Number id = null;
//		DalHints hints = new DalHints();
//		PersonGenSimpleShardByDbOnMySqlLlj ret = dao.queryByPk(id, hints);
//	}
//	
//	@Test
//	public void testQueryByPk2() throws Exception {
//		PersonGenSimpleShardByDbOnMySqlLlj pk = null;
//		DalHints hints = new DalHints();
//		PersonGenSimpleShardByDbOnMySqlLlj ret = dao.queryByPk(pk, hints);
//	}
//	
//	@Test
//	public void testUpdate1() throws Exception {
//		DalHints hints = new DalHints();
//		PersonGenSimpleShardByDbOnMySqlLlj daoPojo = null;
//		int ret = dao.update(hints, daoPojo);
//	}
//	
//	@Test
//	public void testUpdate2() throws Exception {
//		DalHints hints = new DalHints();
//		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos = null;
//		int[] ret = dao.update(hints, daoPojos);
//	}
//	
//	@Test
//	public void testBatchUpdate() throws Exception {
//		DalHints hints = new DalHints();
//		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos = null;
//		int[] ret = dao.batchUpdate(hints, daoPojos);
//	}
	
	@Test
	public void testtest_build_delete_allShard_async() throws Exception {
		Integer Age = 21;// Test value here
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		int ret = dao.test_build_delete_allShard_async(Age, hints);
		assertEquals(0, ret);
		Future<Integer> fr = (Future<Integer>) hints.getAsyncResult();
		ret = fr.get(); // 异步返回结果
		assertEquals(2, ret);

		assertEquals(2, dao.count(new DalHints().inShard(0)));
		assertEquals(2, dao.count(new DalHints().inShard(1)));

		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.asyncExecution);
		exclude.add(DalHintEnum.futureResult);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}

	@Test
	public void testtest_build_delete_shards_callback() throws Exception {
		Integer Age = 22;// Test value here
		DalHints hints = new DalHints();
		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		DefaultResultCallback callback = new DefaultResultCallback();
		int ret = dao.test_build_delete_shards_callback(Age, hints, shards,
				callback);
		assertEquals(0, ret);
		callback.waitForDone();
		ret = (Integer) callback.getResult(); // 异步返回结果
		assertEquals(2, ret);
		assertEquals(2, dao.count(new DalHints().inShard(0)));
		assertEquals(2, dao.count(new DalHints().inShard(1)));
	}

	@Test
	public void testtest_build_insert_allShard_async() throws Exception {
		String Name = "insert_allShard_async";// Test value here
		Integer Age = 50;// Test value here
		DalHints hints = new DalHints();
		int ret = dao.test_build_insert_allShard_async(Name, Age, hints);
		assertEquals(0, ret);
		Future<Integer> fr = (Future<Integer>) hints.getAsyncResult();
		ret = fr.get(); // 异步返回结果
		assertEquals(2, ret);

		assertEquals(4, dao.count(new DalHints().inShard(0)));
		assertEquals(4, dao.count(new DalHints().inShard(1)));

	}

	@Test
	public void testtest_build_insert_shards_callback() throws Exception {
		String Name = "insert_shards_callback";// Test value here
		Integer Age = 50;// Test value here
		DalHints hints = new DalHints();
		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		DefaultResultCallback callback = new DefaultResultCallback();
		int ret = dao.test_build_insert_shards_callback(Name, Age, hints,
				shards, callback);
		assertEquals(0, ret);
		callback.waitForDone();
		ret = (Integer) callback.getResult(); // 异步返回结果
		assertEquals(2, ret);
		assertEquals(4, dao.count(new DalHints().inShard(0)));
		assertEquals(4, dao.count(new DalHints().inShard(1)));

	}

	@Test
	public void testtest_build_update_allShard_async() throws Exception {
		Integer Age = 20;// Test value here
		String Name = "update_allShard_async";
		DalHints hints = new DalHints();
		int ret = dao.test_build_update_allShard_async(Name, Age, hints);
		assertEquals(0, ret);
		Future<Integer> fr = (Future<Integer>) hints.getAsyncResult();
		ret = fr.get(); // 异步返回结果
		assertEquals(2, ret);
		assertEquals("update_allShard_async",
				dao.queryByPk(1l, new DalHints().inShard(0)).getName());
		assertEquals("update_allShard_async",
				dao.queryByPk(1l, new DalHints().inShard(1)).getName());
	}

	@Test
	public void testtest_build_update_shards_callback() throws Exception {
		Integer Age = 20;// Test value here
		String Name = "update_shards_callback";
		DalHints hints = new DalHints();
		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		DefaultResultCallback callback = new DefaultResultCallback();
		int ret = dao.test_build_update_shards_callback(Name, Age, hints,
				shards, callback);
		assertEquals(0, ret);
		callback.waitForDone();
		ret = (Integer) callback.getResult(); // 异步返回结果
		assertEquals(2, ret);
		assertEquals("update_shards_callback",
				dao.queryByPk(1l, new DalHints().inShard(0)).getName());
		assertEquals("update_shards_callback",
				dao.queryByPk(1l, new DalHints().inShard(1)).getName());
	}

	@Test
	public void testtest_build_query_allShard_async() throws Exception {
		Integer Age = 20;// Test value here
		DalHints hints = new DalHints();
		List<MysqlPersonTable> ret = dao
				.test_build_query_allShard_async(Age, hints);
		assertNull(ret);
		Future<List<MysqlPersonTable>> fr = (Future<List<MysqlPersonTable>>) hints
				.getAsyncResult();
		ret = fr.get(); // 异步返回结果
		assertEquals(2, ret.size());
		// System.out.println(ret.get(0).getName());
		// System.out.println(ret.get(1).getName());
		assertEquals("Initial_Shard_00", ret.get(0).getName());
		assertEquals("Initial_Shard_10", ret.get(1).getName());
	}

	@Test
	public void testtest_build_query_shards_callback() throws Exception {
		Integer Age = 20;// Test value here

		DalHints hints = new DalHints();
        DalHints original=hints.clone();
		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		DefaultResultCallback callback = new DefaultResultCallback();

		List<MysqlPersonTable> ret = dao
				.test_build_query_shards_callback(Age, hints, shards, callback);

		assertNull(ret);

		callback.waitForDone();
		ret = (List<MysqlPersonTable>) callback.getResult(); // 异步返回结果

		assertEquals(2, ret.size());
		assertEquals("Initial_Shard_10", ret.get(1).getName());
		assertEquals("Initial_Shard_00", ret.get(0).getName());

		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shards);
		exclude.add(DalHintEnum.resultCallback);
		exclude.add(DalHintEnum.futureResult);
		exclude.add(DalHintEnum.resultSorter);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}

	@Test
	public void testtest_def_query_allShard_async() throws Exception {
		String Name = "Initial%";// Test value here
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
		List<MysqlPersonTable> ret = dao
				.test_def_query_allShard_async(Name, hints);
		assertNull(ret);
		Future<List<MysqlPersonTable>> fr = (Future<List<MysqlPersonTable>>) hints
				.getAsyncResult();
		ret = fr.get(); // 异步返回结果
		assertEquals(6, ret.size());
		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.allShards);
		exclude.add(DalHintEnum.asyncExecution);
		exclude.add(DalHintEnum.futureResult);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}

	@Test
	public void testtest_def_query_shards_callback() throws Exception {
		String Name = "Initial%";// Test value here

		DalHints hints = new DalHints();
        DalHints original=hints.clone();
		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		DefaultResultCallback callback = new DefaultResultCallback();

		List<MysqlPersonTable> ret = dao
				.test_def_query_shards_callback(Name, hints, shards, callback);

		assertNull(ret);

		callback.waitForDone();
		ret = (List<MysqlPersonTable>) callback.getResult(); // 异步返回结果

		assertEquals(6, ret.size());

		List<DalHintEnum> exclude=new ArrayList<>();
		exclude.add(DalHintEnum.shards);
		exclude.add(DalHintEnum.resultCallback);
		exclude.add(DalHintEnum.futureResult);
		DalHintsChecker.checkEquals(original,hints,exclude);
	}

}
