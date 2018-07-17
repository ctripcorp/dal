package shardtest.oldVersionCodeTest;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import dao.shard.oldVersionCode.SimpleShardByDBOnSqlServerGenDao;
import entity.SqlServerPeopleTable;
import org.junit.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * JUnit test of PeopleSqlServerPeopleTableDao class. Before run the
 * unit test, you should initiate the test data and change all the asserts
 * correspond to you case.
 **/
public class SimpleShardByDBOnSqlServerGenDaoUnitTest {

	private static final String DATA_BASE = "SimpleShardByDBOnSqlServer";

	private static DalClient client = null;
	private static SimpleShardByDBOnSqlServerGenDao dao = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = DalClientFactory.getClient(DATA_BASE);
		dao = new SimpleShardByDBOnSqlServerGenDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		dao.test_def_update(new DalHints().inShard(0));
		dao.test_def_update(new DalHints().inShard(1));

		List<SqlServerPeopleTable> daoPojos1 = new ArrayList<SqlServerPeopleTable>(
				3);
		for (int i = 0; i < 3; i++) {
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Initial_Shard_0" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos1.add(daoPojo);
		}
		dao.insert(new DalHints().inShard(0), daoPojos1);

		List<SqlServerPeopleTable> daoPojos2 = new ArrayList<SqlServerPeopleTable>(
				3);
		for (int i = 0; i < 3; i++) {
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
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

	@Test
	public void testCount() throws Exception {
		int ret1 = dao.count(new DalHints().inShard(0));
		assertEquals(3, ret1);
		int ret2 = dao.count(new DalHints().inShard(1));
		assertEquals(3, ret2);
	}

	@Test
	public void testDelete1() throws Exception {

		SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
		daoPojo.setPeopleID(1l);
		int ret = dao.delete(new DalHints().inShard(0), daoPojo);
		// assertEquals(1, ret);

		Number id = 1l;
		SqlServerPeopleTable ret1 = dao.queryByPk(id,
				new DalHints().inShard(0));
		assertNull(ret1);

		ret = dao.delete(new DalHints().inShard(1), daoPojo);
		// assertEquals(1, ret);
		SqlServerPeopleTable ret2 = dao.queryByPk(id,
				new DalHints().inShard(1));
		assertNull(ret2);

	}

	@Test
	public void testDelete2() throws Exception {
		List<SqlServerPeopleTable> daoPojos1 = new ArrayList<SqlServerPeopleTable>(
				3);
		for (int i = 0; i < 3; i++) {
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Initial_Shard_0" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.delete(new DalHints().inShard(0), daoPojos1);
		assertEquals(3, affected.length);

		List<SqlServerPeopleTable> daoPojos2 = new ArrayList<SqlServerPeopleTable>(
				3);
		for (int i = 0; i < 3; i++) {
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Initial_Shard_1" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos2.add(daoPojo);
		}
		affected = dao.delete(new DalHints().inShard(1), daoPojos2);
		assertEquals(3, affected.length);
	}

	@Test
	public void testBatchDelete() throws Exception {
		List<SqlServerPeopleTable> daoPojos1 = new ArrayList<SqlServerPeopleTable>(
				3);
		for (int i = 0; i < 3; i++) {
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Initial_Shard_0" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.batchDelete(new DalHints().inShard(0), daoPojos1);
		assertEquals(3, affected.length);

		List<SqlServerPeopleTable> daoPojos2 = new ArrayList<SqlServerPeopleTable>(
				3);
		for (int i = 0; i < 3; i++) {
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Initial_Shard_1" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos2.add(daoPojo);
		}
		affected = dao.batchDelete(new DalHints().inShard(1), daoPojos2);
		assertEquals(3, affected.length);
	}

	@Test
	public void testGetAll() throws Exception {
		List<SqlServerPeopleTable> list = dao.getAll(new DalHints()
				.inShard(0));
		assertEquals(3, list.size());

		list = dao.getAll(new DalHints().inShard(1));
		assertEquals(3, list.size());

		list = dao.getAll(new DalHints().inAllShards());
		assertEquals(6, list.size());
	}

	@Test
	public void testInsert1() throws Exception {
		DalHints hints = new DalHints();
		SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
		daoPojo.setPeopleID(4l);
		daoPojo.setName("Initial_Shard_14");
		daoPojo.setCityID(24);
		daoPojo.setProvinceID(34);
		daoPojo.setCountryID(44);

		int affected = dao.insert(new DalHints().inShard(0), daoPojo);
		Number id = 4l;
		SqlServerPeopleTable ret1 = dao.queryByPk(id,
				new DalHints().inShard(0));
		assertNotNull(ret1);

		affected = dao.insert(new DalHints().inShard(1), daoPojo);

		SqlServerPeopleTable ret2 = dao.queryByPk(id,
				new DalHints().inShard(1));
		assertNotNull(ret2);

	}

	@Test
	public void testInsert2() throws Exception {
		List<SqlServerPeopleTable> daoPojos1 = new ArrayList<SqlServerPeopleTable>(
				3);
		for (int i = 0; i < 3; i++) {
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Initial_Shard_0" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.insert(new DalHints().inShard(0), daoPojos1);
		assertEquals(3, affected.length);

		List<SqlServerPeopleTable> daoPojos2 = new ArrayList<SqlServerPeopleTable>(
				3);
		for (int i = 0; i < 3; i++) {
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Initial_Shard_1" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos2.add(daoPojo);
		}
		affected = dao.insert(new DalHints().inShard(1), daoPojos2);
		assertEquals(3, affected.length);
	}

	@Test
	public void testInsert3() throws Exception {
		KeyHolder keyHolder1 = new KeyHolder();

		SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
		// daoPojo.setPeopleID(4l);
		daoPojo.setName("Initial_Shard_14");
		daoPojo.setCityID(24);
		daoPojo.setProvinceID(34);
		daoPojo.setCountryID(44);

		int affected = dao.insert(new DalHints().inShard(0), keyHolder1,
				daoPojo);
		assertEquals(4l, keyHolder1.getKey());
		Number id = 4l;
		SqlServerPeopleTable ret1 = dao.queryByPk(id,
				new DalHints().inShard(0));
		assertNotNull(ret1);

		KeyHolder keyHolder2 = new KeyHolder();
		affected = dao.insert(new DalHints().inShard(1), keyHolder2, daoPojo);
		assertEquals(4l, keyHolder2.getKey());
		SqlServerPeopleTable ret2 = dao.queryByPk(id,
				new DalHints().inShard(1));
		assertNotNull(ret2);
	}

	@Test
	public void testInsert4() throws Exception {

		KeyHolder keyHolder1 = new KeyHolder();
		List<SqlServerPeopleTable> daoPojos1 = new ArrayList<SqlServerPeopleTable>(
				3);
		for (int i = 0; i < 3; i++) {
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Initial_Shard_0" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.insert(new DalHints().inShard(0), keyHolder1,
				daoPojos1);
		assertEquals(3, affected.length);
		assertEquals(3, keyHolder1.size());

		KeyHolder keyHolder2 = new KeyHolder();
		List<SqlServerPeopleTable> daoPojos2 = new ArrayList<SqlServerPeopleTable>(
				3);
		for (int i = 0; i < 3; i++) {
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Initial_Shard_1" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos2.add(daoPojo);
		}
		affected = dao.insert(new DalHints().inShard(1), keyHolder2, daoPojos2);
		assertEquals(3, affected.length);
		assertEquals(3, keyHolder2.size());
	}

	@Test
	public void testInsert5() throws Exception {

		List<SqlServerPeopleTable> daoPojos1 = new ArrayList<SqlServerPeopleTable>(
				3);
		for (int i = 0; i < 3; i++) {
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setPeopleID(i+6L);
			daoPojo.setName("Initial_Shard_0" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.batchInsert(new DalHints().inShard(0), daoPojos1);
		assertEquals(3, affected.length);

		List<SqlServerPeopleTable> daoPojos2 = new ArrayList<SqlServerPeopleTable>(
				3);
		for (int i = 0; i < 3; i++) {
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setPeopleID(i+7L);
			daoPojo.setName("Initial_Shard_1" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos2.add(daoPojo);
		}
		affected = dao.batchInsert(new DalHints().inShard(1), daoPojos2);
		assertEquals(3, affected.length);
	}

	@Test
	public void testQueryByPage() throws Exception {
		int pageSize = 2;
		int pageNo = 1;
		List<SqlServerPeopleTable> list = dao.queryByPage(pageSize,
				pageNo, new DalHints().inShard(0));
		assertEquals(2, list.size());

		list = dao.queryByPage(pageSize, pageNo, new DalHints().inShard(1));
		assertEquals(2, list.size());
	}

	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 1l;
		SqlServerPeopleTable ret = dao.queryByPk(id,
				new DalHints().inShard(0));
		assertEquals("Initial_Shard_00", ret.getName());

		ret = dao.queryByPk(id, new DalHints().inShard(1));
		assertEquals("Initial_Shard_10", ret.getName());
	}

	@Test
	public void testQueryByPk2() throws Exception {
		SqlServerPeopleTable pk = new SqlServerPeopleTable();
		pk.setPeopleID(1l);
		SqlServerPeopleTable ret = dao.queryByPk(pk,
				new DalHints().inShard(0));
		assertEquals("Initial_Shard_00", ret.getName());

		ret = dao.queryByPk(pk, new DalHints().inShard(1));
		assertEquals("Initial_Shard_10", ret.getName());
	}

	@Test
	public void testUpdate1() throws Exception {
		DalHints hints = new DalHints();
		SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
		daoPojo.setPeopleID(1l);
		daoPojo.setName("updateShard00");
		int ret = dao.update(new DalHints().inShard(0), daoPojo);
		SqlServerPeopleTable daoPojo1 = dao.queryByPk(1l,
				new DalHints().inShard(0));
		assertEquals("updateShard00", daoPojo1.getName());

		daoPojo.setName("updateShard10");
		ret = dao.update(new DalHints().inShard(1), daoPojo);
		SqlServerPeopleTable daoPojo2 = dao.queryByPk(1l,
				new DalHints().inShard(1));
		assertEquals("updateShard10", daoPojo2.getName());
	}

	@Test
	public void testUpdate2() throws Exception {
		DalHints hints1 = new DalHints();
		List<SqlServerPeopleTable> daoPojos1 = new ArrayList<SqlServerPeopleTable>(
				2);
		for (int i = 0; i < 2; i++) {
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Update_Shard_0" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos1.add(daoPojo);
		}
		hints1.inShard(0);
		int[] ret0 = dao.update(hints1, daoPojos1);
		assertEquals(2, ret0.length);

		Long peopleID = 1l;
		SqlServerPeopleTable ret1 = dao.queryByPk(peopleID,
				new DalHints().inShard(0));
		assertEquals("Update_Shard_00", ret1.getName());

		peopleID = 2l;
		SqlServerPeopleTable ret2 = dao.queryByPk(peopleID,
				new DalHints().inShard(0));
		assertEquals("Update_Shard_01", ret2.getName());

		DalHints hints2 = new DalHints();
		List<SqlServerPeopleTable> daoPojos2 = new ArrayList<SqlServerPeopleTable>(
				2);
		for (int i = 0; i < 2; i++) {
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Update_Shard_1" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos2.add(daoPojo);
		}
		hints2.inShard(1);
		int[] ret3 = dao.update(hints2, daoPojos2);
		assertEquals(2, ret3.length);

		peopleID = 1l;
		SqlServerPeopleTable ret4 = dao.queryByPk(peopleID,
				new DalHints().inShard(1));
		assertEquals("Update_Shard_10", ret4.getName());

		peopleID = 2l;
		SqlServerPeopleTable ret5 = dao.queryByPk(peopleID,
				new DalHints().inShard(1));
		assertEquals("Update_Shard_11", ret5.getName());
	}

	@Test
	public void testBatchUpdate() throws Exception {
		DalHints hints1 = new DalHints();
		List<SqlServerPeopleTable> daoPojos1 = new ArrayList<SqlServerPeopleTable>(
				2);
		for (int i = 0; i < 2; i++) {
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Update_Shard_0" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos1.add(daoPojo);
		}
		hints1.inShard(0);
		int[] ret0 = dao.batchUpdate(hints1, daoPojos1);
		assertEquals(2, ret0.length);

		Long peopleID = 1l;
		SqlServerPeopleTable ret1 = dao.queryByPk(peopleID,
				new DalHints().inShard(0));
		assertEquals("Update_Shard_00", ret1.getName());

		peopleID = 2l;
		SqlServerPeopleTable ret2 = dao.queryByPk(peopleID,
				new DalHints().inShard(0));
		assertEquals("Update_Shard_01", ret2.getName());

		DalHints hints2 = new DalHints();
		List<SqlServerPeopleTable> daoPojos2 = new ArrayList<SqlServerPeopleTable>(
				2);
		for (int i = 0; i < 2; i++) {
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Update_Shard_1" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos2.add(daoPojo);
		}
		hints2.inShard(1);
		int[] ret3 = dao.batchUpdate(hints2, daoPojos2);
		assertEquals(2, ret3.length);

		peopleID = 1l;
		SqlServerPeopleTable ret4 = dao.queryByPk(peopleID,
				new DalHints().inShard(1));
		assertEquals("Update_Shard_10", ret4.getName());

		peopleID = 2l;
		SqlServerPeopleTable ret5 = dao.queryByPk(peopleID,
				new DalHints().inShard(1));
		assertEquals("Update_Shard_11", ret5.getName());
	}

	@Test
	public void testtest_build_delete() throws Exception {

		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);

		int ret = dao.test_build_delete(CityID, new DalHints().inAllShards()
				.sequentialExecute());
		// assertEquals(-2, ret);
		Number peopleID = 1l;
		SqlServerPeopleTable ret1 = dao.queryByPk(peopleID,
				new DalHints().inShard(0));
		assertNull(ret1);

		peopleID = 2l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(0));
		assertNull(ret1);

		peopleID = 3l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(0));
		assertNotNull(ret1);

		peopleID = 1l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertNull(ret1);

		peopleID = 2l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertNull(ret1);

		peopleID = 3l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertNotNull(ret1);

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		CityID.add(22);
		ret = dao.test_build_delete(CityID, new DalHints().inShards(shards)
				.sequentialExecute());
		assertEquals(-2, ret);

		peopleID = 3l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(0));
		assertNull(ret1);

		peopleID = 3l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertNull(ret1);
	}

	@Test
	public void testtest_build_insert() throws Exception {
		Integer CityID = 24;// Test value here
		String Name = "Initial_Shard_4";// Test value here
		Integer ProvinceID = 34;// Test value here
		Integer CountryID = 44;// Test value here

		int ret = dao.test_build_insert(CityID, Name, ProvinceID, CountryID,
				new DalHints().inAllShards().sequentialExecute());
		Number peopleID = 4l;
		SqlServerPeopleTable ret1 = dao.queryByPk(peopleID,
				new DalHints().inShard(0));
		assertEquals("Initial_Shard_4", ret1.getName());

		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertEquals("Initial_Shard_4", ret1.getName());

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		CityID = 25;// Test value here
		Name = "Initial_Shard_5";// Test value here
		ProvinceID = 35;// Test value here
		CountryID = 45;// Test value here

		ret = dao.test_build_insert(CityID, Name, ProvinceID, CountryID,
				new DalHints().inShards(shards).sequentialExecute());
		peopleID = 5l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(0));
		assertEquals("Initial_Shard_5", ret1.getName());

		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertEquals("Initial_Shard_5", ret1.getName());
	}

	@Test
	public void testtest_build_update() throws Exception {
		// String Name = "";// Test value here
		// Integer CityID = null;// Test value here
		// int ret = dao.test_build_update(Name, CityID, new DalHints());
		String Name = "UpdateInAllShards";// Test value here
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		int ret = dao.test_build_update(Name, CityID, new DalHints()
				.inAllShards().sequentialExecute());
		// assertEquals(-2, ret);
		Number peopleID = 1l;
		SqlServerPeopleTable ret1 = dao.queryByPk(peopleID,
				new DalHints().inShard(0));
		assertEquals("UpdateInAllShards", ret1.getName());

		peopleID = 2l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(0));
		assertEquals("UpdateInAllShards", ret1.getName());

		peopleID = 3l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(0));
		assertEquals("Initial_Shard_02", ret1.getName());

