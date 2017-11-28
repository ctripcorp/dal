package shardTest.oldVersionCodeTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.*;



import static org.junit.Assert.*;

import com.ctrip.platform.dal.dao.*;

/**
 * JUnit test of PeopleSimpleShardByDBOnSqlServerGenDao class. Before run the
 * unit test, you should initiate the test data and change all the asserts
 * correspond to you case.
 **/
public class ShardColModShardByDBOnSqlServerGenDaoUnitTest {

	private static final String DATA_BASE = "ShardColModShardByDBOnSqlServer";

	private static DalClient client = null;
	private static ShardColModShardByDBOnSqlServerGenDao dao = null;

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
		dao = new ShardColModShardByDBOnSqlServerGenDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		dao.test_def_update(new DalHints().inShard(0));
		dao.test_def_update(new DalHints().inShard(1));

		List<ShardColModShardByDBOnSqlServerGen> daoPojos1 = new ArrayList<ShardColModShardByDBOnSqlServerGen>(
				3);
		for (int i = 0; i < 6; i++) {
			ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
			if (i % 2 == 0)
				daoPojo.setName("Initial_Shard_0" + i);
			else
				daoPojo.setName("Initial_Shard_1" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos1.add(daoPojo);
		}
		dao.insert(new DalHints(), daoPojos1);
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

		ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
		daoPojo.setPeopleID(1l);
		int ret = dao.delete(new DalHints().inShard(0), daoPojo);
		// assertEquals(1, ret);

		Number id = 1l;
		ShardColModShardByDBOnSqlServerGen ret1 = dao.queryByPk(id,
				new DalHints().inShard(0));
		assertNull(ret1);

		ret = dao.delete(new DalHints().inShard(1), daoPojo);
		// assertEquals(1, ret);
		ShardColModShardByDBOnSqlServerGen ret2 = dao.queryByPk(id,
				new DalHints().inShard(1));
		assertNull(ret2);

	}

