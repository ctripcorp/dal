package shardtest.newVersionCodeTest;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import dao.shard.newVersionCode.IgnoreMissingFieldsAndAllowPartialTestOnSqlServerDao;
import entity.SqlServerPeopleTableWithDiffColumns;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit test of IgnoreMissingFieldsAndAllowPartialTestOnSqlServerDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
 **/
public class IgnoreMissingFieldsAndAllowPartialTestOnSqlServerDaoUnitTest {

	private static final String DATA_BASE = "ShardColModShardByDBOnSqlServer";
	//	private static final String DATA_BASE = "SqlServerSimpleShard";
	private static DalClient client = null;
	private static IgnoreMissingFieldsAndAllowPartialTestOnSqlServerDao dao = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		client = DalClientFactory.getClient(DATA_BASE);
		dao = new IgnoreMissingFieldsAndAllowPartialTestOnSqlServerDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
//		for(int i = 0; i < 10; i++) {
//			SqlServerPeopleTableWithDiffColumns daoPojo = createPojo(i);
//
//			try {
//				dao.insert(new DalHints().enableIdentityInsert(), daoPojo);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}

		dao.test_def_truncate(new DalHints().inShard(0));
		dao.test_def_truncate(new DalHints().inShard(1));


//		List<SqlServerPeopleTableWithDiffColumns> daoPojos1 = new ArrayList<SqlServerPeopleTableWithDiffColumns>(3);
//		for(int i=0;i<6;i++)
//		{
//			SqlServerPeopleTableWithDiffColumns daoPojo = new SqlServerPeopleTableWithDiffColumns();
////			if(i%2==0)
////			daoPojo.setName("Initial_Shard_0"+i);
////			else
////				daoPojo.setName("Initial_Shard_1"+i);
//			daoPojo.setCityID(i+20);
////			daoPojo.setProvinceID(i+30);
////			daoPojo.setCountryID(i+40);
//			daoPojos1.add(daoPojo);
//
//		}
//		dao.insert(new DalHints(), daoPojos1);

