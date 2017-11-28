package shardTest.oldVersionCodeTest;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * JUnit test of PeopleSimpleShardByDBOnSqlServerGenDao class. Before run the
 * unit test, you should initiate the test data and change all the asserts
 * correspond to you case.
 **/
public class ignoreMissingFieldsAndAllowPartialTestOnSqlServerGenDaoUnitTest {

	private static final String DATA_BASE = "ShardColModShardByDBOnSqlServer";

	private static DalClient client = null;
	private static ignoreMissingFieldsAndAllowPartialTestOnSqlServerGenDao dao = null;

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
		dao = new ignoreMissingFieldsAndAllowPartialTestOnSqlServerGenDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		dao.test_def_update(new DalHints().inShard(0));
		dao.test_def_update(new DalHints().inShard(1));
//
//		List<ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen> daoPojos1 = new ArrayList<ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen>(
//				3);
//		for (int i = 0; i < 6; i++) {
//			ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen daoPojo = new ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen();
////			if (i % 2 == 0)
////				daoPojo.setName("Initial_Shard_0" + i);
////			else
////				daoPojo.setName("Initial_Shard_1" + i);
//			daoPojo.setCityID(i + 20);
////			daoPojo.setProvinceID(i + 30);
////			daoPojo.setCountryID(i + 40);
//			daoPojos1.add(daoPojo);
//		}
//		dao.insert(new DalHints(), daoPojos1);
		dao.test_def_insert(20,new DalHints().inShard(0));
		dao.test_def_insert(22,new DalHints().inShard(0));
		dao.test_def_insert(24,new DalHints().inShard(0));
		dao.test_def_insert(21,new DalHints().inShard(1));
		dao.test_def_insert(23,new DalHints().inShard(1));
		dao.test_def_insert(25,new DalHints().inShard(1));
	}

	@After
	public void tearDown() throws Exception {
//		dao.test_def_update(new DalHints().inShard(0));
//		dao.test_def_update(new DalHints().inShard(1));
	}


	@Test
	public void testtest_build_queryIgnoreMissingFields_pojoFirst() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen ret = dao
				.test_build_queryIgnoreMissingFields_pojoFirst(CityID,
						new DalHints().inAllShards().ignoreMissingFields());
		assertEquals(1, ret.getPeopleID().intValue());

		ret = dao.test_build_queryIgnoreMissingFields_pojoFirst(CityID,
				new DalHints().setShardValue(2).ignoreMissingFields());
		assertEquals(1, ret.getPeopleID().intValue());
	}

	@Test
	public void testtest_build_queryAllowPartial_pojoFirst() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen ret = dao
				.test_build_queryAllowPartial_pojoFirst(CityID,
						new DalHints().inAllShards().allowPartial());
		assertEquals(1, ret.getPeopleID().intValue());

		ret = dao.test_build_queryAllowPartial_pojoFirst(CityID,
				new DalHints().setShardValue(2).allowPartial());
		assertEquals(1, ret.getPeopleID().intValue());
	}

	@Test
	public void testtest_build_queryFromIgnoreMissingFields_pojoList() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		List<ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen> ret = dao
				.test_build_queryFromIgnoreMissingFields_pojoList(CityID, new DalHints().inAllShards().ignoreMissingFields(),0,3);
		assertEquals(3, ret.size());
		// for(int i=0;i<ret.size();i++)
		// System.out.println(ret.get(i).getName());

		ret = dao.test_build_queryFromIgnoreMissingFields_pojoList(CityID,
				new DalHints().setShardColValue("CityID", 20).ignoreMissingFields(),0,2);
		assertEquals(2, ret.size());
	}

	@Test
	public void testtest_build_queryFromAllowPartial_pojoList() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		List<ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen> ret = dao
				.test_build_queryFromAllowPartial_pojoList(CityID, new DalHints().inAllShards().allowPartial(),0,3);
		assertEquals(3, ret.size());
		// for(int i=0;i<ret.size();i++)
		// System.out.println(ret.get(i).getName());

		ret = dao.test_build_queryFromAllowPartial_pojoList(CityID,
				new DalHints().setShardColValue("CityID", 20).allowPartial(),0,2);
		assertEquals(2, ret.size());
	}

	@Test
	public void testtest_build_queryIgnoreMissingFields_pojoList() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		List<ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen> ret = dao
				.test_build_queryIgnoreMissingFields_pojoList(CityID, new DalHints().inAllShards().ignoreMissingFields());
		assertEquals(3, ret.size());
		// for(int i=0;i<ret.size();i++)
		// System.out.println(ret.get(i).getName());

		ret = dao.test_build_queryIgnoreMissingFields_pojoList(CityID,
				new DalHints().setShardColValue("CityID", 21).ignoreMissingFields());
		assertEquals(1, ret.size());
	}

	@Test
	public void testtest_build_queryAllowPartial_pojoList() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		List<ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen> ret = dao
				.test_build_queryAllowPartial_pojoList(CityID, new DalHints().inAllShards().allowPartial());
		assertEquals(3, ret.size());
		// for(int i=0;i<ret.size();i++)
		// System.out.println(ret.get(i).getName());

		ret = dao.test_build_queryAllowPartial_pojoList(CityID,
				new DalHints().setShardColValue("CityID", 21).allowPartial());
		assertEquals(1, ret.size());
	}

	@Test
	public void test_build_queryIgnoreMissingFields_pojoListByPage() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(5);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);
		CityID.add(24);
		CityID.add(23);

		List<ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen> ret = dao
				.test_build_queryIgnoreMissingFields_pojoListByPage(CityID, 1, 10,
						new DalHints().setShardColValue("CityID", 22).ignoreMissingFields());
		// for(int i=0;i<ret.size();i++)
		// System.out.println(ret.get(i).getName());

		assertEquals(3, ret.size());
		ret = dao.test_build_queryIgnoreMissingFields_pojoListByPage(CityID, 2, 2,
				new DalHints().inShard(1).ignoreMissingFields());
		assertEquals(0, ret.size());
	}

	@Test
	public void test_build_queryAllowPartial_pojoListByPage() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(5);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);
		CityID.add(24);
		CityID.add(23);

		List<ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen> ret = dao
				.test_build_queryAllowPartial_pojoListByPage(CityID, 1, 10,
						new DalHints().setShardColValue("CityID", 22).allowPartial());
		// for(int i=0;i<ret.size();i++)
		// System.out.println(ret.get(i).getName());

		assertEquals(3, ret.size());
		ret = dao.test_build_queryAllowPartial_pojoListByPage(CityID, 2, 2,
				new DalHints().inShard(1).allowPartial());
		assertEquals(0, ret.size());
	}

	@Test
	public void testtest_build_queryIgnoreMissingFields_pojoSingle() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen ret = dao
				.test_build_queryIgnoreMissingFields_pojoSingle(CityID,
						new DalHints().setShardColValue("CityID", 24).ignoreMissingFields());
		assertEquals(1, ret.getPeopleID().intValue());

		ret = dao.test_build_queryIgnoreMissingFields_pojoSingle(CityID,
				new DalHints().setShardColValue("CityID", 25).ignoreMissingFields());
		assertEquals(1, ret.getPeopleID().intValue());
	}

	@Test
	public void testtest_build_queryAllowPartial_pojoSingle() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen ret = dao
				.test_build_queryAllowPartial_pojoSingle(CityID,
						new DalHints().setShardColValue("CityID", 24).allowPartial());
		assertEquals(1, ret.getPeopleID().intValue());

		ret = dao.test_build_queryAllowPartial_pojoSingle(CityID,
				new DalHints().setShardColValue("CityID", 25).allowPartial());
		assertEquals(1, ret.getPeopleID().intValue());
	}

	@Test
	public void testtest_def_queryIgnoreMissingFields_pojoFirst() throws Exception {
		// Integer CityID = 1;// Test value here
		// Test_def_query_pojoPojo ret = dao.test_def_query_pojoFirst(CityID,
		// new DalHints());
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen ret = dao
				.test_def_queryIgnoreMissingFields_pojoFirst(CityID, new DalHints().inAllShards().ignoreMissingFields());
		assertEquals(1, ret.getPeopleID().intValue());

		ret = dao.test_def_queryIgnoreMissingFields_pojoFirst(CityID,
				new DalHints().setShardValue(2).ignoreMissingFields());
		assertEquals(1, ret.getPeopleID().intValue());
	}


	@Test
	public void testtest_def_queryAllowPartial_pojoFirst() throws Exception {
		// Integer CityID = 1;// Test value here
		// Test_def_query_pojoPojo ret = dao.test_def_query_pojoFirst(CityID,
		// new DalHints());
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen ret = dao
				.test_def_queryAllowPartial_pojoFirst(CityID, new DalHints().inAllShards().allowPartial());
		assertEquals(1, ret.getPeopleID().intValue());

		ret = dao.test_def_queryAllowPartial_pojoFirst(CityID,
				new DalHints().setShardValue(2).allowPartial());
		assertEquals(1, ret.getPeopleID().intValue());
	}

	@Test
	public void testtest_def_queryIgnoreMissingFields_pojoList() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		List<ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen> ret = dao
				.test_def_queryIgnoreMissingFields_pojoList(CityID, new DalHints().inAllShards().ignoreMissingFields());
		assertEquals(3, ret.size());
		// for(int i=0;i<ret.size();i++)
		// System.out.println(ret.get(i).getName());

		ret = dao.test_def_queryIgnoreMissingFields_pojoList(CityID,
				new DalHints().setShardColValue("CityID", 21).ignoreMissingFields());
		assertEquals(1, ret.size());
	}

	@Test
	public void test_def_queryAllowPartial_pojoList() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		List<ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen> ret = dao
				.test_def_queryAllowPartial_pojoList(CityID, new DalHints().inAllShards().allowPartial());
		assertEquals(3, ret.size());
		// for(int i=0;i<ret.size();i++)
		// System.out.println(ret.get(i).getName());

		ret = dao.test_def_queryAllowPartial_pojoList(CityID,
				new DalHints().setShardColValue("CityID", 21).allowPartial());
		assertEquals(1, ret.size());
	}

	@Test
	public void testtest_def_queryIgnoreMissingFields_pojoListByPage() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(5);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);
		CityID.add(24);
		CityID.add(23);

		List<ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen> ret = dao
				.test_def_queryIgnoreMissingFields_pojoListByPage(CityID, 1, 10,
						new DalHints().setShardColValue("CityID", 22).ignoreMissingFields());
		// for(int i=0;i<ret.size();i++)
		// System.out.println(ret.get(i).getName());

		assertEquals(3, ret.size());
		ret = dao.test_def_queryIgnoreMissingFields_pojoListByPage(CityID, 2, 2,
				new DalHints().inShard(1).ignoreMissingFields());
		assertEquals(0, ret.size());
	}

	@Test
	public void testtest_def_queryAllowPartial_pojoListByPage() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(5);// Test value here
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);
		CityID.add(24);
		CityID.add(23);

		List<ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen> ret = dao
				.test_def_queryAllowPartial_pojoListByPage(CityID, 1, 10,
						new DalHints().setShardColValue("CityID", 22).allowPartial());
		// for(int i=0;i<ret.size();i++)
		// System.out.println(ret.get(i).getName());

		assertEquals(3, ret.size());
		ret = dao.test_def_queryAllowPartial_pojoListByPage(CityID, 2, 2,
				new DalHints().inShard(1).allowPartial());
		assertEquals(0, ret.size());
	}

	@Test
	public void testtest_def_queryIgnoreMissingFields_pojoSingle() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen ret = dao
				.test_def_queryIgnoreMissingFields_pojoSingle(CityID,
						new DalHints().setShardColValue("CityID", 24).ignoreMissingFields());
		assertEquals(1, ret.getPeopleID().intValue());

		ret = dao.test_def_queryIgnoreMissingFields_pojoSingle(CityID,
				new DalHints().setShardColValue("CityID", 25).ignoreMissingFields());
		assertEquals(1, ret.getPeopleID().intValue());
	}

	@Test
	public void testtest_def_queryAllowPartial_pojoSingle() throws Exception {
		List<Integer> CityID = new ArrayList<Integer>(2);// Test value here
		CityID.add(20);
		CityID.add(21);
		ignoreMissingFieldsAndAllowPartialTestOnSqlServerGen ret = dao
				.test_def_queryAllowPartial_pojoSingle(CityID,
						new DalHints().setShardColValue("CityID", 24).allowPartial());
		assertEquals(1, ret.getPeopleID().intValue());

		ret = dao.test_def_queryAllowPartial_pojoSingle(CityID,
				new DalHints().setShardColValue("CityID", 25).allowPartial());
		assertEquals(1, ret.getPeopleID().intValue());
	}

}