	@Test
	public void testDelete2() throws Exception {
		List<ShardColModShardByDBOnSqlServerGen> daoPojos1 = new ArrayList<ShardColModShardByDBOnSqlServerGen>(
				3);
		for (int i = 0; i < 3; i++) {
			ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Initial_Shard_0" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.delete(new DalHints().inShard(0), daoPojos1);
		assertEquals(3, affected.length);

		List<ShardColModShardByDBOnSqlServerGen> daoPojos2 = new ArrayList<ShardColModShardByDBOnSqlServerGen>(
				3);
		for (int i = 0; i < 3; i++) {
			ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
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
		List<ShardColModShardByDBOnSqlServerGen> daoPojos1 = new ArrayList<ShardColModShardByDBOnSqlServerGen>(
				3);
		for (int i = 0; i < 3; i++) {
			ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Initial_Shard_0" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.batchDelete(new DalHints().inShard(0), daoPojos1);
		assertEquals(3, affected.length);

		List<ShardColModShardByDBOnSqlServerGen> daoPojos2 = new ArrayList<ShardColModShardByDBOnSqlServerGen>(
				3);
		for (int i = 0; i < 3; i++) {
			ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Initial_Shard_1" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos2.add(daoPojo);
		}
		affected = dao.batchDelete(new DalHints().inShard(1), daoPojos2);
		assertEquals(3, affected.length);

		affected=dao.batchDelete(null,daoPojos1);
		assertEquals(3,affected.length);
	}

	@Test
	public void testGetAll() throws Exception {
		List<ShardColModShardByDBOnSqlServerGen> list = dao
				.getAll(new DalHints().inShard(0));
		assertEquals(3, list.size());

		list = dao.getAll(new DalHints().inShard(1));
		assertEquals(3, list.size());

		list = dao.getAll(new DalHints().inAllShards());
		assertEquals(6, list.size());
	}

	@Test
	public void testInsert1() throws Exception {
		DalHints hints = new DalHints();
		ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
		daoPojo.setPeopleID(4l);
		daoPojo.setName("Initial_Shard_14");
		daoPojo.setCityID(24);
		daoPojo.setProvinceID(34);
		daoPojo.setCountryID(44);

		int affected = dao.insert(new DalHints().inShard(0), daoPojo);
		Number id = 4l;
		ShardColModShardByDBOnSqlServerGen ret1 = dao.queryByPk(id,
				new DalHints().inShard(0));
		assertNotNull(ret1);

		affected = dao.insert(new DalHints().inShard(1), daoPojo);

		ShardColModShardByDBOnSqlServerGen ret2 = dao.queryByPk(id,
				new DalHints().inShard(1));
		assertNotNull(ret2);

	}

	@Test
	public void testInsert2() throws Exception {
		List<ShardColModShardByDBOnSqlServerGen> daoPojos1 = new ArrayList<ShardColModShardByDBOnSqlServerGen>(
				3);
		for (int i = 0; i < 3; i++) {
			ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Initial_Shard_0" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.insert(new DalHints().inShard(0), daoPojos1);
		assertEquals(3, affected.length);

		List<ShardColModShardByDBOnSqlServerGen> daoPojos2 = new ArrayList<ShardColModShardByDBOnSqlServerGen>(
				3);
		for (int i = 0; i < 3; i++) {
			ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
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

		ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
		// daoPojo.setPeopleID(4l);
		daoPojo.setName("Initial_Shard_14");
		daoPojo.setCityID(24);
		daoPojo.setProvinceID(34);
		daoPojo.setCountryID(44);

		int affected = dao.insert(new DalHints().inShard(0), keyHolder1,
				daoPojo);
		assertEquals(4l, keyHolder1.getKey());
		Number id = 4l;
		ShardColModShardByDBOnSqlServerGen ret1 = dao.queryByPk(id,
				new DalHints().inShard(0));
		assertNotNull(ret1);

		KeyHolder keyHolder2 = new KeyHolder();
		affected = dao.insert(new DalHints().inShard(1), keyHolder2, daoPojo);
		assertEquals(4l, keyHolder2.getKey());
		ShardColModShardByDBOnSqlServerGen ret2 = dao.queryByPk(id,
				new DalHints().inShard(1));
		assertNotNull(ret2);
	}

	@Test
	public void testInsert4() throws Exception {

		KeyHolder keyHolder1 = new KeyHolder();
		List<ShardColModShardByDBOnSqlServerGen> daoPojos1 = new ArrayList<ShardColModShardByDBOnSqlServerGen>(
				3);
		for (int i = 0; i < 3; i++) {
			ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
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
		List<ShardColModShardByDBOnSqlServerGen> daoPojos2 = new ArrayList<ShardColModShardByDBOnSqlServerGen>(
				3);
		for (int i = 0; i < 3; i++) {
			ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
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

		List<ShardColModShardByDBOnSqlServerGen> daoPojos1 = new ArrayList<ShardColModShardByDBOnSqlServerGen>(
				3);
		for (int i = 0; i < 3; i++) {
			ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
			daoPojo.setPeopleID(Long.valueOf(i)+4);
			daoPojo.setName("Initial_Shard_0" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos1.add(daoPojo);
		}
		int[] affected = dao.batchInsert(new DalHints().inShard(0), daoPojos1);
		assertEquals(3, affected.length);

		List<ShardColModShardByDBOnSqlServerGen> daoPojos2 = new ArrayList<ShardColModShardByDBOnSqlServerGen>(
				3);
		for (int i = 0; i < 3; i++) {
			ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
			daoPojo.setPeopleID(Long.valueOf(i)+5);
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
		List<ShardColModShardByDBOnSqlServerGen> list = dao.queryByPage(
				pageSize, pageNo, new DalHints().inShard(0));
		assertEquals(2, list.size());

		list = dao.queryByPage(pageSize, pageNo, new DalHints().inShard(1));
		assertEquals(2, list.size());
	}

	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 1l;
		ShardColModShardByDBOnSqlServerGen ret = dao.queryByPk(id,
				new DalHints().inShard(0));
		assertEquals("Initial_Shard_00", ret.getName());

		ret = dao.queryByPk(id, new DalHints().inShard(1));
		assertEquals("Initial_Shard_11", ret.getName());
	}

	@Test
	public void testQueryByPk2() throws Exception {
		ShardColModShardByDBOnSqlServerGen pk = new ShardColModShardByDBOnSqlServerGen();
		pk.setPeopleID(1l);
		ShardColModShardByDBOnSqlServerGen ret = dao.queryByPk(pk,
				new DalHints().inShard(0));
		assertEquals("Initial_Shard_00", ret.getName());

		ret = dao.queryByPk(pk, new DalHints().inShard(1));
		assertEquals("Initial_Shard_11", ret.getName());
	}

	@Test
	public void testUpdate1() throws Exception {
		DalHints hints = new DalHints();
		ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
		daoPojo.setPeopleID(1l);
		daoPojo.setName("updateShard00");
		int ret = dao.update(new DalHints().inShard(0), daoPojo);
		ShardColModShardByDBOnSqlServerGen daoPojo1 = dao.queryByPk(1l,
				new DalHints().inShard(0));
		assertEquals("updateShard00", daoPojo1.getName());
		assertEquals("20", daoPojo1.getCityID().toString());

		daoPojo.setName("updateShard10");
		ret = dao.update(new DalHints().inShard(1), daoPojo);
		ShardColModShardByDBOnSqlServerGen daoPojo2 = dao.queryByPk(1l,
				new DalHints().inShard(1));
		assertEquals("updateShard10", daoPojo2.getName());

	}

	@Test
	public void testUpdate2() throws Exception {
		DalHints hints1 = new DalHints();
		List<ShardColModShardByDBOnSqlServerGen> daoPojos1 = new ArrayList<ShardColModShardByDBOnSqlServerGen>(
				2);
		for (int i = 0; i < 2; i++) {
			ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
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
		ShardColModShardByDBOnSqlServerGen ret1 = dao.queryByPk(peopleID,
				new DalHints().inShard(0));
		assertEquals("Update_Shard_00", ret1.getName());

		peopleID = 2l;
		ShardColModShardByDBOnSqlServerGen ret2 = dao.queryByPk(peopleID,
				new DalHints().inShard(0));
		assertEquals("Update_Shard_01", ret2.getName());

		DalHints hints2 = new DalHints();
		List<ShardColModShardByDBOnSqlServerGen> daoPojos2 = new ArrayList<ShardColModShardByDBOnSqlServerGen>(
				2);
		for (int i = 0; i < 2; i++) {
			ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
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
		ShardColModShardByDBOnSqlServerGen ret4 = dao.queryByPk(peopleID,
				new DalHints().inShard(1));
		assertEquals("Update_Shard_10", ret4.getName());

		peopleID = 2l;
		ShardColModShardByDBOnSqlServerGen ret5 = dao.queryByPk(peopleID,
				new DalHints().inShard(1));
		assertEquals("Update_Shard_11", ret5.getName());
	}

	@Test
	public void testBatchUpdate() throws Exception {
		DalHints hints1 = new DalHints();
		List<ShardColModShardByDBOnSqlServerGen> daoPojos1 = new ArrayList<ShardColModShardByDBOnSqlServerGen>(
				2);
		for (int i = 0; i < 2; i++) {
			ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
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
		ShardColModShardByDBOnSqlServerGen ret1 = dao.queryByPk(peopleID,
				new DalHints().inShard(0));
		assertEquals("Update_Shard_00", ret1.getName());

		peopleID = 2l;
		ShardColModShardByDBOnSqlServerGen ret2 = dao.queryByPk(peopleID,
				new DalHints().inShard(0));
		assertEquals("Update_Shard_01", ret2.getName());

		DalHints hints2 = new DalHints();
		List<ShardColModShardByDBOnSqlServerGen> daoPojos2 = new ArrayList<ShardColModShardByDBOnSqlServerGen>(
				2);
		for (int i = 0; i < 2; i++) {
			ShardColModShardByDBOnSqlServerGen daoPojo = new ShardColModShardByDBOnSqlServerGen();
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
		ShardColModShardByDBOnSqlServerGen ret4 = dao.queryByPk(peopleID,
				new DalHints().inShard(1));
		assertEquals("Update_Shard_10", ret4.getName());

		peopleID = 2l;
		ShardColModShardByDBOnSqlServerGen ret5 = dao.queryByPk(peopleID,
				new DalHints().inShard(1));
		assertEquals("Update_Shard_11", ret5.getName());


		daoPojos1.get(0).setPeopleID(100L);
		daoPojos1.get(1).setPeopleID(101L);
		ret3=dao.batchUpdate(null,daoPojos1);
		assertEquals(2, ret3.length);
	}

	@Test
	public void testtest_build_delete() throws Exception {

		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);

		int ret = dao.test_build_delete(CityID, new DalHints().inAllShards());
		// assertEquals(-2, ret);
		Number peopleID = 1l;
		ShardColModShardByDBOnSqlServerGen ret1 = dao.queryByPk(peopleID,
				new DalHints().inShard(0));
		assertNull(ret1);

		peopleID = 2l;
		ret1 = dao.queryByPk(peopleID,
				new DalHints().setShardColValue("CityID", 20));
		assertNotNull(ret1);

		peopleID = 1l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertNull(ret1);

		peopleID = 2l;
		ret1 = dao.queryByPk(peopleID, new DalHints().setShardValue(3));
		assertNotNull(ret1);

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		CityID.add(22);
		ret = dao.test_build_delete(CityID, new DalHints().inShards(shards));
		// assertEquals(-2, ret);

		peopleID = 2l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(0));
		assertNull(ret1);

		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertNotNull(ret1);
	}

	@Test
	public void testtest_build_insert() throws Exception {
		Integer CityID = 26;// Test value here
		String Name = "Initial_Shard_06";// Test value here
		Integer ProvinceID = 36;// Test value here
		Integer CountryID = 46;// Test value here

		int ret = dao.test_build_insert(CityID, Name, ProvinceID, CountryID,
				new DalHints().setShardColValue("CityID", 26));

		Number peopleID = 4l;
		ShardColModShardByDBOnSqlServerGen ret1 = dao.queryByPk(peopleID,
				new DalHints().setShardColValue("CityID", 26));
		assertEquals("Initial_Shard_06", ret1.getName());

		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertNull(ret1);

		CityID = 27;// Test value here
		Name = "Initial_Shard_17";// Test value here
		ProvinceID = 35;// Test value here
		CountryID = 45;// Test value here

		ret = dao.test_build_insert(CityID, Name, ProvinceID, CountryID,
				new DalHints());
		peopleID = 4l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(0));
		assertEquals("Initial_Shard_06", ret1.getName());

		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertEquals("Initial_Shard_17", ret1.getName());
	}

	@Test
	public void testtest_build_update() throws Exception {
		String Name = "UpdateInAllShards";// Test value here
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		int ret = dao.test_build_update(Name, CityID,
				new DalHints().inAllShards());
		// assertEquals(-2, ret);
		Number peopleID = 1l;
		ShardColModShardByDBOnSqlServerGen ret1 = dao.queryByPk(peopleID,
				new DalHints().setShardColValue("CityID", 20));
		assertEquals("UpdateInAllShards", ret1.getName());

		peopleID = 1l;
		ret1 = dao.queryByPk(peopleID,
				new DalHints().setShardColValue("CityID", 21));
		assertEquals("UpdateInAllShards", ret1.getName());

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		Name = "UpdateByShards";// Test value here
		CityID.add(22);// Test value here
		ret = dao.test_build_update(Name, CityID,
				new DalHints().inShards(shards));
		peopleID = 2l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(0));
		assertEquals("UpdateByShards", ret1.getName());

		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertEquals("Initial_Shard_13", ret1.getName());
	}

	@Test
	public void testtest_ClientQueryFrom_list() throws Exception {
		//Integer CityID = null;// Test value here
		//List<ignoreMissingFieldsAndAllowPartialTestOnSqlServer> ret = dao.test_build_query_list(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<ShardColModShardByDBOnSqlServerGen> ret = dao.test_ClientQueryFrom_list(CityID, new DalHints().inAllShards(),1,1);
		assertEquals(1, ret.size());
		assertEquals(22, ret.get(0).getCityID().intValue());
		assertEquals(2, ret.get(0).getPeopleID().intValue());

		ret = dao.test_ClientQueryFrom_list(CityID, new DalHints().setShardColValue("CityID", 20),1,1);
		assertEquals(1, ret.size());
		assertEquals(22, ret.get(0).getCityID().intValue());
		assertEquals(2, ret.get(0).getPeopleID().intValue());
	}
	@Test
	public void testtest_ClientQueryFromPartialFieldsSet_list() throws Exception {
		//Integer CityID = null;// Test value here
		//List<ignoreMissingFieldsAndAllowPartialTestOnSqlServer> ret = dao.test_build_query_list(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<ShardColModShardByDBOnSqlServerGen> ret = dao.test_ClientQueryFromPartialFieldsSet_list(CityID, new DalHints().inAllShards(),1,1);
		assertEquals(1, ret.size());
		assertEquals(22, ret.get(0).getCityID().intValue());
		assertNull(ret.get(0).getPeopleID());

		ret = dao.test_ClientQueryFromPartialFieldsSet_list(CityID, new DalHints().setShardColValue("CityID", 20),1,1);
		assertEquals(1, ret.size());
		assertEquals(22, ret.get(0).getCityID().intValue());
		assertNull(ret.get(0).getPeopleID());
	}
	@Test
	public void testtest_ClientQueryFromPartialFieldsStrings_list() throws Exception {
		//Integer CityID = null;// Test value here
		//List<ignoreMissingFieldsAndAllowPartialTestOnSqlServer> ret = dao.test_build_query_list(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<ShardColModShardByDBOnSqlServerGen> ret = dao.test_ClientQueryFromPartialFieldsStrings_list(CityID, new DalHints().inAllShards(),1,1);
		assertEquals(1, ret.size());
		assertEquals(22, ret.get(0).getCityID().intValue());
		assertNull(ret.get(0).getPeopleID());

		ret = dao.test_ClientQueryFromPartialFieldsStrings_list(CityID, new DalHints().setShardColValue("CityID", 20),1,1);
		assertEquals(1, ret.size());
		assertEquals(22, ret.get(0).getCityID().intValue());
		assertNull(ret.get(0).getPeopleID());
	}

	@Test
	public void testtest_build_queryFrom_pojoList() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		List<ShardColModShardByDBOnSqlServerGen> ret = dao
				.test_build_queryFrom_pojoList(CityID, new DalHints().inAllShards(),0,3);
		assertEquals(3, ret.size());
		assertEquals(20, ret.get(0).getCityID().intValue());
		assertEquals(21, ret.get(1).getCityID().intValue());
		assertEquals(22, ret.get(2).getCityID().intValue());

		ret = dao.test_build_queryFrom_pojoList(CityID,
				new DalHints().setShardColValue("CityID", 20),0,1);
		assertEquals(1, ret.size());
		assertEquals(20, ret.get(0).getCityID().intValue());
	}

	@Test
	public void testtest_build_queryFromPartialFieldsStrings_pojoList() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		List<ShardColModShardByDBOnSqlServerGen> ret = dao
				.test_build_queryFromPartialFieldsStrings_pojoList(CityID, new DalHints().inAllShards(),0,3);
		assertEquals(3, ret.size());
		assertEquals(20, ret.get(0).getCityID().intValue());
		assertEquals(1,ret.get(0).getPeopleID().intValue());
		assertNull(ret.get(0).getCountryID());
		assertNull(ret.get(0).getProvinceID());
		assertNull(ret.get(0).getName());
		assertEquals(21, ret.get(1).getCityID().intValue());
		assertEquals(1,ret.get(1).getPeopleID().intValue());
		assertNull(ret.get(1).getCountryID());
		assertNull(ret.get(1).getProvinceID());
		assertNull(ret.get(1).getName());
		assertEquals(22, ret.get(2).getCityID().intValue());
		assertEquals(2,ret.get(2).getPeopleID().intValue());
		assertNull(ret.get(2).getCountryID());
		assertNull(ret.get(2).getProvinceID());
		assertNull(ret.get(2).getName());

		ret = dao.test_build_queryFromPartialFieldsStrings_pojoList(CityID,
				new DalHints().setShardColValue("CityID", 20),0,1);
		assertEquals(1, ret.size());
		assertEquals(20, ret.get(0).getCityID().intValue());
		assertEquals(1,ret.get(0).getPeopleID().intValue());
		assertNull(ret.get(0).getCountryID());
		assertNull(ret.get(0).getProvinceID());
		assertNull(ret.get(0).getName());
	}

	@Test
	public void testtest_build_queryFromPartialFieldsSet_pojoList() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		List<ShardColModShardByDBOnSqlServerGen> ret = dao
				.test_build_queryFromPartialFieldsSet_pojoList(CityID, new DalHints().inAllShards(),0,3);
		assertEquals(3, ret.size());
		assertEquals(20, ret.get(0).getCityID().intValue());
		assertEquals(1,ret.get(0).getPeopleID().intValue());
		assertEquals(21, ret.get(1).getCityID().intValue());
		assertEquals(1,ret.get(1).getPeopleID().intValue());
		assertEquals(22, ret.get(2).getCityID().intValue());
		assertEquals(2,ret.get(2).getPeopleID().intValue());

		ret = dao.test_build_queryFromPartialFieldsSet_pojoList(CityID,
				new DalHints().setShardColValue("CityID", 20),0,1);
		assertEquals(1, ret.size());
		assertEquals(20, ret.get(0).getCityID().intValue());
		assertEquals(1,ret.get(0).getPeopleID().intValue());
	}

	@Test
	public void testtest_build_query_pojoFirst() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		ShardColModShardByDBOnSqlServerGen ret = dao
				.test_build_query_pojoFirst(CityID,
						new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret.getName());

		ret = dao.test_build_query_pojoFirst(CityID,
				new DalHints().setShardValue(2));
		assertEquals("Initial_Shard_00", ret.getName());
	}

	@Test
	public void testtest_build_query_pojoList() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		List<ShardColModShardByDBOnSqlServerGen> ret = dao
				.test_build_query_pojoList(CityID, new DalHints().inAllShards());
		assertEquals(3, ret.size());
		// for(int i=0;i<ret.size();i++)
		// System.out.println(ret.get(i).getName());

		ret = dao.test_build_query_pojoList(CityID,
				new DalHints().setShardColValue("CityID", 21));
		assertEquals(1, ret.size());
	}

	@Test
	public void test_build_query_pojoListByPage() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(5);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);
		CityID.add(24);
		CityID.add(23);

		List<ShardColModShardByDBOnSqlServerGen> ret = dao
				.test_build_query_pojoListByPage(CityID, 1, 10,
						new DalHints().setShardColValue("CityID", 22));
		// for(int i=0;i<ret.size();i++)
		// System.out.println(ret.get(i).getName());

		assertEquals(3, ret.size());
		ret = dao.test_build_query_pojoListByPage(CityID, 2, 2,
				new DalHints().inShard(1));
		assertEquals(0, ret.size());
	}

	@Test
	public void testtest_build_query_pojoSingle() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		ShardColModShardByDBOnSqlServerGen ret = dao
				.test_build_query_pojoSingle(CityID,
						new DalHints().setShardColValue("CityID", 24));
		assertEquals("Initial_Shard_00", ret.getName());

		ret = dao.test_build_query_pojoSingle(CityID,
				new DalHints().setShardColValue("CityID", 25));
		assertEquals("Initial_Shard_11", ret.getName());
	}