		dao.test_def_insert(20,new DalHints().inShard(0));
		dao.test_def_insert(22,new DalHints().inShard(0));
		dao.test_def_insert(24,new DalHints().inShard(0));
		dao.test_def_insert(21,new DalHints().inShard(1));
		dao.test_def_insert(23,new DalHints().inShard(1));
		dao.test_def_insert(25,new DalHints().inShard(1));


	}

	private SqlServerPeopleTableWithDiffColumns createPojo(int index) {
		SqlServerPeopleTableWithDiffColumns daoPojo = new SqlServerPeopleTableWithDiffColumns();

		//daoPojo.setId(index);
		//daoPojo set not null field

		return daoPojo;
	}

	private void changePojo(SqlServerPeopleTableWithDiffColumns daoPojo) {
		// Change a field to make pojo different with original one
	}

	private void changePojos(List<SqlServerPeopleTableWithDiffColumns> daoPojos) {
		for(SqlServerPeopleTableWithDiffColumns daoPojo: daoPojos)
			changePojo(daoPojo);
	}

	private void verifyPojo(SqlServerPeopleTableWithDiffColumns daoPojo) {
		//assert changed value
	}

	private void verifyPojos(List<SqlServerPeopleTableWithDiffColumns> daoPojos) {
		for(SqlServerPeopleTableWithDiffColumns daoPojo: daoPojos)
			verifyPojo(daoPojo);
	}

	@After
	public void tearDown() throws Exception {
//		dao.test_def_truncate(new DalHints().inShard(0));
//		dao.test_def_truncate(new DalHints().inShard(1));
	}


	@Test
	public void testtest_ClientQueryFromIgnoreMissingFields_list() throws Exception {
		//Integer CityID = null;// Test value here
		//List<SqlServerPeopleTableWithDiffColumns> ret = dao.test_build_query_list(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<SqlServerPeopleTableWithDiffColumns> ret = dao.test_ClientQueryFromIgnoreMissingFields_list(CityID, new DalHints().inAllShards().ignoreMissingFields(),1,1);
		assertEquals(1, ret.size());
		assertEquals(22, ret.get(0).getCityID().intValue());
		assertEquals(2, ret.get(0).getPeopleID().intValue());


		ret = dao.test_ClientQueryFromIgnoreMissingFields_list(CityID, new DalHints().setShardColValue("CityID", 20).ignoreMissingFields(),1,1);
		assertEquals(1, ret.size());
		assertEquals(22, ret.get(0).getCityID().intValue());
		assertEquals(2, ret.get(0).getPeopleID().intValue());
	}

	@Test
	public void testtest_build_queryIgnoreMissingFields_first() throws Exception {
		//Integer CityID = null;// Test value here
		//SqlServerPeopleTableWithDiffColumns ret = dao.test_build_query_first(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		SqlServerPeopleTableWithDiffColumns ret = dao.test_build_query_first(CityID, new DalHints().inAllShards().ignoreMissingFields());
		assertEquals(1, ret.getPeopleID().intValue());

		ret = dao.test_build_query_first(CityID, new DalHints().setShardColValue("CityID", 20).ignoreMissingFields());
		assertEquals(1, ret.getPeopleID().intValue());
	}

	@Test
	public void testtest_build_queryIgnoreMissingFields_list() throws Exception {
		//Integer CityID = null;// Test value here
		//List<SqlServerPeopleTableWithDiffColumns> ret = dao.test_build_query_list(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<SqlServerPeopleTableWithDiffColumns> ret = dao.test_build_query_list(CityID, new DalHints().inAllShards().ignoreMissingFields());
		assertEquals(3, ret.size());

		ret = dao.test_build_query_list(CityID, new DalHints().setShardColValue("CityID", 20).ignoreMissingFields());
		assertEquals(2, ret.size());
	}

	@Test
	public void testtest_build_queryIgnoreMissingFields_listByPage() throws Exception {
		//Integer CityID = null;// Test value here
		//List<SqlServerPeopleTableWithDiffColumns> ret = dao.test_build_query_listByPage(CityID, 1, 10, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<SqlServerPeopleTableWithDiffColumns> ret = dao.test_build_query_listByPage(CityID, 2, 1, new DalHints().inShard(0).ignoreMissingFields());
		assertEquals(2, ret.get(0).getPeopleID().intValue());

		ret = dao.test_build_query_listByPage(CityID, 1, 1, new DalHints().setShardColValue("CityID", 21).ignoreMissingFields());
		assertEquals(2, ret.get(0).getPeopleID().intValue());
	}

	@Test
	public void testtest_build_queryIgnoreMissingFields_single() throws Exception {
		//Integer CityID = null;// Test value here
		//SqlServerPeopleTableWithDiffColumns ret = dao.test_build_query_single(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		SqlServerPeopleTableWithDiffColumns ret = dao.test_build_query_single(CityID, new DalHints().setShardColValue("CityID", 21).ignoreMissingFields());
		assertEquals(2, ret.getPeopleID().intValue());

		CityID.clear();
		CityID.add(30);
		CityID.add(31);
		ret = dao.test_build_query_single(CityID, new DalHints().inShard(0).ignoreMissingFields());
		assertNull(ret);

	}

	@Test
	public void testtest_def_queryIgnoreMissingFields_listFirst() throws Exception {
		//Integer CityID = 1;// Test value here
		//DefPojo ret = dao.test_def_query_listFirst(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		SqlServerPeopleTableWithDiffColumns ret = dao.test_def_queryIgnoreMissingFields_listFirst(CityID, new DalHints().setShardColValue("CityID", 20).ignoreMissingFields());
		assertEquals(1, ret.getPeopleID().intValue());

		ret = dao.test_def_queryIgnoreMissingFields_listFirst(CityID, new DalHints().inAllShards().ignoreMissingFields());
		assertEquals(1, ret.getPeopleID().intValue());

		CityID.clear();
		CityID.add(30);
		CityID.add(31);

		ret = dao.test_def_queryIgnoreMissingFields_listFirst(CityID, new DalHints().inShard(1).ignoreMissingFields());
		assertNull(ret);
	}

	@Test
	public void testtest_def_queryAllowPartial_listFirst() throws Exception {
		//Integer CityID = 1;// Test value here
		//DefPojo ret = dao.test_def_query_listFirst(CityID, new DalHints());


		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		SqlServerPeopleTableWithDiffColumns ret = dao.test_def_queryAllowPartial_listFirst(CityID, new DalHints().setShardColValue("CityID", 20).allowPartial());
		assertEquals(1, ret.getPeopleID().intValue());

		ret = dao.test_def_queryAllowPartial_listFirst(CityID, new DalHints().inAllShards().allowPartial());
		assertNotNull(ret.getPeopleID());

		CityID.clear();
		CityID.add(30);
		CityID.add(31);

		ret = dao.test_def_queryAllowPartial_listFirst(CityID, new DalHints().inShard(1).allowPartial());
		assertNull(ret);
	}

	@Test
	public void testtest_def_queryIgnoreMissingFields_list() throws Exception {
		//Integer CityID = 1;// Test value here
		//List<DefPojo> ret = dao.test_def_query_list(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
//		List<SqlServerPeopleTableWithDiffColumns> ret = dao.test_def_query_list(CityID, new DalHints().inAllShards());
//	    assertEquals(3, ret.size());

		List<SqlServerPeopleTableWithDiffColumns> ret = dao.test_def_queryIgnoreMissingFields_list(CityID, new DalHints().inShard(1).ignoreMissingFields());
		assertEquals(1, ret.size());

		ret = dao.test_def_queryIgnoreMissingFields_list(CityID, new DalHints().setShardColValue("CityID", 20).ignoreMissingFields());
		assertEquals(2, ret.size());

		CityID.clear();
		CityID.add(30);
		CityID.add(31);

		ret = dao.test_def_queryIgnoreMissingFields_list(CityID, new DalHints().setShardColValue("CityID", 20).ignoreMissingFields());
		assertEquals(0, ret.size());
	}

	@Test
	public void testtest_def_queryAllowPartial_list() throws Exception {
		//Integer CityID = 1;// Test value here
		//List<DefPojo> ret = dao.test_def_query_list(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
//		List<SqlServerPeopleTableWithDiffColumns> ret = dao.test_def_query_list(CityID, new DalHints().inAllShards());
//	    assertEquals(3, ret.size());

		List<SqlServerPeopleTableWithDiffColumns> ret = dao.test_def_queryAllowPartial_list(CityID, new DalHints().inShard(1).allowPartial());
		assertEquals(1, ret.size());

		ret = dao.test_def_queryAllowPartial_list(CityID, new DalHints().setShardColValue("CityID", 20).allowPartial());
		assertEquals(2, ret.size());

		CityID.clear();
		CityID.add(30);
		CityID.add(31);

		ret = dao.test_def_queryAllowPartial_list(CityID, new DalHints().setShardColValue("CityID", 20).allowPartial());
		assertEquals(0, ret.size());
	}

	@Test
	public void testtest_def_queryIgnoreMissingFields_listByPage() throws Exception {
		//Integer CityID = 1;// Test value here
		//List<DefPojo> ret = dao.test_def_query_listByPage(CityID, 1, 10, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<SqlServerPeopleTableWithDiffColumns> ret = dao.test_def_queryIgnoreMissingFields_listByPage(CityID, 2, 1, new DalHints().inShard(0).ignoreMissingFields());
		assertEquals(2, ret.get(0).getPeopleID().intValue());

		ret = dao.test_def_queryIgnoreMissingFields_listByPage(CityID, 1, 1, new DalHints().setShardColValue("CityID", 21).ignoreMissingFields());
		assertEquals(2, ret.get(0).getPeopleID().intValue());
	}

	@Test
	public void testtest_def_queryAllowPartial_listByPage() throws Exception {
		//Integer CityID = 1;// Test value here
		//List<DefPojo> ret = dao.test_def_query_listByPage(CityID, 1, 10, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<SqlServerPeopleTableWithDiffColumns> ret = dao.test_def_queryAllowPartial_listByPage(CityID, 2, 1, new DalHints().inShard(0).allowPartial());
		assertEquals(2, ret.get(0).getPeopleID().intValue());

		ret = dao.test_def_queryAllowPartial_listByPage(CityID, 1, 1, new DalHints().setShardColValue("CityID", 21).allowPartial());
		assertEquals(2, ret.get(0).getPeopleID().intValue());
	}

	@Test
	public void testtest_def_queryIgnoreMissingFields_listSingle() throws Exception {
		//Integer CityID = 1;// Test value here
		//DefPojo ret = dao.test_def_query_listSingle(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		SqlServerPeopleTableWithDiffColumns ret = dao.test_def_queryIgnoreMissingFields_listSingle(CityID, new DalHints().setShardColValue("CityID", 21).ignoreMissingFields());
		assertEquals(2, ret.getPeopleID().intValue());

		CityID.clear();
		CityID.add(30);
		CityID.add(31);
		ret = dao.test_def_queryIgnoreMissingFields_listSingle(CityID, new DalHints().inShard(0).ignoreMissingFields());
		assertNull(ret);
	}

	@Test
	public void testtest_def_queryAllowPartial_listSingle() throws Exception {
		//Integer CityID = 1;// Test value here
		//DefPojo ret = dao.test_def_query_listSingle(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		SqlServerPeopleTableWithDiffColumns ret = dao.test_def_queryAllowPartial_listSingle(CityID, new DalHints().setShardColValue("CityID", 21).allowPartial());
		assertEquals(2, ret.getPeopleID().intValue());

		CityID.clear();
		CityID.add(30);
		CityID.add(31);
		ret = dao.test_def_queryAllowPartial_listSingle(CityID, new DalHints().inShard(0).allowPartial());
		assertNull(ret);
	}

}