		peopleID = 1l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertEquals("UpdateInAllShards", ret1.getName());

		peopleID = 2l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertEquals("UpdateInAllShards", ret1.getName());

		peopleID = 3l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertEquals("Initial_Shard_12", ret1.getName());

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		Name = "UpdateByShards";// Test value here
		CityID.add(22);// Test value here
		ret = dao.test_build_update(Name, CityID,
				new DalHints().inShards(shards).sequentialExecute());
		peopleID = 3l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(0));
		assertEquals("UpdateByShards", ret1.getName());

		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertEquals("UpdateByShards", ret1.getName());
	}

	@Test
	public void testtest_build_query_pojoFirst() throws Exception {
		// Integer CityID = null;// Test value here
		// PeopleSqlServerPeopleTable ret =
		// dao.test_build_query_pojoFirst(CityID, new DalHints());
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		SqlServerPeopleTable ret = dao.test_build_query_pojoFirst(
				CityID, new DalHints().inAllShards());
		assertNotNull(ret);

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		ret = dao.test_build_query_pojoFirst(CityID,
				new DalHints().inShards(shards));
		assertNotNull(ret);
	}

	@Test
	public void testtest_build_query_pojoList() throws Exception {
		// Integer CityID = null;// Test value here
		// List<PeopleSqlServerPeopleTable> ret =
		// dao.test_build_query_pojoList(CityID, new DalHints());

		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		List<SqlServerPeopleTable> ret = dao
				.test_build_query_pojoList(CityID, new DalHints().inAllShards());
		assertEquals(4, ret.size());

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		ret = dao.test_build_query_pojoList(CityID,
				new DalHints().inShards(shards));
		assertEquals(4, ret.size());
	}

	@Test
	public void testtest_build_query_pojoListByPage() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		List<SqlServerPeopleTable> ret = dao
				.test_build_query_pojoListByPage(CityID, 1, 2,
						new DalHints().inShard(0));
		assertEquals(2, ret.size());

		ret = dao.test_build_query_pojoListByPage(CityID, 1, 2,
				new DalHints().inShard(1));
		assertEquals(2, ret.size());
	}

	@Test
	public void testtest_build_query_pojoSingle() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(24);
		SqlServerPeopleTable ret = dao.test_build_query_pojoSingle(
				CityID, new DalHints().inShard(0));
		assertEquals("Initial_Shard_00", ret.getName());

		ret = dao
				.test_build_query_pojoSingle(CityID, new DalHints().inShard(1));
		assertEquals("Initial_Shard_10", ret.getName());
	}

	@Test
	public void testtest_build_query_fieldFirst() throws Exception {
		// Integer CityID = null;// Test value here
		// PeopleSqlServerPeopleTable ret =
		// dao.test_build_query_fieldFirst(CityID, new DalHints());

		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		String ret = dao.test_build_query_fieldFirst(CityID,
				new DalHints().inAllShards());
		assertNotNull(ret);
		assertEquals("Initial_Shard_00", ret);

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		ret = dao.test_build_query_fieldFirst(CityID,
				new DalHints().inShards(shards));
		assertEquals("Initial_Shard_00", ret);
	}

	@Test
	public void testtest_build_query_fieldList() throws Exception {
		// Integer CityID = null;// Test value here
		// List<String> ret = dao.test_build_query_fieldList(CityID, new
		// DalHints());
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		List<String> ret = dao.test_build_query_fieldList(CityID,
				new DalHints().inAllShards());
		assertEquals(4, ret.size());

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		ret = dao.test_build_query_fieldList(CityID,
				new DalHints().inShards(shards));
		assertEquals(4, ret.size());
	}

	@Test
	public void testtest_build_query_filedListByPage() throws Exception {
		// Integer CityID = null;// Test value here
		// List<String> ret = dao.test_build_query_filedListByPage(CityID, 1,
		// 10, new DalHints());
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		List<String> ret = dao.test_build_query_fieldListByPage(CityID, 1, 2,
				new DalHints().inShard(0));
		assertEquals(2, ret.size());

		ret = dao.test_build_query_fieldListByPage(CityID, 1, 2,
				new DalHints().inShard(1));
		assertEquals(2, ret.size());
	}

	@Test
	public void testtest_build_query_fieldSingle() throws Exception {
		// Integer CityID = null;// Test value here
		// String ret = dao.test_build_query_fieldSingle(CityID, new
		// DalHints());
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(24);
		String ret = dao.test_build_query_fieldSingle(CityID,
				new DalHints().inShard(0));
		assertEquals("Initial_Shard_00", ret);

		ret = dao.test_build_query_fieldSingle(CityID,
				new DalHints().inShard(1));
		assertEquals("Initial_Shard_10", ret);
	}

	@Test
	public void testtest_def_update() throws Exception {

		int ret = dao.test_def_update(new DalHints().inShard(0));
		Number peopleID = 1l;
		SqlServerPeopleTable ret1 = dao.queryByPk(peopleID,
				new DalHints().inShard(0));
		assertNull(ret1);

		peopleID = 2l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(0));
		assertNull(ret1);

		peopleID = 3l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(0));
		assertNull(ret1);

		peopleID = 1l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertEquals("Initial_Shard_10", ret1.getName());

		peopleID = 2l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertEquals("Initial_Shard_11", ret1.getName());

		peopleID = 3l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertEquals("Initial_Shard_12", ret1.getName());

		ret = dao.test_def_update(new DalHints().inShard(1));
		peopleID = 1l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertNull(ret1);

		peopleID = 2l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertNull(ret1);

		peopleID = 3l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertNull(ret1);
	}

	@Test
	public void testtest_def_query_pojoFirst() throws Exception {
		// Integer CityID = 1;// Test value here
		// Test_def_query_pojoPojo ret = dao.test_def_query_pojoFirst(CityID,
		// new DalHints());
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		SqlServerPeopleTable ret = dao.test_def_query_pojoFirst(
				CityID, new DalHints().inAllShards());
		assertNotNull(ret);

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		ret = dao.test_def_query_pojoFirst(CityID,
				new DalHints().inShards(shards));
		assertNotNull(ret);
	}

	@Test
	public void testtest_def_query_pojoList() throws Exception {
		// Integer CityID = 1;// Test value here
		// List<Test_def_query_pojoPojo> ret =
		// dao.test_def_query_pojoList(CityID, new DalHints());
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		List<SqlServerPeopleTable> ret = dao.test_def_query_pojoList(
				CityID, new DalHints().inAllShards());
		assertEquals(4, ret.size());

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		ret = dao.test_def_query_pojoList(CityID,
				new DalHints().inShards(shards));
		assertEquals(4, ret.size());
	}

	@Test
	public void testtest_def_query_pojoListByPage() throws Exception {
		// Integer CityID = 1;// Test value here
		// List<Test_def_query_pojoPojo> ret =
		// dao.test_def_query_pojoListByPage(CityID, 1, 10, new DalHints());
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		List<SqlServerPeopleTable> ret = dao
				.test_def_query_pojoListByPage(CityID, 1, 2,
						new DalHints().inShard(0));
		assertEquals(2, ret.size());

		ret = dao.test_def_query_pojoListByPage(CityID, 1, 2,
				new DalHints().inShard(1));
		assertEquals(2, ret.size());
	}

	@Test
	public void testtest_def_query_pojoSingle() throws Exception {
		// Integer CityID = 1;// Test value here
		// Test_def_query_pojoPojo ret = dao.test_def_query_pojoSingle(CityID,
		// new DalHints());
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(24);
		SqlServerPeopleTable ret = dao.test_def_query_pojoSingle(
				CityID, new DalHints().inShard(0));
		assertEquals("Initial_Shard_00", ret.getName());

		ret = dao.test_def_query_pojoSingle(CityID, new DalHints().inShard(1));
		assertEquals("Initial_Shard_10", ret.getName());
	}

	@Test
	public void testtest_def_query_fieldFirst() throws Exception {
		// Integer CityID = 1;// Test value here
		// String ret = dao.test_def_query_fieldFirst(CityID, new DalHints());
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		String ret = dao.test_def_query_fieldFirst(CityID,
				new DalHints().inAllShards());
		assertNotNull(ret);
		assertEquals("Initial_Shard_00", ret);

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		ret = dao.test_def_query_fieldFirst(CityID,
				new DalHints().inShards(shards));
		assertEquals("Initial_Shard_00", ret);
	}

	@Test
	public void testtest_def_query_fieldList() throws Exception {
		// Integer CityID = 1;// Test value here
		// List<String> ret = dao.test_def_query_fieldList(CityID, new
		// DalHints());
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		List<String> ret = dao.test_def_query_fieldList(CityID,
				new DalHints().inAllShards());
		assertEquals(4, ret.size());

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		ret = dao.test_def_query_fieldList(CityID,
				new DalHints().inShards(shards));
		assertEquals(4, ret.size());
	}

	@Test
	public void testtest_def_query_fieldListByPage() throws Exception {
		// Integer CityID = 1;// Test value here
		// List<String> ret = dao.test_def_query_fieldListByPage(CityID, 1, 10,
		// new DalHints());
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		List<String> ret = dao.test_def_query_fieldListByPage(CityID, 1, 2,
				new DalHints().inShard(0));
		assertEquals(2, ret.size());

		ret = dao.test_def_query_fieldListByPage(CityID, 1, 2,
				new DalHints().inShard(1));
		assertEquals(2, ret.size());
	}

	@Test
	public void testtest_def_query_fieldSingle() throws Exception {
		// Integer CityID = 1;// Test value here
		// String ret = dao.test_def_query_fieldSingle(CityID, new DalHints());
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(24);
		String ret = dao.test_def_query_fieldSingle(CityID,
				new DalHints().inShard(0));
		assertEquals("Initial_Shard_00", ret);

		ret = dao.test_def_query_fieldSingle(CityID, new DalHints().inShard(1));
		assertEquals("Initial_Shard_10", ret);
	}

	@Test
	public void testtest_build_delete_equal() throws Exception {
		Integer CityID = 20;// Test value here
		dao.test_build_delete_equal(CityID, new DalHints().inAllShards()
				.sequentialExecute());
		// assertEquals(-2, ret);
		int count = dao.count(new DalHints().inShard(0));
		assertEquals(2, count);
		count = dao.count(new DalHints().inShard(1));
		assertEquals(2, count);

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		CityID = 21;
		dao.test_build_delete_equal(CityID, new DalHints().inShards(shards)
				.sequentialExecute());
		// assertEquals(-2, ret);
		count = dao.count(new DalHints().inShard(0));
		assertEquals(1, count);
		count = dao.count(new DalHints().inShard(1));
		assertEquals(1, count);
	}

	@Test
	public void testtest_build_insert_equal() throws Exception {
		Integer CityID = 20;// Test value here
		String Name = "InsertInAllShards";
		int ProvinceID = 50;
		int CountryID = 50;
		int ret = dao.test_build_insert_equal(CityID, Name, ProvinceID,
				CountryID, new DalHints().inAllShards().sequentialExecute());
		// assertEquals(2, ret.size());
		int count = dao.count(new DalHints().inShard(0));
		assertEquals(4, count);
		count = dao.count(new DalHints().inShard(1));
		assertEquals(4, count);

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		Integer CityID1 = 21;// Test value here
		String Name1 = "InsertInShards";
		int ProvinceID1 = 51;
		int CountryID1 = 51;
		ret = dao
				.test_build_insert_equal(CityID1, Name1, ProvinceID1,
						CountryID1, new DalHints().inShards(shards)
								.sequentialExecute());
		// assertEquals(2, ret.size());
		count = dao.count(new DalHints().inShard(0));
		assertEquals(5, count);
		count = dao.count(new DalHints().inShard(1));
		assertEquals(5, count);
	}

	@Test
	public void testtest_build_update_equal() throws Exception {
		String Name = "UpdateInAllShards";// Test value here
		Integer CityID = 20;// Test value here
		int ret = dao.test_build_update_equal(Name, CityID, new DalHints()
				.inAllShards().sequentialExecute());
		// assertEquals(-2, ret);
		SqlServerPeopleTable pojo = dao.queryByPk(1,
				new DalHints().inShard(0));
		assertEquals("UpdateInAllShards", pojo.getName());

		pojo = dao.queryByPk(1, new DalHints().inShard(1));
		assertEquals("UpdateInAllShards", pojo.getName());

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		Name = "UpdateByShards";// Test value here
		CityID = 22;// Test value here
		ret = dao.test_build_update_equal(Name, CityID, new DalHints()
				.inShards(shards).sequentialExecute());
		// assertEquals(-2, ret);
		pojo = dao.queryByPk(3, new DalHints().inShard(0));
		assertEquals("UpdateByShards", pojo.getName());

		pojo = dao.queryByPk(3, new DalHints().inShard(1));
		assertEquals("UpdateByShards", pojo.getName());
	}

	@Test
	public void testtest_build_query_equal() throws Exception {
		Integer CityID = 20;// Test value here
		List<SqlServerPeopleTable> ret = dao.test_build_query_equal(
				CityID, new DalHints().inAllShards().sequentialExecute());
		assertEquals(2, ret.size());

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		CityID = 21;// Test value here
		ret = dao.test_build_query_equal(CityID, new DalHints()
				.inShards(shards).sequentialExecute());
		assertEquals(2, ret.size());
	}

	// @Test
	// public void testtest_def_update_equal() throws Exception {
	// int ret1 = dao.test_def_update_equal(new DalHints().inShard(0));
	// assertEquals(-1,ret1);
	// int ret2 = dao.test_def_update_equal(new DalHints().inShard(1));
	// assertEquals(-1,ret2);
	// }

	@Test
	public void testtest_def_query_equal() throws Exception {
		DalHints hints1 = new DalHints();
		// hints1.inShard(0);
		Integer CityID = 20;
		List<SqlServerPeopleTable> ret1 = dao.test_def_query_equal(
				CityID, hints1.inShard(0));
		assertEquals(1, ret1.size());

		DalHints hints2 = new DalHints();
		// hints2.inShard(1);
		CityID = 22;
		List<SqlServerPeopleTable> ret2 = dao.test_def_query_equal(
				CityID, hints2.inShard(1));
		assertEquals(1, ret2.size());
	}

	@Test
	public void testtest_def_query_page() throws Exception {
		DalHints hints1 = new DalHints();
		hints1.inShard(0);
		int pageNo = 1;
		int pageSize = 2;
		Integer CityID = 19;
		List<SqlServerPeopleTable> ret1 = dao.def_query_page(CityID,
				pageNo, pageSize, hints1);
		assertEquals(2, ret1.size());

	}
}
