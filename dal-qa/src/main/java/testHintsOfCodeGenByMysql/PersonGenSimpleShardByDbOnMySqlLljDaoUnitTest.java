package testHintsOfCodeGenByMysql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;

/**
 * JUnit test of PersonGenSimpleShardByDbOnMySqlLljDao class. Before run the
 * unit test, you should initiate the test data and change all the asserts
 * correspond to you case.
 **/
public class PersonGenSimpleShardByDbOnMySqlLljDaoUnitTest {

	private static final String DATA_BASE = "testHintsOfCodeGenByMysql";

	private static DalClient client = null;
	private static PersonGenSimpleShardByDbOnMySqlLljDao dao = null;

	// private Integer getModel_Integer(DalHints hints) throws SQLException {
	// try {
	// return (Integer)hints.getAsyncResult().get();
	// } catch (Exception e) {
	// throw new SQLException(e);
	// }
	// }
	//
	// private List<PersonGenSimpleShardByDbOnMySqlLlj> getModel_list(DalHints
	// hints) throws SQLException {
	// try {
	// return
	// (List<PersonGenSimpleShardByDbOnMySqlLlj>)hints.getAsyncResult().get();
	// } catch (Exception e) {
	// throw new SQLException(e);
	// }
	// }
	//
	// private int[] getModel_ArrayInt(DalHints hints) throws SQLException {
	// try {
	// return (int[])hints.getAsyncResult().get();
	// } catch (Exception e) {
	// throw new SQLException(e);
	// }
	// }
	//
	// private PersonGenSimpleShardByDbOnMySqlLlj getModel_Pojo(DalHints hints)
	// throws SQLException {
	// try {
	// return (PersonGenSimpleShardByDbOnMySqlLlj)hints.getAsyncResult().get();
	// } catch (Exception e) {
	// throw new SQLException(e);
	// }
	// }
	//
	// private class TestQueryResultCallback_Integer implements
	// DalResultCallback {
	// private AtomicReference<Integer> model_Integer = new AtomicReference<>();
	//
	// @Override
	// public <T> void onResult(T result) {
	// model_Integer.set((Integer)result);
	// }
	//
	// public Integer get_Integer() {
	// while(model_Integer.get() == null)
	// try {
	// Thread.sleep(1);
	// } catch (Exception e) {
	// return null;
	// }
	// return model_Integer.get();
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	// // TODO Auto-generated method stub
	//
	// }
	// }
	//
	// public class TestQueryResultCallback_list implements DalResultCallback {
	//
	// private AtomicReference<List<PersonGenSimpleShardByDbOnMySqlLlj>>
	// model_list = new AtomicReference<>();
	//
	// @Override
	// public <T> void onResult(T result) {
	//
	// model_list.set((List<PersonGenSimpleShardByDbOnMySqlLlj>)result);
	// }
	//
	// public List<PersonGenSimpleShardByDbOnMySqlLlj> get_list() {
	// while(model_list.get() == null)
	// try {
	// Thread.sleep(1);
	// } catch (Exception e) {
	// return null;
	// }
	// return model_list.get();
	// }
	// @Override
	// public void onError(Throwable e) {
	// // TODO Auto-generated method stub
	//
	// }
	// }
	// public class TestQueryResultCallback_ArrayInt implements
	// DalResultCallback {
	//
	// private AtomicReference<int[]> model_ArrayInt = new AtomicReference<>();
	//
	// @Override
	// public <T> void onResult(T result) {
	//
	// model_ArrayInt.set((int[])result);
	// }
	//
	// public int[] get_ArrayInt() {
	// while(model_ArrayInt.get() == null)
	// try {
	// Thread.sleep(1);
	// } catch (Exception e) {
	// return null;
	// }
	// return model_ArrayInt.get();
	// }
	// @Override
	// public void onError(Throwable e) {
	// // TODO Auto-generated method stub
	//
	// }
	// }
	//
	// public class TestQueryResultCallback_Pojo implements DalResultCallback {
	//
	// private AtomicReference<PersonGenSimpleShardByDbOnMySqlLlj> model_Pojo =
	// new AtomicReference<>();
	//
	// @Override
	// public <T> void onResult(T result) {
	//
	// model_Pojo.set((PersonGenSimpleShardByDbOnMySqlLlj)result);
	// }
	//
	// public PersonGenSimpleShardByDbOnMySqlLlj get_Pojo() {
	// while(model_Pojo.get() == null)
	// try {
	// Thread.sleep(1);
	// } catch (Exception e) {
	// return null;
	// }
	// return model_Pojo.get();
	// }
	// @Override
	// public void onError(Throwable e) {
	// // TODO Auto-generated method stub
	//
	// }
	// }
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/**
		 * Initialize DalClientFactory. The Dal.config can be specified from
		 * class-path or local file path. One of follow three need to be
		 * enabled.
		 **/
		// DalClientFactory.initPrivateFactory(); //Load from class-path
		// connections.properties
		DalClientFactory.initClientFactory(); // load from class-path Dal.config
		// DalClientFactory.initClientFactory("E:/DalMult.config"); // load from
		// the specified Dal.config file path
		client = DalClientFactory.getClient(DATA_BASE);
		dao = new PersonGenSimpleShardByDbOnMySqlLljDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		dao.test_def_update(new DalHints().inShard(0));
		dao.test_def_update(new DalHints().inShard(1));

		KeyHolder keyHolder = new KeyHolder();
		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos1 = new ArrayList<PersonGenSimpleShardByDbOnMySqlLlj>(
				3);

