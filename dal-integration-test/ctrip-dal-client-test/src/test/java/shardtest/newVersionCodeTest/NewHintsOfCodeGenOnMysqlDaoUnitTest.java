package shardtest.newVersionCodeTest;

import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;
import dao.shard.newVersionCode.NewHintsOfCodeGenOnMySqlDao;
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
public class NewHintsOfCodeGenOnMysqlDaoUnitTest {

	private static final String DATA_BASE = "ShardColModShardByDBOnMysql";

	private static DalClient client = null;
	private static NewHintsOfCodeGenOnMySqlDao dao = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		client = DalClientFactory.getClient(DATA_BASE);
		dao = new NewHintsOfCodeGenOnMySqlDao();
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
//		dao.delete(null, dao.getAll(null));
		dao.test_def_update(new DalHints().inShard(0));
		dao.test_def_update(new DalHints().inShard(1));
	} 
	
	
//	@Test
//	public void testCount() throws Exception {
//		int affected = dao.count(new DalHints());
//		assertEquals(10, affected);
//	}
//	
//	@Test
//	public void testDelete1() throws Exception {
//	    DalHints hints = new DalHints();
//		PersonGen daoPojo = createPojo(1);
//		int affected = dao.delete(hints, daoPojo); 
//		assertEquals(1, affected);
//	}
//	
//	@Test
//	public void testDelete2() throws Exception {
//		DalHints hints = new DalHints();
//		List<PersonGen> daoPojos = dao.getAll(null);
//		int[] affected = dao.delete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//	}
//	
//	@Test
//	public void testBatchDelete() throws Exception {
//		DalHints hints = new DalHints();
//		List<PersonGen> daoPojos = dao.getAll(null);
//		int[] affected = dao.batchDelete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//	}
//	
//	@Test
//	public void testGetAll() throws Exception {
//		List<PersonGen> list = dao.getAll(new DalHints());
//		assertEquals(10, list.size());
//	}
//	
//	@Test
//	public void testInsert1() throws Exception {
//		DalHints hints = new DalHints();
//		PersonGen daoPojo = createPojo(1);
//		int affected = dao.insert(hints, daoPojo);
//		assertEquals(1, affected);
//	}
//	
//	@Test
//	public void testInsert2() throws Exception {
//		DalHints hints = new DalHints();
//		List<PersonGen> daoPojos = dao.getAll(new DalHints());
//		int[] affected = dao.insert(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//	}
//	
//	@Test
//	public void testInsert3() throws Exception {
//		DalHints hints = new DalHints();
//		KeyHolder keyHolder = new KeyHolder();
//		PersonGen daoPojo = createPojo(1);
//		int affected = dao.insert(hints, keyHolder, daoPojo);
//		assertEquals(1, affected);
//		assertEquals(1, keyHolder.size());
//	}
//	
//	@Test
//	public void testInsert4() throws Exception {
//		DalHints hints = new DalHints();
//		KeyHolder keyHolder = new KeyHolder();
//		List<PersonGen> daoPojos = dao.getAll(new DalHints());
//		int[] affected = dao.insert(hints, keyHolder, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		assertEquals(10, keyHolder.size());
//	}
//	
//	@Test
//	public void testInsert5() throws Exception {
//		DalHints hints = new DalHints();
//		List<PersonGen> daoPojos = dao.getAll(new DalHints());
//		int[] affected = dao.batchInsert(hints, daoPojos);
//	}
//	
//	@Test
//	public void testCombinedInsert1() throws Exception {
//		DalHints hints = new DalHints();
//		List<PersonGen> daoPojos = dao.getAll(new DalHints());
//		int affected = dao.combinedInsert(hints, daoPojos);
//		assertEquals(10, affected);
//	}
//	
//	@Test
//	public void testCombinedInsert2() throws Exception {
//		DalHints hints = new DalHints();
//		KeyHolder keyHolder = new KeyHolder();
//		List<PersonGen> daoPojos = dao.getAll(new DalHints());
//		int affected = dao.combinedInsert(hints, keyHolder, daoPojos);
//		assertEquals(10, keyHolder.size());
//	}
//	
//	@Test
//	public void testQueryByPage() throws Exception {
//		DalHints hints = new DalHints();
//		int pageSize = 100;
//		int pageNo = 1;
//		List<PersonGen> list = dao.queryByPage(pageSize, pageNo, hints);
//		assertEquals(10, list.size());
//	}
//	
//	@Test
//	public void testQueryByPk1() throws Exception {
//		Number id = 1;
//		DalHints hints = new DalHints();
//		PersonGen affected = dao.queryByPk(id, hints);
//		assertNotNull(affected);
//	}
//	
//	@Test
//	public void testQueryByPk2() throws Exception {
//		PersonGen pk = createPojo(1);
//		DalHints hints = new DalHints();
//		PersonGen affected = dao.queryByPk(pk, hints);
//		assertNotNull(affected);
//	}
//	
//	@Test
//	public void testUpdate1() throws Exception {
//		DalHints hints = new DalHints();
//		PersonGen daoPojo = dao.queryByPk(createPojo(1), hints);
//		changePojo(daoPojo);
//		int affected = dao.update(hints, daoPojo);
//		assertEquals(1, affected);
//		daoPojo = dao.queryByPk(createPojo(1), null);
//		verifyPojo(daoPojo);
//	}
//	
//	@Test
//	public void testUpdate2() throws Exception {
//		DalHints hints = new DalHints();
//		List<PersonGen> daoPojos = dao.getAll(new DalHints());
//		changePojos(daoPojos);
//		int[] affected = dao.update(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		verifyPojos(dao.getAll(new DalHints()));
//	}
//	
//	@Test
//	public void testBatchUpdate() throws Exception {
//		DalHints hints = new DalHints();
//		List<PersonGen> daoPojos = dao.getAll(new DalHints());
//		changePojos(daoPojos);
//		int[] affected = dao.batchUpdate(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		verifyPojos(dao.getAll(new DalHints()));
//	}
	
	@Test
	public void testtest_build_delete_shards_callback() throws Exception {
		//Integer Age = null;// Test value here
	    //int ret = dao.test_build_delete_shards_callback(Age, new DalHints());
		Integer Age = 22;// Test value here
		DalHints hints = new DalHints();
		DalHints original=hints.clone();
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

		List<DalHintEnum> notNullItems=new ArrayList<>();
		notNullItems.add(DalHintEnum.shards);
		notNullItems.add(DalHintEnum.futureResult);
		notNullItems.add(DalHintEnum.resultCallback);
//		DalHintsChecker.checkNull(hints,notNullItems);
		DalHintsChecker.checkEquals(original,hints,notNullItems);
	}
	
	@Test
	public void testtest_build_delete_allShard_async() throws Exception {
		//Integer Age = null;// Test value here
	    //int ret = dao.test_build_delete_allShard_async(Age, new DalHints());
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

		List<DalHintEnum> notNullItems=new ArrayList<>();
		notNullItems.add(DalHintEnum.allShards);
		notNullItems.add(DalHintEnum.futureResult);
		notNullItems.add(DalHintEnum.asyncExecution);
//		DalHintsChecker.checkNull(hints,notNullItems);
		notNullItems.add(DalHintEnum.implicitAllTableShards);
		DalHintsChecker.checkEquals(original,hints,notNullItems);
	}
	
	@Test
	public void testtest_build_insert_shards_callback() throws Exception {
		//String Name = "";// Test value here
		//Integer Age = null;// Test value here
	    //int ret = dao.test_build_insert_shards_callback(Name, Age, new DalHints());
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
	public void testtest_build_insert_allShard_async() throws Exception {
		//String Name = "";// Test value here
		//Integer Age = null;// Test value here
	    //int ret = dao.test_build_insert_allShard_async(Name, Age, new DalHints());
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
	public void testtest_build_update_shards_callback() throws Exception {
		//String Name = "";// Test value here
		//Integer Age = null;// Test value here
	    //int ret = dao.test_build_update_shards_callback(Name, Age, new DalHints());
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
	public void testtest_build_update_allShard_async() throws Exception {
		//String Name = "";// Test value here
		//Integer Age = null;// Test value here
	    //int ret = dao.test_build_update_allShard_async(Name, Age, new DalHints());
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
	public void testtest_build_query_allShard_async() throws Exception {
		//Integer Age = null;// Test value here
	    //List<PersonGen> ret = dao.test_build_query_allShard_async(Age, new DalHints());
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
//		assertEquals("Initial_Shard_10", ret.get(0).getName());
//		assertEquals("Initial_Shard_00", ret.get(1).getName());
	}

	@Test
	public void testtest_build_query_shards_callback() throws Exception {
		//Integer Age = null;// Test value here
	    //List<PersonGen> ret = dao.test_build_query_shards_callback(Age, new DalHints());
		Integer Age = 20;// Test value here

		DalHints hints = new DalHints();

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
//		assertEquals("Initial_Shard_10", ret.get(0).getName());
//		assertEquals("Initial_Shard_00", ret.get(1).getName());
	}

	@Test
	public void testtest_def_update() throws Exception {
//		DalHints hints = new DalHints();
//	    int ret = dao.test_def_update(hints);
//	    assertNull(ret);
//	    Future<Integer> fr = (Future<Integer>) hints.getAsyncResult();
//		ret = fr.get(); // 异步返回结果
//		assertEquals(6, ret);
	}
	
	@Test
	public void testtest_def_query_shards_callback() throws Exception {
		//String Name = "";// Test value here
		//List<PojoPojo> ret = dao.test_def_query_shards_callback(Name, new DalHints());
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

		List<DalHintEnum> notNullItems=new ArrayList<>();
		notNullItems.add(DalHintEnum.shards);
		notNullItems.add(DalHintEnum.futureResult);
		notNullItems.add(DalHintEnum.resultCallback);
//		DalHintsChecker.checkNull(hints,notNullItems);
		DalHintsChecker.checkEquals(original,hints,notNullItems);
	}
	
	@Test
	public void testtest_def_query_allShard_async() throws Exception {
		//String Name = "";// Test value here
		//List<PojoPojo> ret = dao.test_def_query_allShard_async(Name, new DalHints());
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

		List<DalHintEnum> notNullItems=new ArrayList<>();
		notNullItems.add(DalHintEnum.allShards);
		notNullItems.add(DalHintEnum.futureResult);
		notNullItems.add(DalHintEnum.asyncExecution);
//		DalHintsChecker.checkNull(hints,notNullItems);
		notNullItems.add(DalHintEnum.implicitAllTableShards);
        DalHintsChecker.checkEquals(original,hints,notNullItems);
	}
}
