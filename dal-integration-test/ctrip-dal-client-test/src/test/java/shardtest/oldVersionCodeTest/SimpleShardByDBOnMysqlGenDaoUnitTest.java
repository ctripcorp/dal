package shardtest.oldVersionCodeTest;

import com.ctrip.platform.dal.dao.*;
import dao.shard.oldVersionCode.SimpleShardByDBOnMysqlGenDao;
import entity.MysqlPersonTable;
import org.junit.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * JUnit test of PersonMysqlPersonTableDao class. Before run the unit
 * test, you should initiate the test data and change all the asserts correspond
 * to you case.
 **/
public class SimpleShardByDBOnMysqlGenDaoUnitTest {

	private static final String DATA_BASE = "SimpleShardByDBOnMysql";

	private static DalClient client = null;
	private static SimpleShardByDBOnMysqlGenDao dao = null;

	private Integer getModel_Integer(DalHints hints) throws SQLException {
		try {
			return (Integer) hints.getAsyncResult().get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	private List<MysqlPersonTable> getModel_list(DalHints hints)
			throws SQLException {
		try {
			return (List<MysqlPersonTable>) hints.getAsyncResult()
					.get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	private int[] getModel_ArrayInt(DalHints hints) throws SQLException {
		try {
			return (int[]) hints.getAsyncResult().get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	private MysqlPersonTable getModel_Pojo(DalHints hints)
			throws SQLException {
		try {
			return (MysqlPersonTable) hints.getAsyncResult().get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	private class TestQueryResultCallback_Integer implements DalResultCallback {
		private AtomicReference<Integer> model_Integer = new AtomicReference<>();

		@Override
		public <T> void onResult(T result) {
			model_Integer.set((Integer) result);
		}

		public Integer get_Integer() {
			while (model_Integer.get() == null)
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					return null;
				}
			return model_Integer.get();
		}

		@Override
		public void onError(Throwable e) {


		}
	}

	public class TestQueryResultCallback_list implements DalResultCallback {

		private AtomicReference<List<MysqlPersonTable>> model_list = new AtomicReference<>();

		@Override
		public <T> void onResult(T result) {

			model_list.set((List<MysqlPersonTable>) result);
		}

		public List<MysqlPersonTable> get_list() {
			while (model_list.get() == null)
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					return null;
				}
			return model_list.get();
		}

		@Override
		public void onError(Throwable e) {


		}
	}

	public class TestQueryResultCallback_ArrayInt implements DalResultCallback {

		private AtomicReference<int[]> model_ArrayInt = new AtomicReference<>();

		@Override
		public <T> void onResult(T result) {

			model_ArrayInt.set((int[]) result);
		}

		public int[] get_ArrayInt() {
			while (model_ArrayInt.get() == null)
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					return null;
				}
			return model_ArrayInt.get();
		}

		@Override
		public void onError(Throwable e) {


		}
	}

	public class TestQueryResultCallback_Pojo implements DalResultCallback {

		private AtomicReference<MysqlPersonTable> model_Pojo = new AtomicReference<>();

		@Override
		public <T> void onResult(T result) {

			model_Pojo.set((MysqlPersonTable) result);
		}

		public MysqlPersonTable get_Pojo() {
			while (model_Pojo.get() == null)
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					return null;
				}
			return model_Pojo.get();
		}

		@Override
		public void onError(Throwable e) {


		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		client = DalClientFactory.getClient(DATA_BASE);
		dao = new SimpleShardByDBOnMysqlGenDao();
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
			daoPojo.setAge(i + 30);
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

	@Test
	public void testCount() throws Exception {
		DalHints hints1 = new DalHints();
		hints1.inShard(0);
		int ret = dao.count(hints1);
		assertEquals(3, ret);

		DalHints hints2 = new DalHints();
		hints2.inShard(1);
		ret = dao.count(hints2);
		assertEquals(3, ret);
		
//		ret=dao.count(new DalHints().inAllShards());
//		assertEquals(6, ret);
	}

	@Test
	public void testDelete1() throws Exception {
		DalHints hints = new DalHints();
		MysqlPersonTable daoPojo = new MysqlPersonTable();
		daoPojo.setID(1);
		int ret = dao.delete(hints.inShard(0), daoPojo);
		assertEquals(1, ret);
	}

	@Test
	public void testDelete2() throws Exception {
		DalHints hints1 = new DalHints().asyncExecution();// 异步返回结果
		List<MysqlPersonTable> daoPojos1 = new ArrayList<MysqlPersonTable>(
				3);
		for (int i = 0; i < 3; i++) {
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setID(i + 1);
			daoPojo.setAge(i + 20);
			daoPojo.setName("Initial_Shard_0" + i);
			daoPojos1.add(daoPojo);
		}
		hints1.inShard(0);
		int[] affected = dao.delete(hints1, daoPojos1);
		assertNull(affected);
		affected = getModel_ArrayInt(hints1);// 异步返回结果
		assertEquals(3, affected.length);

		TestQueryResultCallback_ArrayInt callback = new TestQueryResultCallback_ArrayInt();// 异步回调
		DalHints hints2 = new DalHints().callbackWith(callback);// 异步回调
		hints2.inShard(1);
		List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(
				3);
		for (int i = 0; i < 3; i++) {
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setID(i + 1);
			daoPojo.setAge(i + 30);
			daoPojo.setName("Initial_Shard_1" + i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.delete(hints2, daoPojos2);
		assertNull(affected);
		affected = callback.get_ArrayInt();// 异步回调
		assertEquals(3, affected.length);
	}

	@Test
	public void testBatchDelete() throws Exception {
		DalHints hints1 = new DalHints().asyncExecution();// 异步返回结果
		List<MysqlPersonTable> daoPojos1 = new ArrayList<MysqlPersonTable>(
				3);
		for (int i = 0; i < 3; i++) {
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setAge(i + 20);
			daoPojo.setName("Initial_Shard_0" + i);
			daoPojos1.add(daoPojo);
		}
		hints1.inShard(0);
		int[] affected = dao.batchDelete(hints1, daoPojos1);
		assertNull(affected);
		affected = getModel_ArrayInt(hints1);// 异步返回结果
		assertEquals(3, affected.length);

		TestQueryResultCallback_ArrayInt callback = new TestQueryResultCallback_ArrayInt();// 异步回调
		DalHints hints2 = new DalHints().callbackWith(callback);// 异步回调
		hints2.inShard(1);
		List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(
				3);
		for (int i = 0; i < 3; i++) {
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setAge(i + 30);
			daoPojo.setName("Initial_Shard_1" + i);
			daoPojos2.add(daoPojo);
		}
		affected = dao.batchDelete(hints2, daoPojos2);
		assertNull(affected);
		affected = callback.get_ArrayInt();// 异步回调
		assertEquals(3, affected.length);
	}

	@Test
	public void testGetAll() throws Exception {
		DalHints hints1 = new DalHints();
		hints1.inShard(1);
		List<MysqlPersonTable> list = dao.getAll(hints1);
		assertEquals(3, list.size());

		DalHints hints2 = new DalHints();// 异步回调
		hints2.inShard(0);
		list = dao.getAll(hints2);
		assertEquals(3, list.size());
	}

	@Test
	public void testInsert1() throws Exception {
		DalHints hints = new DalHints();
		hints.inShard(0);
		MysqlPersonTable daoPojo = new MysqlPersonTable();
		daoPojo.setAge(12);
		daoPojo.setName("Angela");
		int affected = dao.insert(hints, daoPojo);
		assertEquals(1, affected);
		hints.inShard(1);
		daoPojo.setAge(23);
		daoPojo.setName("Lily");
		affected = dao.insert(hints, daoPojo);
		assertEquals(1, affected);
	}

	@Test
	public void testInsert2() throws Exception {

		DalHints hints1 = new DalHints().asyncExecution();// 异步返回结果
		List<MysqlPersonTable> daoPojos = new ArrayList<MysqlPersonTable>(
				3);
		for (int i = 0; i < 3; i++) {
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setAge(i + 50);
			daoPojo.setName("Belle");
			daoPojos.add(daoPojo);
		}
		hints1.inShard(1);
		int[] affected = dao.insert(hints1, daoPojos);
		assertNull(affected);
		affected = getModel_ArrayInt(hints1);// 异步返回结果
		assertEquals(3, affected.length);

		TestQueryResultCallback_ArrayInt callback = new TestQueryResultCallback_ArrayInt();// 异步回调
		DalHints hints2 = new DalHints().callbackWith(callback);// 异步回调
		hints2.inShard(0);
		affected = dao.insert(hints2, daoPojos);
		assertNull(affected);
		affected = callback.get_ArrayInt();// 异步回调
		assertEquals(3, affected.length);
	}

	@Test
	public void testInsert3() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		MysqlPersonTable daoPojo = new MysqlPersonTable();
		daoPojo.setAge(26);
		daoPojo.setName("Ochirly");
		hints.inShard(1);
		int affected = dao.insert(hints, keyHolder, daoPojo);
		assertEquals(1, affected);
		assertEquals(4l, keyHolder.getKey());
	}

	@Test
	public void testInsert4() throws Exception {
		DalHints hints1 = new DalHints().asyncExecution();// 异步返回结果
		KeyHolder keyHolder = new KeyHolder();
		List<MysqlPersonTable> daoPojos = new ArrayList<MysqlPersonTable>(
				3);
		for (int i = 0; i < 3; i++) {
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setAge(i + 50);
			daoPojo.setName("Belle");
			daoPojos.add(daoPojo);
		}
		hints1.inShard(1);
		int[] affected = dao.insert(hints1, keyHolder, daoPojos);
		assertNull(affected);
		affected = getModel_ArrayInt(hints1);// 异步返回结果
		assertEquals(3, affected.length);

		TestQueryResultCallback_ArrayInt callback = new TestQueryResultCallback_ArrayInt();// 异步回调
		DalHints hints2 = new DalHints().callbackWith(callback);// 异步回调
		hints2.inShard(0);
		affected = dao.insert(hints2, keyHolder, daoPojos);
		assertNull(affected);
		affected = callback.get_ArrayInt();// 异步回调
		assertEquals(3, affected.length);
	}

	@Test
	public void testInsert5() throws Exception {
		DalHints hints1 = new DalHints();
		List<MysqlPersonTable> daoPojos = new ArrayList<MysqlPersonTable>(
				3);
		for (int i = 0; i < 3; i++) {
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setAge(i + 50);
			daoPojo.setName("Belle");
			daoPojos.add(daoPojo);
		}
		hints1.inShard(1);
		int[] affected1 = dao.batchInsert(hints1, daoPojos);

		assertEquals(3, affected1.length);
		// assertEquals(1, affected1[0]);
		// assertEquals(1, affected1[1]);
		// assertEquals(1, affected1[2]);

		DalHints hints2 = new DalHints();
		hints2.inShard(0);
		int[] affected2 = dao.batchInsert(hints2, daoPojos);

		assertEquals(3, affected2.length);
		// assertEquals(1, affected2[0]);
		// assertEquals(1, affected2[1]);
		// assertEquals(1, affected2[2]);
	}

	@Test
	public void testCombinedInsert1() throws Exception {
		DalHints hints = new DalHints();
		hints.inShard(1);
		List<MysqlPersonTable> daoPojos = new ArrayList<MysqlPersonTable>(
				2);

		MysqlPersonTable daoPojos1 = new MysqlPersonTable();
		daoPojos1.setAge(35);
		daoPojos1.setName("Bella");
		daoPojos.add(daoPojos1);

		MysqlPersonTable daoPojos2 = new MysqlPersonTable();
		daoPojos2.setAge(47);
		daoPojos2.setName("May");
		daoPojos.add(daoPojos2);

		int affected = dao.combinedInsert(hints, daoPojos);
		assertEquals(2, affected);
	}

	@Test
	public void testCombinedInsert2() throws Exception {
		DalHints hints1 = new DalHints().asyncExecution();// 异步返回结果
		KeyHolder keyHolder = new KeyHolder();
		List<MysqlPersonTable> daoPojos = new ArrayList<MysqlPersonTable>(
				3);
		for (int i = 0; i < 3; i++) {
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setAge(i + 20);
			daoPojo.setName("Saturday");
			daoPojos.add(daoPojo);
		}
		hints1.inShard(0);
		int affected = dao.combinedInsert(hints1, keyHolder, daoPojos);
		assertEquals(0, affected);
		affected = getModel_Integer(hints1);// 异步返回结果
		assertEquals(3, affected);

		TestQueryResultCallback_Integer callback = new TestQueryResultCallback_Integer();// 异步回调
		DalHints hints2 = new DalHints().callbackWith(callback);// 异步回调
		hints2.inShard(1);
		affected = dao.combinedInsert(hints2, keyHolder, daoPojos);
		assertEquals(0, affected);
		affected = callback.get_Integer();// 异步回调
		assertEquals(3, affected);
	}

	@Test
	public void testQueryByPage() throws Exception {
		DalHints hints = new DalHints();
		hints.inShard(0);
		int pageSize = 1;
		int pageNo = 3;
		List<MysqlPersonTable> list = dao.queryByPage(pageSize,
				pageNo, hints);
		assertEquals(1, list.size());
	}

	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 1l;
		DalHints hints1 = new DalHints().asyncExecution();// 异步返回结果
		hints1.inShard(0);
		MysqlPersonTable ret = dao.queryByPk(id, hints1);
		assertNull(ret);
		ret = getModel_Pojo(hints1);// 异步返回结果
		assertEquals("Initial_Shard_00", ret.getName());

		id = 2l;
		TestQueryResultCallback_Pojo callback = new TestQueryResultCallback_Pojo();// 异步回调
		DalHints hints2 = new DalHints().callbackWith(callback);// 异步回调
		hints2.inShard(1);
		ret = dao.queryByPk(id, hints2);
		assertNull(ret);
		ret = callback.get_Pojo();// 异步回调
		assertEquals("Initial_Shard_11", ret.getName());
	}

	@Test
	public void testQueryByPk2() throws Exception {
		MysqlPersonTable pk = new MysqlPersonTable();
		pk.setID(2);
		DalHints hints = new DalHints();

		hints.inShard(0);
		MysqlPersonTable ret = dao.queryByPk(pk, hints);
		assertEquals("Initial_Shard_01", ret.getName());

		hints.inShard(1);
		ret = dao.queryByPk(pk, hints);
		assertEquals("Initial_Shard_11", ret.getName());
	}

	@Test
	public void testUpdate1() throws Exception {

		MysqlPersonTable daoPojo = new MysqlPersonTable();
		daoPojo.setID(1);
		daoPojo.setAge(80);
		daoPojo.setName("UpdateShard11");
		int ret = dao.update(new DalHints().inShard(1), daoPojo);
		assertEquals(1, ret);

		Number id1 = 1l;
		MysqlPersonTable ret1 = dao.queryByPk(id1,
				new DalHints().inShard(1));
		assertEquals("UpdateShard11", ret1.getName());

		daoPojo.setID(2);
		daoPojo.setAge(80);
		daoPojo.setName("UpdateShard02");
		ret = dao.update(new DalHints().inShard(0), daoPojo);
		assertEquals(1, ret);

		Number id2 = 2l;
		MysqlPersonTable ret2 = dao.queryByPk(id2,
				new DalHints().inShard(0));
		assertEquals("UpdateShard02", ret2.getName());

	}

	@Test
	public void testUpdate2() throws Exception {
		DalHints hints1 = new DalHints().asyncExecution();// 异步返回结果
		List<MysqlPersonTable> daoPojos1 = new ArrayList<MysqlPersonTable>(
				2);
		for (int i = 0; i < 2; i++) {
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setID(i + 1);
			daoPojo.setAge(i + 80);
			daoPojo.setName("UpdateShard1" + i);
			daoPojos1.add(daoPojo);
		}
		hints1.inShard(1);
		int[] ret = dao.update(hints1, daoPojos1);
		assertNull(ret);
		ret = getModel_ArrayInt(hints1);// 异步返回结果
		assertEquals(2, ret.length);

		Number id1 = 1l;
		MysqlPersonTable ret1 = dao.queryByPk(id1,
				new DalHints().inShard(1));
		assertEquals("UpdateShard10", ret1.getName());
		Number id2 = 2l;
		MysqlPersonTable ret2 = dao.queryByPk(id2,
				new DalHints().inShard(1));
		assertEquals("UpdateShard11", ret2.getName());

		TestQueryResultCallback_ArrayInt callback = new TestQueryResultCallback_ArrayInt();// 异步回调
		DalHints hints2 = new DalHints().callbackWith(callback);// 异步回调
		List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(
				2);
		for (int i = 0; i < 2; i++) {
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setID(i + 1);
			daoPojo.setAge(i + 80);
			daoPojo.setName("UpdateShard0" + i);
			daoPojos2.add(daoPojo);
		}
		hints2.inShard(0);
		ret = dao.update(hints2, daoPojos2);
		assertNull(ret);
		ret = callback.get_ArrayInt();// 异步回调
		assertEquals(2, ret.length);

		Number id3 = 1l;
		MysqlPersonTable ret3 = dao.queryByPk(id3,
				new DalHints().inShard(0));
		assertEquals("UpdateShard00", ret3.getName());
		Number id4 = 2l;
		MysqlPersonTable ret4 = dao.queryByPk(id4,
				new DalHints().inShard(0));
		assertEquals("UpdateShard01", ret4.getName());
	}

	@Test
	public void testBatchUpdate() throws Exception {
		DalHints hints1 = new DalHints().asyncExecution();// 异步返回结果
		List<MysqlPersonTable> daoPojos1 = new ArrayList<MysqlPersonTable>(
				2);
		for (int i = 0; i < 2; i++) {
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setID(i + 1);
			daoPojo.setAge(i + 80);
			daoPojo.setName("UpdateShard1" + i);
			daoPojos1.add(daoPojo);
		}
		hints1.inShard(1);
		int[] ret = dao.batchUpdate(hints1, daoPojos1);
		assertNull(ret);
		ret = getModel_ArrayInt(hints1);// 异步返回结果
		assertEquals(2, ret.length);

		Number id1 = 1l;
		MysqlPersonTable ret1 = dao.queryByPk(id1,
				new DalHints().inShard(1));
		assertEquals("UpdateShard10", ret1.getName());
		Number id2 = 2l;
		MysqlPersonTable ret2 = dao.queryByPk(id2,
				new DalHints().inShard(1));
		assertEquals("UpdateShard11", ret2.getName());

		TestQueryResultCallback_ArrayInt callback = new TestQueryResultCallback_ArrayInt();// 异步回调
		DalHints hints2 = new DalHints().callbackWith(callback);// 异步回调
		List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(
				2);
		for (int i = 0; i < 2; i++) {
			MysqlPersonTable daoPojo = new MysqlPersonTable();
			daoPojo.setID(i + 1);
			daoPojo.setAge(i + 80);
			daoPojo.setName("UpdateShard0" + i);
			daoPojos2.add(daoPojo);
		}
		hints2.inShard(0);
		ret = dao.batchUpdate(hints2, daoPojos2);
		assertNull(ret);
		ret = callback.get_ArrayInt();// 异步回调
		assertEquals(2, ret.length);

		Number id3 = 1l;
		MysqlPersonTable ret3 = dao.queryByPk(id3,
				new DalHints().inShard(0));
		assertEquals("UpdateShard00", ret3.getName());
		Number id4 = 2l;
		MysqlPersonTable ret4 = dao.queryByPk(id4,
				new DalHints().inShard(0));
		assertEquals("UpdateShard01", ret4.getName());
	}

	@Test
	public void testtest_build_delete() throws Exception {
		TestQueryResultCallback_Integer callback = new TestQueryResultCallback_Integer();// 异步回调
		DalHints hints = new DalHints().callbackWith(callback);// 异步回调
		List<Integer> Age = new ArrayList<Integer>(2);
		Age.add(21);
		Age.add(31);

		int ret = dao.test_build_delete(Age, hints.inAllShards());
		assertEquals(0, ret);
		// ret = getModel_Integer(hints);//异步返回结果
		ret = callback.get_Integer();// 异步回调
		assertEquals(2, ret);
	}

	@Test
	public void testtest_build_insert() throws Exception {

		TestQueryResultCallback_Integer callback = new TestQueryResultCallback_Integer();// 异步回调
		DalHints hints3 = new DalHints().callbackWith(callback);// 异步回调

		Integer Age = 26;// Test value here
		String Name = "KarryAll";

		int ret = dao.test_build_insert(Name, Age, hints3.inAllShards());
		assertEquals(0, ret);

		ret = callback.get_Integer();// 异步回调
		assertEquals(2, ret);
	}

	@Test
	public void testtest_build_update() throws Exception {
		// String Name = "";// Test value here
		// Integer Age = null;// Test value here
		// int ret = dao.test_build_update(Name, Age, new DalHints());
		TestQueryResultCallback_Integer callback = new TestQueryResultCallback_Integer();// 异步回调
		DalHints hints = new DalHints().callbackWith(callback);// 异步回调

		String Name = "callback";
		List<Integer> Age = new ArrayList<Integer>(2);
		Age.add(21);
		Age.add(31);

		int ret = dao.test_build_update(Name, Age, hints.inAllShards());
		assertEquals(0, ret);
		// ret = getModel_Integer(hints);//异步返回结果
		ret = callback.get_Integer();// 异步回调
		assertEquals(2, ret);
	}

	@Test
	public void testtest_build_query_first() throws Exception {

		List<Integer> Age = new ArrayList<Integer>(6);
		Age.add(21);
		Age.add(20);
		Age.add(22);
		Age.add(30);
		Age.add(31);
		Age.add(32);

		MysqlPersonTable ret = dao.test_build_query_first(Age,
				new DalHints().inShard(0));
		assertEquals("Initial_Shard_00", ret.getName());

		ret = dao.test_build_query_first(Age, new DalHints().inShard(1));
		assertEquals("Initial_Shard_10", ret.getName());

		ret = dao.test_build_query_first(Age, new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret.getName());

	}

	@Test
	public void testtest_build_query() throws Exception {
		List<Integer> Age = new ArrayList<Integer>(6);
		Age.add(21);
		Age.add(20);
		Age.add(22);
		Age.add(30);
		Age.add(31);
		Age.add(32);

		TestQueryResultCallback_list callback = new TestQueryResultCallback_list();// 异步回调
		DalHints hints = new DalHints().callbackWith(callback);// 异步回调

		List<MysqlPersonTable> ret = dao.test_build_query(Age,
				hints.inAllShards());
		assertNull(ret);
		ret = callback.get_list();// 异步回调
		assertEquals(6, ret.size());
		// for (int i = 0; i < ret.size(); i++)
		// System.out.println(ret.get(i).getName());
	}

	// 分页不要使用allshard
	@Test
	public void testtest_build_query_byPage() throws Exception {
		// Integer Age = null;// Test value here
		// List<PersonMysqlPersonTable> ret =
		// dao.test_build_query_byPage(Age, 1, 10, new DalHints());
		List<Integer> Age = new ArrayList<Integer>(6);
		Age.add(21);
		Age.add(20);
		Age.add(22);
		Age.add(30);
		Age.add(31);
		Age.add(32);

		List<MysqlPersonTable> ret = dao.test_build_query_byPage(Age,
				1, 2, new DalHints().inShard(0));
		assertEquals(2, ret.size());
		// for(int i=0;i<ret.size();i++)
		// System.out.println(ret.get(i).getName());
		ret = dao.test_build_query_byPage(Age, 1, 2, new DalHints().inShard(1));
		assertEquals(2, ret.size());

	}

	@Test
	public void testtest_build_query_single() throws Exception {
		List<Integer> Age = new ArrayList<Integer>(2);
		Age.add(21);

		Age.add(41);

		MysqlPersonTable ret = dao.test_build_query_single(Age,
				new DalHints().inShard(0));
		assertEquals("Initial_Shard_01", ret.getName());

		ret = dao.test_build_query_single(Age, new DalHints().inShard(1));
		assertNull(ret);

		ret = dao.test_build_query_single(Age, new DalHints().inAllShards());
		assertEquals("Initial_Shard_01", ret.getName());
	}

	@Test
	public void testtest_build_query_field_first() throws Exception {
		// Integer Age = null;// Test value here
		// PersonMysqlPersonTable ret =
		// dao.test_build_query_field_first(Age, new DalHints());
		List<Integer> Age = new ArrayList<Integer>(6);
		Age.add(21);
		Age.add(20);
		Age.add(22);
		Age.add(30);
		Age.add(31);
		Age.add(32);

		String ret = dao.test_build_query_field_first(Age,
				new DalHints().inShard(0));
		assertEquals("Initial_Shard_00", ret);

		ret = dao.test_build_query_field_first(Age, new DalHints().inShard(1));
		assertEquals("Initial_Shard_10", ret);

		ret = dao.test_build_query_field_first(Age,
				new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret);
	}

	@Test
	public void testtest_build_query_field() throws Exception {
		// Integer Age = null;// Test value here
		// List<String> ret = dao.test_build_query_field(Age, new DalHints());
		List<Integer> Age = new ArrayList<Integer>(6);
		Age.add(21);
		Age.add(20);
		Age.add(22);
		Age.add(30);
		Age.add(31);
		Age.add(32);
		List<String> ret = dao.test_build_query_field(Age,
				new DalHints().inShard(0));
		assertEquals(3, ret.size());

		ret = dao.test_build_query_field(Age, new DalHints().inShard(1));
		assertEquals(3, ret.size());

		ret = dao.test_build_query_field(Age, new DalHints().inAllShards());
		assertEquals(6, ret.size());
	}

	@Test
	public void testtest_build_query_field_byPage() throws Exception {
		// Integer Age = null;// Test value here
		// List<String> ret = dao.test_build_query_field_byPage(Age, 1, 10, new
		// DalHints());
		List<Integer> Age = new ArrayList<Integer>(6);
		Age.add(21);
		Age.add(20);
		Age.add(22);
		Age.add(30);
		Age.add(31);
		Age.add(32);

		List<String> ret = dao.test_build_query_field_byPage(Age, 1, 2,
				new DalHints().inShard(0));
		assertEquals(2, ret.size());
		ret = dao.test_build_query_field_byPage(Age, 1, 2,
				new DalHints().inShard(1));
		assertEquals(2, ret.size());

	}

	@Test
	public void testtest_build_query_field_single() throws Exception {
		// Integer Age = null;// Test value here
		// String ret = dao.test_build_query_field_single(Age, new DalHints());
		List<Integer> Age = new ArrayList<Integer>(2);
		Age.add(21);
		Age.add(41);

		String ret = dao.test_build_query_field_single(Age,
				new DalHints().inShard(0));
		assertEquals("Initial_Shard_01", ret);

		ret = dao.test_build_query_field_single(Age, new DalHints().inShard(1));
		assertNull(ret);

		ret = dao.test_build_query_field_single(Age,
				new DalHints().inAllShards());
		assertEquals("Initial_Shard_01", ret);
	}

	@Test
	public void test_def_query_list() throws Exception {
		TestQueryResultCallback_list callback = new TestQueryResultCallback_list();// 异步回调
		DalHints hints = new DalHints().callbackWith(callback);// 异步回调

		List<Integer> Age = new ArrayList<Integer>(6);
		Age.add(21);
		Age.add(20);
		Age.add(22);
		Age.add(30);
		Age.add(31);
		Age.add(32);

		List<MysqlPersonTable> ret = dao.test_def_query_list(Age,
				hints.inAllShards());
		assertNull(ret);
		// ret = getModel_list(hints);//异步返回结果
		ret = callback.get_list();// 异步回调
		assertEquals(6, ret.size());

		ret = dao.test_def_query_list(Age, new DalHints().inShard(0));
		assertEquals(3, ret.size());

		ret = dao.test_def_query_list(Age, new DalHints().inShard(1));
		assertEquals(3, ret.size());
	}

	@Test
	public void test_def_query_listByPage() throws Exception {
		List<Integer> Age = new ArrayList<Integer>(6);
		Age.add(21);
		Age.add(20);
		Age.add(22);
		Age.add(30);
		Age.add(31);
		Age.add(32);

		List<MysqlPersonTable> ret = dao.test_def_query_listByPage(
				Age, 1, 2, new DalHints().inShard(0));
		assertEquals(2, ret.size());

		ret = dao.test_def_query_listByPage(Age, 1, 2,
				new DalHints().inShard(1));
		assertEquals(2, ret.size());
	}

	@Test
	public void test_def_query_list_single() throws Exception {
		List<Integer> Age = new ArrayList<Integer>(2);
		Age.add(21);
		Age.add(41);

		MysqlPersonTable ret = dao.test_def_query_list_single(Age,
				new DalHints().inShard(0));
		assertEquals("Initial_Shard_01", ret.getName());

		ret = dao.test_def_query_list_single(Age, new DalHints().inShard(1));
		assertNull(ret);

		ret = dao.test_def_query_list_single(Age, new DalHints().inAllShards());
		assertEquals("Initial_Shard_01", ret.getName());
	}

	@Test
	public void test_def_query_list_first() throws Exception {
		List<Integer> Age = new ArrayList<Integer>(6);
		Age.add(21);
		Age.add(20);
		Age.add(22);
		Age.add(30);
		Age.add(31);
		Age.add(32);

		MysqlPersonTable ret = dao.test_def_query_list_first(Age,
				new DalHints().inShard(0));
		assertEquals("Initial_Shard_00", ret.getName());

		ret = dao.test_def_query_list_first(Age, new DalHints().inShard(1));
		assertEquals("Initial_Shard_10", ret.getName());

		ret = dao.test_def_query_list_first(Age, new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret.getName());
	}

	@Test
	public void test_def_query_field() throws Exception {

		List<Integer> Age = new ArrayList<Integer>(6);
		Age.add(21);
		Age.add(20);
		Age.add(22);
		Age.add(30);
		Age.add(31);
		Age.add(32);

		List<String> ret = dao.test_def_query_field(Age,
				new DalHints().inAllShards());

		assertEquals(6, ret.size());

		ret = dao.test_def_query_field(Age, new DalHints().inShard(0));
		assertEquals(3, ret.size());

		ret = dao.test_def_query_field(Age, new DalHints().inShard(1));
		assertEquals(3, ret.size());
	}

	@Test
	public void test_def_query_field_byPage() throws Exception {
		List<Integer> Age = new ArrayList<Integer>(6);
		Age.add(21);
		Age.add(20);
		Age.add(22);
		Age.add(30);
		Age.add(31);
		Age.add(32);

		List<String> ret = dao.test_def_query_field_byPage(Age, 1, 2,
				new DalHints().inShard(0));
		assertEquals(2, ret.size());

		ret = dao.test_def_query_field_byPage(Age, 1, 2,
				new DalHints().inShard(1));
		assertEquals(2, ret.size());
	}

	@Test
	public void test_def_query_field_single() throws Exception {
		List<Integer> Age = new ArrayList<Integer>(2);
		Age.add(21);
		Age.add(41);

		String ret = dao.test_def_query_field_single(Age,
				new DalHints().inShard(0));
		assertEquals("Initial_Shard_01", ret);

		ret = dao.test_def_query_field_single(Age, new DalHints().inShard(1));
		assertNull(ret);

		ret = dao
				.test_def_query_field_single(Age, new DalHints().inAllShards());
		assertEquals("Initial_Shard_01", ret);
	}

	@Test
	public void test_def_query_field_first() throws Exception {
		List<Integer> Age = new ArrayList<Integer>(6);
		Age.add(21);
		Age.add(20);
		Age.add(22);
		Age.add(30);
		Age.add(31);
		Age.add(32);

		String ret = dao.test_def_query_field_first(Age,
				new DalHints().inShard(0));
		assertEquals("Initial_Shard_00", ret);

		ret = dao.test_def_query_field_first(Age, new DalHints().inShard(1));
		assertEquals("Initial_Shard_10", ret);

		ret = dao.test_def_query_field_first(Age, new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret);
	}

	@Test
	public void test_def_update() throws Exception {
		int ret = dao.count(new DalHints().inShard(0));
		assertEquals(3, ret);
		dao.test_def_update(new DalHints().inShard(0));
		ret = dao.count(new DalHints().inShard(0));
		assertEquals(0, ret);

		ret = dao.count(new DalHints().inShard(1));
		assertEquals(3, ret);
		dao.test_def_update(new DalHints().inShard(1));
		ret = dao.count(new DalHints().inShard(1));
		assertEquals(0, ret);
	}

	@Test
	public void test_def_delete() throws Exception {
		List<Integer> Age = new ArrayList<Integer>(2);
		Age.add(20);
		Age.add(21);

		int ret = dao.test_def_delete(Age, new DalHints().inShard(0));
		assertEquals(2, ret);
	}

	@Test
	public void testtest_build_delete_equal() throws Exception {
		/*
		 * Integer param1 = 23;// Test value here int ret =
		 * dao.test_build_delete_equal(param1, new DalHints().inShard(0));
		 * assertEquals(1, ret);
		 * 
		 * param1 = 52;// Test value here ret =
		 * dao.test_build_delete_equal(param1, new DalHints().inShard(1));
		 * assertEquals(1, ret);
		 */

		// DalHints hints = new DalHints().asyncExecution();//异步返回结果
		TestQueryResultCallback_Integer callback = new TestQueryResultCallback_Integer();// 异步回调
		DalHints hints = new DalHints().callbackWith(callback);// 异步回调

		Integer param1 = 31;// Test value here

		int ret = dao.test_build_delete_equal(param1, hints.inAllShards());
		assertEquals(0, ret);
		// ret = getModel_Integer(hints);//异步返回结果
		ret = callback.get_Integer();// 异步回调
		assertEquals(1, ret);
	}

	@Test
	public void testtest_build_insert_equal() throws Exception {
		/*
		 * DalHints hints1 = new DalHints(); DalHints hints2 = new DalHints();
		 */
		// DalHints hints3 = new DalHints().asyncExecution();//异步返回结果
		TestQueryResultCallback_Integer callback = new TestQueryResultCallback_Integer();// 异步回调
		DalHints hints3 = new DalHints().callbackWith(callback);// 异步回调

		Integer Age = 26;// Test value here
		String Name = "KarryAll";
		/*
		 * String Name = "Karry0"; hints1.inShard(0); int ret =
		 * dao.test_build_insert_equal(Name, Age, hints1); assertEquals(1, ret);
		 * 
		 * Name = "Karry1"; hints2.inShard(1); ret =
		 * dao.test_build_insert_equal(Name, Age, hints2); assertEquals(1, ret);
		 */

		int ret = dao.test_build_insert_equal(Name, Age, hints3.inAllShards());
		assertEquals(0, ret);
		// ret = getModel(hints3);//异步返回结果
		ret = callback.get_Integer();// 异步回调
		assertEquals(2, ret);
	}

	@Test
	public void testtest_build_update_equal() throws Exception {
		/*
		 * String Name = "AppleUpdate";// Test value here Integer Age = 23;//
		 * Test value here int ret = dao.test_build_update_equal(Name, Age, new
		 * DalHints().inShard(0)); assertEquals(1, ret);
		 * 
		 * Name = "BlueberryUpdate";// Test value here Age = 52;// Test value
		 * here ret = dao.test_build_update_equal(Name, Age, new
		 * DalHints().inShard(1)); assertEquals(1, ret);
		 */

		// DalHints hints = new DalHints().asyncExecution();//异步返回结果
		TestQueryResultCallback_Integer callback = new TestQueryResultCallback_Integer();// 异步回调
		DalHints hints = new DalHints().callbackWith(callback);// 异步回调

		String Name = "callback";
		Integer Age = 30;

		int ret = dao.test_build_update_equal(Name, Age, hints.inAllShards());
		assertEquals(0, ret);
		// ret = getModel_Integer(hints);//异步返回结果
		ret = callback.get_Integer();// 异步回调
		assertEquals(1, ret);
	}

	@Test
	public void testtest_build_query_equal() throws Exception {
		Integer Age = 20;// Test value here
		/*
		 * List<MysqlPersonTable> ret = dao.test_build_query_equal(Age,
		 * new DalHints().inShard(0)); assertEquals(2, ret.size());
		 * 
		 * ret = dao.test_build_query_equal(Age, new DalHints().inShard(1));
		 * assertEquals(3, ret.size());
		 */

		// DalHints hints = new DalHints().asyncExecution();//异步返回结果

		TestQueryResultCallback_list callback = new TestQueryResultCallback_list();// 异步回调
		DalHints hints = new DalHints().callbackWith(callback);// 异步回调

		List<MysqlPersonTable> ret = dao.test_build_query_equal(Age,
				hints.inAllShards());
		assertNull(ret);
		// ret = getModel_list(hints);//异步返回结果
		ret = callback.get_list();// 异步回调
		assertEquals(1, ret.size());
	}

	@Test
	public void testtest_build_query_single_greaterThan() throws Exception {
		Integer Age = 31;// Test value here
		/*
		 * List<MysqlPersonTable> ret = dao.test_build_query_equal(Age,
		 * new DalHints().inShard(0)); assertEquals(2, ret.size());
		 * 
		 * ret = dao.test_build_query_equal(Age, new DalHints().inShard(1));
		 * assertEquals(3, ret.size());
		 */

		// DalHints hints = new DalHints().asyncExecution();//异步返回结果

		TestQueryResultCallback_Pojo callback = new TestQueryResultCallback_Pojo();// 异步回调
		DalHints hints = new DalHints().callbackWith(callback);// 异步回调

		MysqlPersonTable ret = dao
				.test_build_query_single_greaterThan(Age, hints.inShard(1));
		assertNull(ret);
		// ret = getModel_list(hints);//异步返回结果
		ret = callback.get_Pojo();// 异步回调
		assertNotNull(ret);

		assertEquals("Initial_Shard_12", ret.getName());

	}

	@Test
	public void testtest_build_query_first_greaterThan() throws Exception {
		Integer Age = 20;// Test value here
		/*
		 * List<MysqlPersonTable> ret = dao.test_build_query(Age, new
		 * DalHints().inShard(0)); assertEquals(2, ret.size());
		 * 
		 * ret = dao.test_build_query(Age, new DalHints().inShard(1));
		 * assertEquals(3, ret.size());
		 */

		// DalHints hints = new DalHints().asyncExecution();//异步返回结果

		TestQueryResultCallback_Pojo callback = new TestQueryResultCallback_Pojo();// 异步回调
		DalHints hints = new DalHints().callbackWith(callback);// 异步回调

		MysqlPersonTable ret = dao.test_build_query_first_greaterThan(
				Age, hints.inAllShards());
		assertNull(ret);
		// ret = getModel_list(hints);//异步返回结果
		ret = callback.get_Pojo();// 异步回调
		assertNotNull(ret);
		assertEquals("Initial_Shard_01", ret.getName());
	}

	// @Test
	// public void testtest_build_query_page() throws SQLException{
	// Integer Age = 20;
	// int pageNo=1;
	// int pageSize=5;
	// Integer ret = dao.test_build_query_nullable(Age,pageNo,pageSize, new
	// DalHints().inAllShards());
	// assertEquals(5, ret.intValue());
	// }

	// 自定义，查询
	@Test
	public void testtest_def_query_equal() throws Exception {
		/*
		 * Integer Age = 20;// Test value here List<MysqlPersonTable>
		 * ret = dao.test_def_query(Age, new DalHints().inShard(0));
		 * assertEquals(2, ret.size());
		 * 
		 * Age = 23;// Test value here ret = dao.test_def_query(Age, new
		 * DalHints().inShard(1)); assertEquals(4, ret.size());
		 */

		// DalHints hints = new DalHints().asyncExecution();//异步返回结果

		TestQueryResultCallback_list callback = new TestQueryResultCallback_list();// 异步回调
		DalHints hints = new DalHints().callbackWith(callback);// 异步回调

		Integer Age = 22;// Test value here
		List<MysqlPersonTable> ret = dao.test_def_query_equal(Age,
				hints.inAllShards());
		assertNull(ret);
		// ret = getModel_list(hints);//异步返回结果
		ret = callback.get_list();// 异步回调
		assertEquals(1, ret.size());
		
		Age = 100;
		ret = dao.test_def_query_equal(Age,
				hints.inShard(0));
		assertNull(ret);
	}

	// 自定义，增删改，不支持异步
	@Test
	public void testtest_def_update_tableID() throws Exception {
		int ret1 = dao.test_def_update(new DalHints().inShard(0), "0");
		int ret2 = dao.test_def_update(new DalHints().inShard(0), "1");
		assertEquals(0, ret1);
		assertEquals(0, ret2);

		int ret3 = dao.test_def_update(new DalHints().inShard(1), "0");
		int ret4 = dao.test_def_update(new DalHints().inShard(1), "1");
		assertEquals(0, ret3);
		assertEquals(0, ret4);
	}
}