		for (int i = 0; i < 3; i++) {
			PersonGenSimpleShardByDbOnMySqlLlj daoPojo = new PersonGenSimpleShardByDbOnMySqlLlj();
			daoPojo.setAge(i + 20);
			daoPojo.setName("Initial_Shard_0" + i);
			daoPojos1.add(daoPojo);
		}
		dao.insert(new DalHints().inShard(0), keyHolder, daoPojos1);

		List<PersonGenSimpleShardByDbOnMySqlLlj> daoPojos2 = new ArrayList<PersonGenSimpleShardByDbOnMySqlLlj>(
				3);
		for (int i = 0; i < 3; i++) {
			PersonGenSimpleShardByDbOnMySqlLlj daoPojo = new PersonGenSimpleShardByDbOnMySqlLlj();
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
//		DalHints hints = new DalHints();
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
//		List<PersonGenSimpleShardByDbOnMySqlLlj> list = dao
//				.getAll(new DalHints());
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
//		List<PersonGenSimpleShardByDbOnMySqlLlj> list = dao.queryByPage(
//				pageSize, pageNo, hints);
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
		int ret = dao.test_build_delete_allShard_async(Age, hints);
		assertEquals(0, ret);
		Future<Integer> fr = (Future<Integer>) hints.getAsyncResult();
		ret = fr.get(); // 异步返回结果
		assertEquals(2, ret);

		assertEquals(2, dao.count(new DalHints().inShard(0)));
		assertEquals(2, dao.count(new DalHints().inShard(1)));
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
		List<PersonGenSimpleShardByDbOnMySqlLlj> ret = dao
				.test_build_query_allShard_async(Age, hints);
		assertNull(ret);
		Future<List<PersonGenSimpleShardByDbOnMySqlLlj>> fr = (Future<List<PersonGenSimpleShardByDbOnMySqlLlj>>) hints
				.getAsyncResult();
		ret = fr.get(); // 异步返回结果
		assertEquals(2, ret.size());
		// System.out.println(ret.get(0).getName());
		// System.out.println(ret.get(1).getName());
		assertEquals("Initial_Shard_10", ret.get(0).getName());
		assertEquals("Initial_Shard_00", ret.get(1).getName());
	}

	@Test
	public void testtest_build_query_shards_callback() throws Exception {
		Integer Age = 20;// Test value here

		DalHints hints = new DalHints();

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		DefaultResultCallback callback = new DefaultResultCallback();

		List<PersonGenSimpleShardByDbOnMySqlLlj> ret = dao
				.test_build_query_shards_callback(Age, hints, shards, callback);

		assertNull(ret);

		callback.waitForDone();
		ret = (List<PersonGenSimpleShardByDbOnMySqlLlj>) callback.getResult(); // 异步返回结果

		assertEquals(2, ret.size());
		assertEquals("Initial_Shard_10", ret.get(0).getName());
		assertEquals("Initial_Shard_00", ret.get(1).getName());
	}

	@Test
	public void testtest_def_query_allShard_async() throws Exception {
		String Name = "Initial%";// Test value here
		DalHints hints = new DalHints();
		List<PersonGenSimpleShardByDbOnMySqlLlj> ret = dao
				.test_def_query_allShard_async(Name, hints);
		assertNull(ret);
		Future<List<PersonGenSimpleShardByDbOnMySqlLlj>> fr = (Future<List<PersonGenSimpleShardByDbOnMySqlLlj>>) hints
				.getAsyncResult();
		ret = fr.get(); // 异步返回结果
		assertEquals(6, ret.size());
	}

	@Test
	public void testtest_def_query_shards_callback() throws Exception {
		String Name = "Initial%";// Test value here

		DalHints hints = new DalHints();

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		DefaultResultCallback callback = new DefaultResultCallback();

		List<PersonGenSimpleShardByDbOnMySqlLlj> ret = dao
				.test_def_query_shards_callback(Name, hints, shards, callback);

		assertNull(ret);

		callback.waitForDone();
		ret = (List<PersonGenSimpleShardByDbOnMySqlLlj>) callback.getResult(); // 异步返回结果

		assertEquals(6, ret.size());
	}

	// 自定义增删改不支持inAllShard和inShards，也不支持异步操作，只支持inShard
	// @Test
	// public void testtest_def_update_allShard() throws Exception {
	// String Name = "def_update_allShard";// Test value here
	// Integer Age = 20;// Test value here
	// DalHints hints = new DalHints();
	// int ret = dao.test_def_update_allShard(Name, Age, hints);
	// assertEquals(2, ret);
	//
	// assertEquals("def_update_allShard",dao.queryByPk(1l, new
	// DalHints().inShard(0)).getName());
	// assertEquals("def_update_allShard",dao.queryByPk(1l, new
	// DalHints().inShard(1)).getName());
	// }
	
	@Test
	public void testtest_def_query_count_list() throws Exception {
		DalHints hints = new DalHints();
		List<Long> ret =  dao.test_def_query_count_list(hints);
        assertEquals(2, ret.size());
        assertEquals(3l, ret.get(0).longValue());
        assertEquals(3l, ret.get(1).longValue());
	}
	
	@Test
	public void testtest_def_query_count_merge() throws Exception {
		DalHints hints = new DalHints().mergeBy(new ResultMerger.LongSummary());
		long ret =  dao.test_def_query_count_merge(hints);
        assertEquals(6, ret);
	}
}