	@Test
	public void testtest_build_query_fieldFirst() throws Exception {

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
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		List<String> ret = dao.test_build_query_fieldList(CityID,
				new DalHints().inAllShards());
		assertEquals(2, ret.size());

		ret = dao.test_build_query_fieldList(CityID,
				new DalHints().setShardColValue("CityID", 21));
		assertEquals(1, ret.size());
	}

	@Test
	public void testtest_build_query_filedListByPage() throws Exception {

		List<Integer> CityID = new ArrayList<Integer>(5);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);
		CityID.add(24);
		CityID.add(23);

		List<String> ret = dao.test_build_query_fieldListByPage(CityID, 1, 10,
				new DalHints().setShardColValue("CityID", 22));
		// for(int i=0;i<ret.size();i++)
		// System.out.println(ret.get(i).getName());

		assertEquals(3, ret.size());
		ret = dao.test_build_query_fieldListByPage(CityID, 2, 2,
				new DalHints().inShard(1));
		assertEquals(0, ret.size());
	}

	@Test
	public void testtest_build_query_fieldSingle() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		String ret = dao.test_build_query_fieldSingle(CityID,
				new DalHints().setShardColValue("CityID", 24));
		assertEquals("Initial_Shard_00", ret);

		ret = dao.test_build_query_fieldSingle(CityID,
				new DalHints().setShardColValue("CityID", 25));
		assertEquals("Initial_Shard_11", ret);
	}

	@Test
	public void testtest_def_update() throws Exception {

		int ret = dao.test_def_update(new DalHints().setShardColValue("CityID",
				20));
		Number peopleID = 1l;
		ShardColModShardByDBOnSqlServerGen ret1 = dao.queryByPk(peopleID,
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
		assertEquals("Initial_Shard_11", ret1.getName());

		peopleID = 2l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertEquals("Initial_Shard_13", ret1.getName());

		peopleID = 3l;
		ret1 = dao.queryByPk(peopleID, new DalHints().inShard(1));
		assertEquals("Initial_Shard_15", ret1.getName());

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
	public void testtest_def_queryFrom_pojoList() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		List<ShardColModShardByDBOnSqlServerGen> ret = dao
				.test_def_queryFrom_pojoList(CityID, new DalHints().inAllShards(),1,1);
		assertEquals(1, ret.size());
		assertEquals(21, ret.get(0).getCityID().intValue());
		assertEquals(1, ret.get(0).getPeopleID().intValue());

		ret = dao.test_def_queryFrom_pojoList(CityID,
				new DalHints().setShardColValue("CityID", 21),1,1);
		assertEquals(0, ret.size());
	}

	@Test
	public void testtest_def_queryFromPartialFieldsSet_pojoList() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		List<ShardColModShardByDBOnSqlServerGen> ret = dao
				.test_def_queryFromPartialFieldsSet_pojoList(CityID, new DalHints().inAllShards(),1,1);
		assertEquals(1, ret.size());
		assertEquals(21, ret.get(0).getCityID().intValue());
		assertEquals(1, ret.get(0).getPeopleID().intValue());
		assertNull(ret.get(0).getName());

		ret = dao.test_def_queryFromPartialFieldsSet_pojoList(CityID,
				new DalHints().setShardColValue("CityID", 21),0,1);
		assertEquals(1, ret.size());
		assertEquals(21, ret.get(0).getCityID().intValue());
		assertEquals(1, ret.get(0).getPeopleID().intValue());
		assertNull(ret.get(0).getName());
	}

	@Test
	public void testtest_def_queryFromPartialFieldsStrings_pojoList() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		List<ShardColModShardByDBOnSqlServerGen> ret = dao
				.test_def_queryFromPartialFieldsStrings_pojoList(CityID, new DalHints().inAllShards(),1,1);
		assertEquals(1, ret.size());
		assertEquals(21, ret.get(0).getCityID().intValue());
		assertEquals(1, ret.get(0).getPeopleID().intValue());
		assertNull(ret.get(0).getName());

		ret = dao.test_def_queryFromPartialFieldsStrings_pojoList(CityID,
				new DalHints().setShardColValue("CityID", 21),0,1);
		assertEquals(1, ret.size());
		assertEquals(21, ret.get(0).getCityID().intValue());
		assertEquals(1, ret.get(0).getPeopleID().intValue());
		assertNull(ret.get(0).getName());
	}

	@Test
	public void testtest_def_query_pojoFirst() throws Exception {
		// Integer CityID = 1;// Test value here
		// Test_def_query_pojoPojo ret = dao.test_def_query_pojoFirst(CityID,
		// new DalHints());
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		ShardColModShardByDBOnSqlServerGen ret = dao
				.test_def_query_pojoFirst(CityID, new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret.getName());

		ret = dao.test_def_query_pojoFirst(CityID,
				new DalHints().setShardValue(2));
		assertEquals("Initial_Shard_00", ret.getName());
	}

	@Test
	public void testtest_def_query_pojoList() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		List<ShardColModShardByDBOnSqlServerGen> ret = dao
				.test_def_query_pojoList(CityID, new DalHints().inAllShards());
		assertEquals(3, ret.size());
		// for(int i=0;i<ret.size();i++)
		// System.out.println(ret.get(i).getName());

		ret = dao.test_def_query_pojoList(CityID,
				new DalHints().setShardColValue("CityID", 21));
		assertEquals(1, ret.size());
	}

	@Test
	public void testtest_def_query_pojoListByPage() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(5);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);
		CityID.add(24);
		CityID.add(23);

		List<ShardColModShardByDBOnSqlServerGen> ret = dao
				.test_def_query_pojoListByPage(CityID, 1, 10,
						new DalHints().setShardColValue("CityID", 22));
		// for(int i=0;i<ret.size();i++)
		// System.out.println(ret.get(i).getName());

		assertEquals(3, ret.size());
		ret = dao.test_def_query_pojoListByPage(CityID, 2, 2,
				new DalHints().inShard(1));
		assertEquals(0, ret.size());
	}

	@Test
	public void testtest_def_query_pojoSingle() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		ShardColModShardByDBOnSqlServerGen ret = dao
				.test_def_query_pojoSingle(CityID,
						new DalHints().setShardColValue("CityID", 24));
		assertEquals("Initial_Shard_00", ret.getName());

		ret = dao.test_def_query_pojoSingle(CityID,
				new DalHints().setShardColValue("CityID", 25));
		assertEquals("Initial_Shard_11", ret.getName());
	}

	@Test
	public void testtest_def_query_fieldFirst() throws Exception {
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
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		List<String> ret = dao.test_def_query_fieldList(CityID,
				new DalHints().inAllShards());
		assertEquals(2, ret.size());

		ret = dao.test_def_query_fieldList(CityID,
				new DalHints().setShardColValue("CityID", 21));
		assertEquals(1, ret.size());
	}

	@Test
	public void testtest_def_query_fieldListByPage() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(5);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);
		CityID.add(24);
		CityID.add(23);

		List<String> ret = dao.test_def_query_fieldListByPage(CityID, 1, 10,
				new DalHints().setShardColValue("CityID", 22));
		// for(int i=0;i<ret.size();i++)
		// System.out.println(ret.get(i).getName());

		assertEquals(3, ret.size());
		ret = dao.test_def_query_fieldListByPage(CityID, 2, 2,
				new DalHints().inShard(1));
		assertEquals(0, ret.size());
	}

	@Test
	public void testtest_def_query_fieldSingle() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		String ret = dao.test_def_query_fieldSingle(CityID,
				new DalHints().setShardColValue("CityID", 24));
		assertEquals("Initial_Shard_00", ret);

		ret = dao.test_def_query_fieldSingle(CityID,
				new DalHints().setShardColValue("CityID", 25));
		assertEquals("Initial_Shard_11", ret);
	}

	@Test
	public void testtest_def_update_in() throws Exception {
		String Name = "def_update_shard";// Test value here
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);

		int ret = dao.test_def_update_in(Name, CityID,
				new DalHints().setShardColValue("CityID", 20));

		ShardColModShardByDBOnSqlServerGen pojo = dao.queryByPk(1l,
				new DalHints().inShard(0));
		assertEquals("def_update_shard", pojo.getName());

		pojo = dao.queryByPk(1l, new DalHints().inShard(1));
		assertEquals("Initial_Shard_11", pojo.getName());
	}

	@Test
	public void testtest_build_delete_equal() throws Exception {
		Integer CityID = 20;// Test value here
		dao.test_build_delete_equal(CityID, new DalHints().inAllShards());
		// assertEquals(-2, ret);

		int count = dao.count(new DalHints().inShard(0));
		assertEquals(2, count);

		count = dao.count(new DalHints().inShard(1));
		assertEquals(3, count);

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		CityID = 21;
		dao.test_build_delete_equal(CityID, new DalHints().inShards(shards));
		// assertEquals(-2, ret);
		count = dao.count(new DalHints().inShard(0));
		assertEquals(2, count);

		count = dao.count(new DalHints().inShard(1));
		assertEquals(2, count);

		CityID = 22;
		dao.test_build_delete_equal(CityID,
				new DalHints().setShardColValue("CityID", 301));
		// assertEquals(-1, ret);
		count = dao.count(new DalHints().inShard(0));
		assertEquals(2, count);

		count = dao.count(new DalHints().inShard(1));
		assertEquals(2, count);
	}

	@Test
	public void testtest_build_insert_equal() throws Exception {
		Integer CityID = 101;// Test value here
		String Name = "InsertInAllShards";// Test value here
		Integer ProvinceID = 100;// Test value here
		Integer CountryID = 100;// Test value here
		dao.test_build_insert_equal(CityID, Name, ProvinceID, CountryID,
				new DalHints().inAllShards());
		// assertEquals(-2, ret);
		int count = dao.count(new DalHints().inShard(0));
		assertEquals(4, count);

		count = dao.count(new DalHints().inShard(1));
		assertEquals(4, count);

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		CityID = 200;
		Name = "InsertBYShards";
		dao.test_build_insert(CityID, Name, ProvinceID, CountryID,
				new DalHints().inShards(shards));
		// assertEquals(-2, ret);
		count = dao.count(new DalHints().inShard(0));
		assertEquals(5, count);

		count = dao.count(new DalHints().inShard(1));
		assertEquals(5, count);

		CityID = 301;
		Name = "InsertBYColMod";
		dao.test_build_insert(CityID, Name, ProvinceID, CountryID,
				new DalHints().setShardColValue("CityID", 301));
		// assertEquals(-1, ret);
		count = dao.count(new DalHints().inShard(0));
		assertEquals(5, count);

		count = dao.count(new DalHints().inShard(1));
		assertEquals(6, count);

	}

	@Test
	public void testtest_build_update_equal() throws Exception {
		String Name = "UpdateInAllShards";// Test value here
		Integer CityID = 21;// Test value here
		dao.test_build_update_equal(Name, CityID, new DalHints().inAllShards());
		// assertEquals(-2, ret);
		ShardColModShardByDBOnSqlServerGen pojo = dao.queryByPk(1,
				new DalHints().inShard(1));
		assertEquals("UpdateInAllShards", pojo.getName());

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		Name = "UpdateByShards";// Test value here
		CityID = 20;// Test value here
		dao.test_build_update_equal(Name, CityID,
				new DalHints().inShards(shards));
		// assertEquals(-2, ret);
		pojo = dao.queryByPk(1, new DalHints().inShard(0));
		assertEquals("UpdateByShards", pojo.getName());

		Name = "UpdateByColMod";// Test value here
		CityID = 23;// Test value here
		dao.test_build_update_equal(Name, CityID,
				new DalHints().setShardColValue("CityID", 301));
		// assertEquals(-1, ret);
		pojo = dao.queryByPk(2, new DalHints().inShard(1));
		assertEquals("UpdateByColMod", pojo.getName());
	}

	@Test
	public void testtest_build_query_equal() throws Exception {
		Integer CityID = 21;// Test value here
		List<ShardColModShardByDBOnSqlServerGen> ret = dao
				.test_build_query_equal(CityID, new DalHints().inAllShards());
		assertEquals(1, ret.size());

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		CityID = 20;// Test value here
		ret = dao.test_build_query_equal(CityID,
				new DalHints().inShards(shards));
		assertEquals(1, ret.size());

		CityID = 25;// Test value here
		ret = dao.test_build_query_equal(CityID,
				new DalHints().setShardColValue("CityID", 301));
		assertEquals(1, ret.size());
	}
}
