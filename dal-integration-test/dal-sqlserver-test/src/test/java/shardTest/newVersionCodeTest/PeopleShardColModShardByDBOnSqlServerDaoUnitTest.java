package shardTest.newVersionCodeTest;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.sql.SQLException;


import org.junit.*;

import static org.junit.Assert.*;


import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.client.DalHA;
import com.ctrip.platform.dal.dao.helper.DalListMerger;


/**
 * JUnit test of ignoreMissingFieldsAndAllowPartialTestOnSqlServerDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
 **/
public class PeopleShardColModShardByDBOnSqlServerDaoUnitTest {

	private static final String DATA_BASE = "ShardColModShardByDBOnSqlServer";
	//	private static final String DATA_BASE = "SqlServerSimpleShard";
	private static DalClient client = null;
	private static PeopleShardColModShardByDBOnSqlServerDao dao = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/**
		 * Initialize DalClientFactory.
		 * The Dal.config can be specified from class-path or local file path.
		 * One of follow three need to be enabled.
		 **/
		DalClientFactory.initClientFactory(); // load from class-path Dal.config
		DalClientFactory.warmUpConnections();
		client = DalClientFactory.getClient(DATA_BASE);
		dao = new PeopleShardColModShardByDBOnSqlServerDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
//		for(int i = 0; i < 10; i++) {
//			ignoreMissingFieldsAndAllowPartialTestOnSqlServer daoPojo = createPojo(i);
//
//			try {
//				dao.insert(new DalHints().enableIdentityInsert(), daoPojo);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}

		dao.test_def_truncate(new DalHints().inShard(0));
		dao.test_def_truncate(new DalHints().inShard(1));


//		List<ignoreMissingFieldsAndAllowPartialTestOnSqlServer> daoPojos1 = new ArrayList<ignoreMissingFieldsAndAllowPartialTestOnSqlServer>(3);
//		for(int i=0;i<6;i++)
//		{
//			ignoreMissingFieldsAndAllowPartialTestOnSqlServer daoPojo = new ignoreMissingFieldsAndAllowPartialTestOnSqlServer();
//			if(i%2==0)
//			daoPojo.setName("Initial_Shard_0"+i);
//			else
//				daoPojo.setName("Initial_Shard_1"+i);	
//			daoPojo.setCityID(i+20);
//			daoPojo.setProvinceID(i+30);
//			daoPojo.setCountryID(i+40);
//			daoPojos1.add(daoPojo);
//		}
//		dao.insert(new DalHints(), daoPojos1);
		List<PeopleShardColModShardByDBOnSqlServer> daoPojos1 = new ArrayList<PeopleShardColModShardByDBOnSqlServer>(3);
		for (int i = 0; i < 12; i++) {
			PeopleShardColModShardByDBOnSqlServer daoPojo = new PeopleShardColModShardByDBOnSqlServer();
			if (i % 2 == 0) {
				daoPojo.setName("Initial_Shard_0" + i);
				if (i < 6)
					daoPojo.setCountryID(20);
				else
					daoPojo.setCountryID(21);
			} else {
				daoPojo.setName("Initial_Shard_1" + i);
				if (i < 7)
					daoPojo.setCountryID(22);
				else
					daoPojo.setCountryID(23);
			}
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);

			daoPojos1.add(daoPojo);
		}
		dao.insert(new DalHints(), daoPojos1);
	}

	private PeopleShardColModShardByDBOnSqlServer createPojo(int index) {
		PeopleShardColModShardByDBOnSqlServer daoPojo = new PeopleShardColModShardByDBOnSqlServer();

		//daoPojo.setId(index);
		//daoPojo set not null field

		return daoPojo;
	}

	private void changePojo(PeopleShardColModShardByDBOnSqlServer daoPojo) {
		// Change a field to make pojo different with original one
	}

	private void changePojos(List<PeopleShardColModShardByDBOnSqlServer> daoPojos) {
		for(PeopleShardColModShardByDBOnSqlServer daoPojo: daoPojos)
			changePojo(daoPojo);
	}

	private void verifyPojo(PeopleShardColModShardByDBOnSqlServer daoPojo) {
		//assert changed value
	}

	private void verifyPojos(List<PeopleShardColModShardByDBOnSqlServer> daoPojos) {
		for(PeopleShardColModShardByDBOnSqlServer daoPojo: daoPojos)
			verifyPojo(daoPojo);
	}

	@After
	public void tearDown() throws Exception {
//		dao.test_def_truncate(new DalHints().inShard(0));
//		dao.test_def_truncate(new DalHints().inShard(1));
//		Thread.sleep(2000);
	}


	@Test
	public void testCount() throws Exception {
		int ret1 = dao.count(new DalHints().inShard(0));
		assertEquals(6,ret1);
		int ret2 = dao.count(new DalHints().inShard(1));
		assertEquals(6,ret2);
		int ret3 = dao.count(new DalHints().inAllShards());
		assertEquals(12,ret3);
	}

	@Test
	public void testDelete1() throws Exception {

		PeopleShardColModShardByDBOnSqlServer daoPojo = createPojo(1);
		daoPojo.setPeopleID(2l);//存在数据返回0
//		daoPojo.setPeopleID(20l);//不存在数据返回100
		daoPojo.setCityID(20);
		int affected = dao.delete(new DalHints().setShardColValue("CityID", 20), daoPojo);
//		System.out.println(affected);
//		assertEquals(1, affected);

		affected = dao.count(new DalHints().inShard(0));
		assertEquals(5, affected);

		affected = dao.count(new DalHints().inShard(1));
		assertEquals(6, affected);
	}

	@Test
	public void testDelete2() throws Exception {

		List<PeopleShardColModShardByDBOnSqlServer> daoPojos = dao.queryAll(new DalHints().inShard(1));
		int[] affected = dao.delete(new DalHints().setShardValue(21), daoPojos);
//		assertArrayEquals(new int[]{1,1,1},  affected);

		int ret=dao.count(new DalHints().inShard(0));
		assertEquals(6, ret);

		ret=dao.count(new DalHints().inShard(1));
		assertEquals(0, ret);
	}

	@Test
	public void testBatchDelete() throws Exception {

		List<PeopleShardColModShardByDBOnSqlServer> daoPojos = dao.queryAll(new DalHints().inAllShards());
		int[] affected = dao.batchDelete(new DalHints(), daoPojos);
//		assertArrayEquals(new int[]{1,1,1},  affected);

		int ret=dao.count(new DalHints().inShard(0));
		assertEquals(0, ret);

		ret=dao.count(new DalHints().inShard(1));
		assertEquals(0, ret);
	}

	@Test
	public void testQueryAll() throws Exception {
		List<PeopleShardColModShardByDBOnSqlServer> ret=dao.queryAll(new DalHints().setShardValue(20));
		assertEquals(6, ret.size());

		ret=dao.queryAll(new DalHints().inAllShards());
		assertEquals(12, ret.size());
	}

	@Test
	public void testInsert1() throws Exception {
		PeopleShardColModShardByDBOnSqlServer daoPojo = new PeopleShardColModShardByDBOnSqlServer();
//		daoPojo.setPeopleID(4l);  
		daoPojo.setName("Initial_Shard_14");
		daoPojo.setCityID(24);
//		daoPojo.setProvinceID(34);
//		daoPojo.setCountryID(44);

//		int affected = dao.insert(new DalHints().inShard(0), daoPojo);
		int affected = dao.insert(new DalHints(), daoPojo);

		Number id = 7l;
		PeopleShardColModShardByDBOnSqlServer ret1 = dao.queryByPk(id, new DalHints().inShard(0));
		assertNotNull(ret1);

		ret1 = dao.queryByPk(id, new DalHints().inShard(1));
		assertNull(ret1);

		affected = dao.insert(new DalHints().inShard(1), daoPojo);

		PeopleShardColModShardByDBOnSqlServer ret2 = dao.queryByPk(id, new DalHints().inShard(1));
		assertNotNull(ret2);
	}

	@Test
	public void testInsert2() throws Exception {

		List<PeopleShardColModShardByDBOnSqlServer> daoPojos = dao.queryAll(new DalHints().inShard(0));
		int[] affected = dao.insert(new DalHints(), daoPojos);
//		assertArrayEquals(new int[]{1,1,1},  affected);

		int ret=dao.count(new DalHints().inShard(0));
		assertEquals(12, ret);

		ret=dao.count(new DalHints().inShard(1));
		assertEquals(6, ret);

	}

	@Test
	public void testInsert3() throws Exception {
		KeyHolder keyHolder = new KeyHolder();
		PeopleShardColModShardByDBOnSqlServer daoPojo = createPojo(1);
		daoPojo.setCityID(101);
		int affected = dao.insert(new DalHints(), keyHolder, daoPojo);
//		assertEquals(1, affected);
		assertEquals(1, keyHolder.size());
		assertEquals(7l, keyHolder.getKey());

		int ret=dao.count(new DalHints().inShard(0));
		assertEquals(6, ret);

		ret=dao.count(new DalHints().inShard(1));
		assertEquals(7, ret);
	}

	@Test
	public void testInsert4() throws Exception {
		KeyHolder keyHolder = new KeyHolder();
		List<PeopleShardColModShardByDBOnSqlServer> daoPojos = dao.queryAll(new DalHints().inShard(0));
		int[] affected = dao.insert(new DalHints(), keyHolder, daoPojos);
//		assertArrayEquals(new int[]{1,1,1},  affected);
		assertEquals(6, keyHolder.size());
		assertEquals(7l,keyHolder.getKey(0));

		int ret=dao.count(new DalHints().inShard(0));
		assertEquals(12, ret);

		ret=dao.count(new DalHints().inShard(1));
		assertEquals(6, ret);
	}

	@Test
	public void testInsert5() throws Exception {
		List<PeopleShardColModShardByDBOnSqlServer> daoPojos = dao.queryAll(new DalHints().inAllShards());
		int[] affected = dao.batchInsert(new DalHints(), daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1},  affected);
		assertEquals(12, affected.length);

		int ret=dao.count(new DalHints().inShard(0));
		assertEquals(12, ret);

		ret=dao.count(new DalHints().inShard(1));
		assertEquals(12, ret);
	}

	@Test
	public void testQueryAllByPage() throws Exception {
		List<PeopleShardColModShardByDBOnSqlServer> ret=dao.queryAllByPage(2, 1, new DalHints().setShardValue(20));
		assertEquals("Initial_Shard_02", ret.get(0).getName());

		ret=dao.queryAllByPage(1, 2, new DalHints().setShardValue(21));
		assertEquals("Initial_Shard_13", ret.get(1).getName());
	}

	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 1l;
		PeopleShardColModShardByDBOnSqlServer ret = dao.queryByPk(id, new DalHints().setShardColValue("CityID", 20));
		assertEquals("Initial_Shard_00", ret.getName());

		ret = dao.queryByPk(id, new DalHints().inShard(1));
		assertEquals("Initial_Shard_11", ret.getName());

		id=7l;
		ret = dao.queryByPk(id, new DalHints().inShard(1));
		assertNull(ret);

	}

	@Test
	public void testQueryByPk2() throws Exception {
		PeopleShardColModShardByDBOnSqlServer pk = new PeopleShardColModShardByDBOnSqlServer();
		pk.setPeopleID(1l);
		PeopleShardColModShardByDBOnSqlServer ret = dao.queryByPk(pk, new DalHints().inShard(0));
		assertEquals("Initial_Shard_00", ret.getName());

		ret = dao.queryByPk(pk, new DalHints().inShard(1));
		assertEquals("Initial_Shard_11", ret.getName());

		pk.setPeopleID(7l);
		ret = dao.queryByPk(pk, new DalHints().setShardValue(20));
		assertNull(ret);
	}

	@Test
	public void testQueryLike() throws Exception {
		PeopleShardColModShardByDBOnSqlServer sample = new PeopleShardColModShardByDBOnSqlServer();
		sample.setCityID(20);
		sample.setName("Initial_Shard_00");

		List<PeopleShardColModShardByDBOnSqlServer> ret=dao.queryLike(sample, new DalHints().inShard(0));
		assertEquals(1, ret.size());

		ret=dao.queryLike(sample, new DalHints().inShard(1));
		assertEquals(0, ret.size());

		ret=dao.queryLike(sample, new DalHints().inAllShards());
		assertEquals(1, ret.size());
	}

	@Test
	public void testUpdate1() throws Exception {
		PeopleShardColModShardByDBOnSqlServer daoPojo = createPojo(1);
		daoPojo.setPeopleID(1l);
		daoPojo.setName("updateshard0");
		daoPojo.setCityID(22);
		int affected = dao.update(new DalHints(), daoPojo);
//		assertEquals(1, affected);

		PeopleShardColModShardByDBOnSqlServer ret=dao.queryByPk(1, new DalHints().inShard(0));
		assertEquals("updateshard0", ret.getName());
//		
//		ret=dao.queryByPk(2, new DalHints().inShard(0));
//		assertEquals("Initial_Shard_02", ret.getName());
	}

	@Test
	public void testUpdate2() throws Exception {
//		DalHints hints = new DalHints();
//		List<ignoreMissingFieldsAndAllowPartialTestOnSqlServer> daoPojos = dao.queryAll(new DalHints());
//		changePojos(daoPojos);
//		int[] affected = dao.update(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		verifyPojos(dao.queryAll(new DalHints()));
//		
		List<PeopleShardColModShardByDBOnSqlServer> daoPojos = new ArrayList<PeopleShardColModShardByDBOnSqlServer>(2);
		PeopleShardColModShardByDBOnSqlServer daoPojo1=new PeopleShardColModShardByDBOnSqlServer();

		daoPojo1.setPeopleID(1l);
		daoPojo1.setCityID(20);
		daoPojo1.setName("updateShard0");

		daoPojos.add(daoPojo1);

		PeopleShardColModShardByDBOnSqlServer daoPojo2=new PeopleShardColModShardByDBOnSqlServer();

		daoPojo2.setPeopleID(1l);
		daoPojo2.setCityID(21);
		daoPojo2.setName("updateShard1");

		daoPojos.add(daoPojo2);

		int[] affected = dao.update(new DalHints(), daoPojos);
//		assertArrayEquals(new int[]{1,1},  affected);

		PeopleShardColModShardByDBOnSqlServer ret=dao.queryByPk(1, new DalHints().inShard(0));
		assertEquals("updateShard0", ret.getName());
		ret=dao.queryByPk(1, new DalHints().inShard(1));
		assertEquals("updateShard1", ret.getName());
	}

	@Test
	public void testBatchUpdateSingle() throws Exception {
		List<PeopleShardColModShardByDBOnSqlServer> daoPojos = new ArrayList<PeopleShardColModShardByDBOnSqlServer>(2);
		PeopleShardColModShardByDBOnSqlServer daoPojo1=new PeopleShardColModShardByDBOnSqlServer();

		daoPojo1.setPeopleID(1l);
		daoPojo1.setCityID(20);
		daoPojo1.setName("updateShard0");

		daoPojos.add(daoPojo1);

//		ignoreMissingFieldsAndAllowPartialTestOnSqlServer daoPojo2=new ignoreMissingFieldsAndAllowPartialTestOnSqlServer();
//		
//		daoPojo2.setPeopleID(1l);
//		daoPojo2.setCityID(21);
//		daoPojo2.setName("updateShard1");
//		
//		daoPojos.add(daoPojo2);

		int[] affected = dao.batchUpdate(new DalHints(), daoPojos);
//		assertArrayEquals(new int[]{-2,-2},  affected);

		PeopleShardColModShardByDBOnSqlServer ret=dao.queryByPk(1, new DalHints().inShard(0));
		assertEquals("updateShard0", ret.getName());
//		ret=dao.queryByPk(1, new DalHints().inShard(1));
//		assertEquals("updateShard1", ret.getName());
	}

	@Test
	public void testBatchUpdate() throws Exception {
		List<PeopleShardColModShardByDBOnSqlServer> daoPojos = new ArrayList<PeopleShardColModShardByDBOnSqlServer>(2);
		PeopleShardColModShardByDBOnSqlServer daoPojo1=new PeopleShardColModShardByDBOnSqlServer();

		daoPojo1.setPeopleID(1l);
		daoPojo1.setCityID(20);
		daoPojo1.setName("updateShard0");

		daoPojos.add(daoPojo1);

		PeopleShardColModShardByDBOnSqlServer daoPojo2=new PeopleShardColModShardByDBOnSqlServer();

		daoPojo2.setPeopleID(1l);
		daoPojo2.setCityID(21);
		daoPojo2.setName("updateShard1");

		daoPojos.add(daoPojo2);

		int[] affected = dao.batchUpdate(new DalHints(), daoPojos);
//		assertArrayEquals(new int[]{-2,-2},  affected);

		PeopleShardColModShardByDBOnSqlServer ret=dao.queryByPk(1, new DalHints().inShard(0));
		assertEquals("updateShard0", ret.getName());
		ret=dao.queryByPk(1, new DalHints().inShard(1));
		assertEquals("updateShard1", ret.getName());
	}

	@Test
	public void testtest_build_delete() throws Exception {
		//Integer CityID = null;// Test value here
		//int ret = dao.test_build_delete(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);

		int ret=dao.test_build_delete(CityID, new DalHints().inAllShards());

		List<PeopleShardColModShardByDBOnSqlServer> pojos=dao.queryAll(new DalHints().inShard(0));
		assertEquals(4, pojos.size());

		pojos=dao.queryAll(new DalHints().inShard(1));
		assertEquals(5, pojos.size());
	}

	@Test
	public void testtest_build_queryEqual() throws Exception {
		Integer cityID=20;
		List<PeopleShardColModShardByDBOnSqlServer> ret=dao.test(cityID, new DalHints().inShard(0));
		assertEquals(1, ret.size());
	}

	@Test
	public void testtest_build_insert() throws Exception {
		//Integer CityID = null;// Test value here
		//String Name = "";// Test value here
		//Integer ProvinceID = null;// Test value here
		//Integer CountryID = null;// Test value here
		//int ret = dao.test_build_insert(CityID, Name, ProvinceID, CountryID, new DalHints());
		String Name = "insert";// Test value here
		Integer CityID = 20;// Test value here
		Integer ProvinceID = 36;// Test value here
		Integer CountryID = 46;
		int ret = dao.test_build_insert(CityID,Name,ProvinceID,CountryID, new DalHints());
//	    assertEquals(1, ret);

		PeopleShardColModShardByDBOnSqlServer pojo=dao.queryByPk(7, new DalHints().inShard(0));
		assertEquals("insert", pojo.getName());

		pojo=dao.queryByPk(7, new DalHints().inShard(1));
		assertNull(pojo);

		ret = dao.test_build_insert(CityID,Name,ProvinceID,CountryID, new DalHints().inAllShards());
//	    assertEquals(2, ret);

		pojo=dao.queryByPk(8, new DalHints().inShard(0));
		assertEquals("insert", pojo.getName());

		pojo=dao.queryByPk(7, new DalHints().inShard(1));
		assertEquals("insert", pojo.getName());
	}

	@Test
	public void testtest_build_update() throws Exception {
		//String Name = "";// Test value here
		//Integer CityID = null;// Test value here
		//int ret = dao.test_build_update(Name, CityID, new DalHints());
		String Name = "update";// Test value here
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		int ret = dao.test_build_update(Name, CityID, new DalHints().inAllShards());
//	    assertEquals(3, ret);

		PeopleShardColModShardByDBOnSqlServer pojo=dao.queryByPk(1, new DalHints().inShard(0));
		assertEquals("update", pojo.getName());

		pojo=dao.queryByPk(2, new DalHints().inShard(0));
		assertEquals("update", pojo.getName());

		pojo=dao.queryByPk(2, new DalHints().inShard(1));
		assertEquals("update", pojo.getName());
	}

	@Test
	public void testtest_build_query_first() throws Exception {
		//Integer CityID = null;// Test value here
		//ignoreMissingFieldsAndAllowPartialTestOnSqlServer ret = dao.test_build_query_first(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		PeopleShardColModShardByDBOnSqlServer ret = dao.test_build_query_first(CityID, new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret.getName());

		ret = dao.test_build_query_first(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals("Initial_Shard_00", ret.getName());
	}

	@Test
	public void testtest_build_queryPartial_first() throws Exception {
		//Integer CityID = null;// Test value here
		//ignoreMissingFieldsAndAllowPartialTestOnSqlServer ret = dao.test_build_query_first(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		PeopleShardColModShardByDBOnSqlServer ret = dao.test_build_queryPartial_first(CityID, new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret.getName());
		assertNull(ret.getPeopleID());

		ret = dao.test_build_queryPartial_first(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals("Initial_Shard_00", ret.getName());
		assertNull(ret.getPeopleID());
	}


	@Test
	public void testtest_ClientQueryFrom_list() throws Exception {
		//Integer CityID = null;// Test value here
		//List<ignoreMissingFieldsAndAllowPartialTestOnSqlServer> ret = dao.test_build_query_list(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<PeopleShardColModShardByDBOnSqlServer> ret = dao.test_ClientQueryFrom_list(CityID, new DalHints().inAllShards(),1,1);
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
		List<PeopleShardColModShardByDBOnSqlServer> ret = dao.test_ClientQueryFromPartialFieldsSet_list(CityID, new DalHints().inAllShards(),1,1);
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
		List<PeopleShardColModShardByDBOnSqlServer> ret = dao.test_ClientQueryFromPartialFieldsStrings_list(CityID, new DalHints().inAllShards(),1,1);
		assertEquals(1, ret.size());
		assertEquals(22, ret.get(0).getCityID().intValue());
		assertNull(ret.get(0).getPeopleID());

		ret = dao.test_ClientQueryFromPartialFieldsStrings_list(CityID, new DalHints().setShardColValue("CityID", 20),1,1);
		assertEquals(1, ret.size());
		assertEquals(22, ret.get(0).getCityID().intValue());
		assertNull(ret.get(0).getPeopleID());
	}

	@Test
	public void testtest_build_query_list() throws Exception {
		//Integer CityID = null;// Test value here
		//List<ignoreMissingFieldsAndAllowPartialTestOnSqlServer> ret = dao.test_build_query_list(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<PeopleShardColModShardByDBOnSqlServer> ret = dao.test_build_query_list(CityID, new DalHints().inAllShards());
		assertEquals(3, ret.size());

		ret = dao.test_build_query_list(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals(2, ret.size());
	}

	@Test
	public void testtest_build_queryPartial_list() throws Exception {
		//Integer CityID = null;// Test value here
		//List<ignoreMissingFieldsAndAllowPartialTestOnSqlServer> ret = dao.test_build_query_list(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<PeopleShardColModShardByDBOnSqlServer> ret = dao.test_build_queryPartial_list(CityID, new DalHints().inAllShards());
		assertEquals(3, ret.size());
		assertNull(ret.get(0).getPeopleID());

		ret = dao.test_build_queryPartial_list(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals(2, ret.size());
		assertNull(ret.get(0).getPeopleID());
	}

	@Test
	public void testtest_build_queryPartialFieldsByHints_list() throws Exception {
		//Integer CityID = null;// Test value here
		//List<ignoreMissingFieldsAndAllowPartialTestOnSqlServer> ret = dao.test_build_query_list(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<PeopleShardColModShardByDBOnSqlServer> ret = dao.test_build_queryPartialFieldsByHints_list(CityID, new DalHints().inAllShards());
		assertEquals(3, ret.size());
		assertNull(ret.get(0).getPeopleID());

		ret = dao.test_build_queryPartialFieldsByHints_list(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals(2, ret.size());
		assertNull(ret.get(0).getPeopleID());
	}

	@Test
	public void testtest_build_query_listByPage() throws Exception {
		//Integer CityID = null;// Test value here
		//List<ignoreMissingFieldsAndAllowPartialTestOnSqlServer> ret = dao.test_build_query_listByPage(CityID, 1, 10, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<PeopleShardColModShardByDBOnSqlServer> ret = dao.test_build_query_listByPage(CityID, 2, 1, new DalHints().inShard(0));
		assertEquals("Initial_Shard_02", ret.get(0).getName());

		ret = dao.test_build_query_listByPage(CityID, 1, 1, new DalHints().setShardColValue("CityID", 21));
		assertEquals("Initial_Shard_13", ret.get(0).getName());
	}

	@Test
	public void testtest_build_queryPartial_listByPage() throws Exception {
		//Integer CityID = null;// Test value here
		//List<ignoreMissingFieldsAndAllowPartialTestOnSqlServer> ret = dao.test_build_query_listByPage(CityID, 1, 10, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<PeopleShardColModShardByDBOnSqlServer> ret = dao.test_build_queryPartial_listByPage(CityID, 2, 1, new DalHints().inShard(0));
		assertEquals("Initial_Shard_02", ret.get(0).getName());
		assertNull(ret.get(0).getPeopleID());

		ret = dao.test_build_queryPartial_listByPage(CityID, 1, 1, new DalHints().setShardColValue("CityID", 21));
		assertEquals("Initial_Shard_13", ret.get(0).getName());
		assertNull(ret.get(0).getPeopleID());
	}

	@Test
	public void testtest_build_query_single() throws Exception {
		//Integer CityID = null;// Test value here
		//ignoreMissingFieldsAndAllowPartialTestOnSqlServer ret = dao.test_build_query_single(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		PeopleShardColModShardByDBOnSqlServer ret = dao.test_build_query_single(CityID, new DalHints().setShardColValue("CityID", 21));
		assertEquals("Initial_Shard_13", ret.getName());

		CityID.clear();
		CityID.add(40);
		CityID.add(41);
		ret = dao.test_build_query_single(CityID, new DalHints().inShard(0));
		assertNull(ret);

	}

	@Test
	public void testtest_build_queryPartial_single() throws Exception {
		//Integer CityID = null;// Test value here
		//ignoreMissingFieldsAndAllowPartialTestOnSqlServer ret = dao.test_build_query_single(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		PeopleShardColModShardByDBOnSqlServer ret = dao.test_build_queryPartial_single(CityID, new DalHints().setShardColValue("CityID", 21));
		assertEquals("Initial_Shard_13", ret.getName());
		assertNull(ret.getPeopleID());

		CityID.clear();
		CityID.add(40);
		CityID.add(41);
		ret = dao.test_build_queryPartial_single(CityID, new DalHints().inShard(0));
		assertNull(ret);

	}

	@Test
	public void testtest_build_query_fieldFirst() throws Exception {
		//Integer CityID = null;// Test value here
		//ignoreMissingFieldsAndAllowPartialTestOnSqlServer ret = dao.test_build_query_fieldFirst(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);

		String ret = dao.test_build_query_fieldFirst(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals("Initial_Shard_00", ret);

		ret = dao.test_build_query_fieldFirst(CityID, new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret);

		CityID.clear();
		CityID.add(40);
		CityID.add(41);
		ret = dao.test_build_query_fieldFirst(CityID, new DalHints().inShard(0));
		assertNull(ret);
	}

	@Test
	public void testtest_build_query_fieldList() throws Exception {
		//Integer CityID = null;// Test value here
		//List<String> ret = dao.test_build_query_fieldList(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<String> ret = dao.test_build_query_fieldList(CityID, new DalHints().inAllShards());
		assertEquals(3, ret.size());

		ret = dao.test_build_query_fieldList(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals(2, ret.size());

		CityID.clear();
		CityID.add(40);
		CityID.add(41);
		ret = dao.test_build_query_fieldList(CityID, new DalHints().inAllShards());
		assertEquals(0, ret.size());
	}

	@Test
	public void testtest_build_query_fieldListByPage() throws Exception {
		//Integer CityID = null;// Test value here
		//List<String> ret = dao.test_build_query_fieldListByPage(CityID, 1, 10, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(21);
		CityID.add(22);
		CityID.add(25);
		CityID.add(24);

		List<String> ret = dao.test_build_query_fieldListByPage(CityID, 2, 2, new DalHints().inShard(0));
		assertEquals("Initial_Shard_00", ret.get(0));

		ret = dao.test_build_query_fieldListByPage(CityID, 1, 500, new DalHints().inShard(0));
		assertEquals(3, ret.size());

//	    for(int i=0;i<ret.size();i++)
//	    	System.out
//					.println("1:"+ret.get(i));

		ret = dao.test_build_query_fieldListByPage(CityID, 1, 3, new DalHints().setShardColValue("CityID", 21));
		assertEquals("Initial_Shard_15", ret.get(0));

//	    for(int i=0;i<ret.size();i++)
//	    	System.out
//					.println("2:"+ret.get(i));
	}


	@Test
	public void testtest_build_query_fieldSingle() throws Exception {
		//Integer CityID = null;// Test value here
		//String ret = dao.test_build_query_fieldSingle(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		String ret = dao.test_build_query_fieldSingle(CityID, new DalHints().setShardColValue("CityID", 21));
		assertEquals("Initial_Shard_13", ret);

		CityID.clear();
		CityID.add(40);
		CityID.add(41);
		ret = dao.test_build_query_fieldSingle(CityID, new DalHints().inAllShards());
		assertNull(ret);
	}

	@Test
	public void testtest_def_upate() throws Exception {
		//String Name = "";// Test value here
		//Integer CityID = 1;// Test value here
		//int ret = dao.test_def_upate(Name, CityID, new DalHints());
		String Name = "def_update";// Test value here
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
//		int ret = dao.test_def_update_in(Name, Age, new DalHints().inAllShards());
		int ret = dao.test_def_update(Name, CityID, new DalHints().inShard(0));
//	    assertEquals(2, ret);

		PeopleShardColModShardByDBOnSqlServer pojo=dao.queryByPk(1, new DalHints().inShard(0));
		assertEquals("def_update", pojo.getName());

		pojo=dao.queryByPk(2, new DalHints().inShard(0));
		assertEquals("def_update", pojo.getName());

		pojo=dao.queryByPk(2, new DalHints().inShard(1));
		assertEquals("Initial_Shard_13", pojo.getName());
	}

	@Test
	public void testtest_def_truncate() throws Exception {
		//int ret = dao.test_def_truncate(new DalHints());
		dao.test_def_truncate(new DalHints().inAllShards());

		int count=dao.count(new DalHints().inAllShards());
		assertEquals(0, count);
	}

	@Test
	public void testtest_def_query_listFirst() throws Exception {
		//Integer CityID = 1;// Test value here
		//DefPojo ret = dao.test_def_query_listFirst(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		PeopleShardColModShardByDBOnSqlServer ret = dao.test_def_query_listFirst(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals("Initial_Shard_00", ret.getName());

		ret = dao.test_def_query_listFirst(CityID, new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret.getName());

		CityID.clear();
		CityID.add(40);
		CityID.add(41);

		ret = dao.test_def_query_listFirst(CityID, new DalHints().inShard(1));
		assertNull(ret);
	}

	@Test
	public void testtest_def_queryPartialSet_listFirst() throws Exception {
		//Integer CityID = 1;// Test value here
		//DefPojo ret = dao.test_def_query_listFirst(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		PeopleShardColModShardByDBOnSqlServer ret = dao.test_def_queryPartialSet_listFirst(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals("Initial_Shard_00", ret.getName());
		assertEquals(20, ret.getCityID().intValue());
		assertNull(ret.getCountryID());
		assertNull(ret.getPeopleID());
		assertNull(ret.getProvinceID());

		ret = dao.test_def_queryPartialSet_listFirst(CityID, new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret.getName());
		assertEquals(20, ret.getCityID().intValue());
		assertNull(ret.getCountryID());
		assertNull(ret.getPeopleID());
		assertNull(ret.getProvinceID());

		CityID.clear();
		CityID.add(40);
		CityID.add(41);

		ret = dao.test_def_queryPartialSet_listFirst(CityID, new DalHints().inShard(1));
		assertNull(ret);
	}

	@Test
	public void testtest_def_queryPartialStrings_listFirst() throws Exception {
		//Integer CityID = 1;// Test value here
		//DefPojo ret = dao.test_def_query_listFirst(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(21);
		CityID.add(22);

		PeopleShardColModShardByDBOnSqlServer ret = dao.test_def_queryPartialStrings_listFirst(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals("Initial_Shard_00", ret.getName());
		assertEquals(20, ret.getCityID().intValue());
		assertNull(ret.getCountryID());
		assertNull(ret.getPeopleID());
		assertNull(ret.getProvinceID());

		ret = dao.test_def_queryPartialStrings_listFirst(CityID, new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret.getName());
		assertEquals(20, ret.getCityID().intValue());
		assertNull(ret.getCountryID());
		assertNull(ret.getPeopleID());
		assertNull(ret.getProvinceID());

		CityID.clear();
		CityID.add(40);
		CityID.add(41);

		ret = dao.test_def_queryPartialStrings_listFirst(CityID, new DalHints().inShard(1));
		assertNull(ret);
	}

	@Test
	public void testtest_def_query_list() throws Exception {
		//Integer CityID = 1;// Test value here
		//List<DefPojo> ret = dao.test_def_query_list(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
//		List<ignoreMissingFieldsAndAllowPartialTestOnSqlServer> ret = dao.test_def_query_list(CityID, new DalHints().inAllShards());
//	    assertEquals(3, ret.size());

		List<PeopleShardColModShardByDBOnSqlServer> ret = dao.test_def_query_list(CityID, new DalHints().inShard(1));
		assertEquals(1, ret.size());

		ret = dao.test_def_query_list(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals(2, ret.size());

		CityID.clear();
		CityID.add(40);
		CityID.add(41);

		ret = dao.test_def_query_list(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals(0, ret.size());
	}

	@Test
	public void testtest_def_queryPartialSet_list() throws Exception {
		//Integer CityID = 1;// Test value here
		//List<DefPojo> ret = dao.test_def_query_list(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
//		List<ignoreMissingFieldsAndAllowPartialTestOnSqlServer> ret = dao.test_def_query_list(CityID, new DalHints().inAllShards());
//	    assertEquals(3, ret.size());

		List<PeopleShardColModShardByDBOnSqlServer> ret = dao.test_def_queryPartialSet_list(CityID, new DalHints().inShard(1));
		assertEquals(1, ret.size());
		assertEquals(23, ret.get(0).getCityID().intValue());
		assertEquals("Initial_Shard_13", ret.get(0).getName());
		assertNull(ret.get(0).getCountryID());
		assertNull(ret.get(0).getPeopleID());
		assertNull(ret.get(0).getProvinceID());


		ret = dao.test_def_queryPartialSet_list(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals(2, ret.size());

		assertEquals(20, ret.get(0).getCityID().intValue());
		assertEquals("Initial_Shard_00", ret.get(0).getName());
		assertNull(ret.get(0).getCountryID());
		assertNull(ret.get(0).getPeopleID());
		assertNull(ret.get(0).getProvinceID());

		assertEquals(22, ret.get(1).getCityID().intValue());
		assertEquals("Initial_Shard_02", ret.get(1).getName());
		assertNull(ret.get(1).getCountryID());
		assertNull(ret.get(1).getPeopleID());
		assertNull(ret.get(1).getProvinceID());

		ret = dao.test_def_queryPartialSet_list(CityID, new DalHints().inAllShards());
		assertEquals(3, ret.size());

		assertEquals(20, ret.get(0).getCityID().intValue());
		assertEquals("Initial_Shard_00", ret.get(0).getName());
		assertNull(ret.get(0).getCountryID());
		assertNull(ret.get(0).getPeopleID());
		assertNull(ret.get(0).getProvinceID());

		assertEquals(22, ret.get(1).getCityID().intValue());
		assertEquals("Initial_Shard_02", ret.get(1).getName());
		assertNull(ret.get(1).getCountryID());
		assertNull(ret.get(1).getPeopleID());
		assertNull(ret.get(1).getProvinceID());

		assertEquals(23, ret.get(2).getCityID().intValue());
		assertEquals("Initial_Shard_13", ret.get(2).getName());
		assertNull(ret.get(2).getCountryID());
		assertNull(ret.get(2).getPeopleID());
		assertNull(ret.get(2).getProvinceID());

		CityID.clear();
		CityID.add(40);
		CityID.add(41);

		ret = dao.test_def_queryPartialSet_list(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals(0, ret.size());
	}

	@Test
	public void testtest_def_queryPartialStrings_list() throws Exception {
		//Integer CityID = 1;// Test value here
		//List<DefPojo> ret = dao.test_def_query_list(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
//		List<ignoreMissingFieldsAndAllowPartialTestOnSqlServer> ret = dao.test_def_query_list(CityID, new DalHints().inAllShards());
//	    assertEquals(3, ret.size());

		List<PeopleShardColModShardByDBOnSqlServer> ret = dao.test_def_queryPartialStrings_list(CityID, new DalHints().inShard(1));
		assertEquals(1, ret.size());
		assertEquals(23, ret.get(0).getCityID().intValue());
		assertEquals("Initial_Shard_13", ret.get(0).getName());
		assertNull(ret.get(0).getCountryID());
		assertNull(ret.get(0).getPeopleID());
		assertNull(ret.get(0).getProvinceID());


		ret = dao.test_def_queryPartialStrings_list(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals(2, ret.size());

		assertEquals(20, ret.get(0).getCityID().intValue());
		assertEquals("Initial_Shard_00", ret.get(0).getName());
		assertNull(ret.get(0).getCountryID());
		assertNull(ret.get(0).getPeopleID());
		assertNull(ret.get(0).getProvinceID());

		assertEquals(22, ret.get(1).getCityID().intValue());
		assertEquals("Initial_Shard_02", ret.get(1).getName());
		assertNull(ret.get(1).getCountryID());
		assertNull(ret.get(1).getPeopleID());
		assertNull(ret.get(1).getProvinceID());

		ret = dao.test_def_queryPartialStrings_list(CityID, new DalHints().inAllShards());
		assertEquals(3, ret.size());

		assertEquals(20, ret.get(0).getCityID().intValue());
		assertEquals("Initial_Shard_00", ret.get(0).getName());
		assertNull(ret.get(0).getCountryID());
		assertNull(ret.get(0).getPeopleID());
		assertNull(ret.get(0).getProvinceID());

		assertEquals(22, ret.get(1).getCityID().intValue());
		assertEquals("Initial_Shard_02", ret.get(1).getName());
		assertNull(ret.get(1).getCountryID());
		assertNull(ret.get(1).getPeopleID());
		assertNull(ret.get(1).getProvinceID());

		assertEquals(23, ret.get(2).getCityID().intValue());
		assertEquals("Initial_Shard_13", ret.get(2).getName());
		assertNull(ret.get(2).getCountryID());
		assertNull(ret.get(2).getPeopleID());
		assertNull(ret.get(2).getProvinceID());

		CityID.clear();
		CityID.add(40);
		CityID.add(41);

		ret = dao.test_def_queryPartialStrings_list(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals(0, ret.size());
	}

	@Test
	public void testtest_def_query_listByPage() throws Exception {
		//Integer CityID = 1;// Test value here
		//List<DefPojo> ret = dao.test_def_query_listByPage(CityID, 1, 10, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<PeopleShardColModShardByDBOnSqlServer> ret = dao.test_def_query_listByPage(CityID, 2, 1, new DalHints().inShard(0));
		assertEquals("Initial_Shard_02", ret.get(0).getName());

		ret = dao.test_def_query_listByPage(CityID, 1, 1, new DalHints().setShardColValue("CityID", 21));
		assertEquals("Initial_Shard_13", ret.get(0).getName());
	}

	@Test
	public void testtest_def_queryPartialSet_listByPage() throws Exception {
		//Integer CityID = 1;// Test value here
		//List<DefPojo> ret = dao.test_def_query_listByPage(CityID, 1, 10, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<PeopleShardColModShardByDBOnSqlServer> ret = dao.test_def_queryPartialSet_listByPage(CityID, 2, 1, new DalHints().inShard(0));
		assertEquals("Initial_Shard_02", ret.get(0).getName());
		assertEquals(22, ret.get(0).getCityID().intValue());
		assertNull(ret.get(0).getCountryID());
		assertNull(ret.get(0).getPeopleID());
		assertNull(ret.get(0).getProvinceID());

		ret = dao.test_def_queryPartialSet_listByPage(CityID, 1, 1, new DalHints().setShardColValue("CityID", 21));
		assertEquals("Initial_Shard_13", ret.get(0).getName());
		assertEquals(23, ret.get(0).getCityID().intValue());
		assertNull(ret.get(0).getCountryID());
		assertNull(ret.get(0).getPeopleID());
		assertNull(ret.get(0).getProvinceID());
	}

	@Test
	public void testtest_def_queryPartialStrings_listByPage() throws Exception {
		//Integer CityID = 1;// Test value here
		//List<DefPojo> ret = dao.test_def_query_listByPage(CityID, 1, 10, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<PeopleShardColModShardByDBOnSqlServer> ret = dao.test_def_queryPartialStrings_listByPage(CityID, 2, 1, new DalHints().inShard(0));
		assertEquals("Initial_Shard_02", ret.get(0).getName());
		assertEquals(22, ret.get(0).getCityID().intValue());
		assertNull(ret.get(0).getCountryID());
		assertNull(ret.get(0).getPeopleID());
		assertNull(ret.get(0).getProvinceID());

		ret = dao.test_def_queryPartialStrings_listByPage(CityID, 1, 1, new DalHints().setShardColValue("CityID", 21));
		assertEquals("Initial_Shard_13", ret.get(0).getName());
		assertEquals(23, ret.get(0).getCityID().intValue());
		assertNull(ret.get(0).getCountryID());
		assertNull(ret.get(0).getPeopleID());
		assertNull(ret.get(0).getProvinceID());
	}

	@Test
	public void testtest_def_query_listSingle() throws Exception {
		//Integer CityID = 1;// Test value here
		//DefPojo ret = dao.test_def_query_listSingle(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		PeopleShardColModShardByDBOnSqlServer ret = dao.test_def_query_listSingle(CityID, new DalHints().setShardColValue("CityID", 21));
		assertEquals("Initial_Shard_13", ret.getName());

		CityID.clear();
		CityID.add(40);
		CityID.add(41);
		ret = dao.test_def_query_listSingle(CityID, new DalHints().inShard(0));
		assertNull(ret);
	}

	@Test
	public void testtest_def_queryPartialSet_listSingle() throws Exception {
		//Integer CityID = 1;// Test value here
		//DefPojo ret = dao.test_def_query_listSingle(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		PeopleShardColModShardByDBOnSqlServer ret = dao.test_def_queryPartialSet_listSingle(CityID, new DalHints().setShardColValue("CityID", 21));
		assertEquals("Initial_Shard_13", ret.getName());
		assertEquals(23, ret.getCityID().intValue());
		assertNull(ret.getCountryID());
		assertNull(ret.getPeopleID());
		assertNull(ret.getProvinceID());

		CityID.clear();
		CityID.add(10);
		CityID.add(12);
		CityID.add(23);
		ret = dao.test_def_queryPartialSet_listSingle(CityID, new DalHints().inAllShards());
		assertEquals("Initial_Shard_13", ret.getName());
		assertEquals(23, ret.getCityID().intValue());
		assertNull(ret.getCountryID());
		assertNull(ret.getPeopleID());
		assertNull(ret.getProvinceID());

		CityID.clear();
		CityID.add(40);
		CityID.add(41);
		ret = dao.test_def_queryPartialSet_listSingle(CityID, new DalHints().inShard(0));
		assertNull(ret);

	}

	@Test
	public void testtest_def_queryPartialStrings_listSingle() throws Exception {
		//Integer CityID = 1;// Test value here
		//DefPojo ret = dao.test_def_query_listSingle(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		PeopleShardColModShardByDBOnSqlServer ret = dao.test_def_queryPartialStrings_listSingle(CityID, new DalHints().setShardColValue("CityID", 21));
		assertEquals("Initial_Shard_13", ret.getName());
		assertEquals(23, ret.getCityID().intValue());
		assertNull(ret.getCountryID());
		assertNull(ret.getPeopleID());
		assertNull(ret.getProvinceID());

		CityID.clear();
		CityID.add(10);
		CityID.add(12);
		CityID.add(23);
		ret = dao.test_def_queryPartialStrings_listSingle(CityID, new DalHints().inAllShards());
		assertEquals("Initial_Shard_13", ret.getName());
		assertEquals(23, ret.getCityID().intValue());
		assertNull(ret.getCountryID());
		assertNull(ret.getPeopleID());
		assertNull(ret.getProvinceID());

		CityID.clear();
		CityID.add(40);
		CityID.add(41);
		ret = dao.test_def_queryPartialStrings_listSingle(CityID, new DalHints().inShard(0));
		assertNull(ret);

	}

	@Test
	public void testtest_def_query_fieldFirst() throws Exception {
		//Integer CityID = 1;// Test value here
		//String ret = dao.test_def_query_fieldFirst(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);

		String ret = dao.test_def_query_fieldFirst(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals("Initial_Shard_00", ret);

		ret = dao.test_def_query_fieldFirst(CityID, new DalHints().inAllShards());
		assertEquals("Initial_Shard_00", ret);

		ret = dao.test_def_query_fieldFirst(CityID, new DalHints().shardBy("CityID"));
		assertEquals("Initial_Shard_00", ret);

		CityID.clear();
		CityID.add(40);
		CityID.add(41);
		ret = dao.test_def_query_fieldFirst(CityID, new DalHints().inShard(0));
		assertNull(ret);
	}

	@Test
	public void testtest_def_query_fieldList() throws Exception {
		//Integer CityID = 1;// Test value here
		//List<String> ret = dao.test_def_query_fieldList(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<String> ret = dao.test_def_query_fieldList(CityID, new DalHints().setShardColValue("CityID", 21));
		assertEquals(1, ret.size());

		ret = dao.test_def_query_fieldList(CityID, new DalHints().setShardColValue("CityID", 20));
		assertEquals(2, ret.size());

		ret = dao.test_def_query_fieldList(CityID, new DalHints().shardBy("CityID"));
		assertEquals(3, ret.size());

		CityID.clear();
		CityID.add(40);
		CityID.add(41);
		ret = dao.test_def_query_fieldList(CityID, new DalHints().inShard(0));
		assertEquals(0, ret.size());
	}

	@Test
	public void testtest_def_query_fieldListByPage() throws Exception {
		//Integer CityID = 1;// Test value here
		//List<String> ret = dao.test_def_query_fieldListByPage(CityID, 1, 10, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		List<String> ret = dao.test_def_query_fieldListByPage(CityID, 2, 1, new DalHints().inShard(0));
		assertEquals("Initial_Shard_00", ret.get(0));

		ret = dao.test_def_query_fieldListByPage(CityID, 1, 1, new DalHints().setShardColValue("CityID", 21));
		assertEquals("Initial_Shard_13", ret.get(0));

		ret = dao.test_def_query_fieldListByPage(CityID, 1, 1, new DalHints().shardBy("CityID"));
		assertEquals("Initial_Shard_02", ret.get(0));
	}

	@Test
	public void testtest_def_query_fieldSingle() throws Exception {
		//Integer CityID = 1;// Test value here
		//String ret = dao.test_def_query_fieldSingle(CityID, new DalHints());
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(23);
		CityID.add(22);
		String ret = dao.test_def_query_fieldSingle(CityID, new DalHints().setShardColValue("CityID", 21));
		assertEquals("Initial_Shard_13", ret);

		CityID.clear();
		CityID.add(40);
		CityID.add(41);
		ret = dao.test_def_query_fieldSingle(CityID, new DalHints().setShardColValue("CityID", 21));
		assertNull(ret);
	}

	private class IntegerComparator implements Comparator<Integer>{
		@Override
		public int compare(Integer o1, Integer o2) {
			return new Integer(o2.compareTo(o1));
		}
	}

	@Test
	/**
	 * queryDao.query("select top 100 * from People", parameters, hints, ignoreMissingFieldsAndAllowPartialTestOnSqlServer.class)
	 **/
	public void testtest_def_top() throws Exception {
		List<PeopleShardColModShardByDBOnSqlServer> ret=dao.test_def_top(new DalHints().inShard(0));
		assertEquals(6, ret.size());
	}


	@Test
	/**
	 * count
	 **/
	public void testtest_def_count() throws Exception {

		DalHints hints = new DalHints().mergeBy(new ResultMerger.IntSummary());
		int ret=dao.test_def_count(hints.inAllShards());
		assertEquals(12, ret);

	}



	@Test
	/**
	 * max
	 **/
	public void testtest_def_max() throws Exception {
		int ret=dao.test_def_queryMax(new DalHints().inAllShards().sortBy(new IntegerComparator()));

		assertEquals(31, ret);
	}

	@Test
	public void test_queryMultipleAllShards() throws SQLException {

		List ret=dao.queryListMultipleAllShards(new DalHints().inAllShards());
//	   assertEquals(16, ret.size());

		List<PeopleShardColModShardByDBOnSqlServer> ret1=(List<PeopleShardColModShardByDBOnSqlServer>) ret.get(0);
		assertEquals(12, ret1.size());
		for(int i=0;i<ret1.size();i++)
			System.out.println("sqlList0:"+ret1.get(i).getName());

		ret1=(List<PeopleShardColModShardByDBOnSqlServer>) ret.get(1);
		assertEquals(12, ret1.size());
		for(int i=0;i<ret1.size();i++)
			System.out.println("sqlList1:"+ret1.get(i).getName());

		ret1=(List<PeopleShardColModShardByDBOnSqlServer>) ret.get(2);
		assertEquals(12, ret1.size());
		for(int i=0;i<ret1.size();i++)
			System.out.println("sqlList2:"+ret1.get(i).getName());

		ret1=(List<PeopleShardColModShardByDBOnSqlServer>) ret.get(3);
		assertEquals(12, ret1.size());
		for(int i=0;i<ret1.size();i++)
			System.out.println("sqlList3:"+ret1.get(i).getName());

		PeopleShardColModShardByDBOnSqlServer retx =(PeopleShardColModShardByDBOnSqlServer) ret.get(4);
		assertNotNull(retx);
		System.out.println("sqlList4:"+retx.getName());

		ret1=(List<PeopleShardColModShardByDBOnSqlServer>) ret.get(5);
		assertEquals(0, ret1.size());
		for(int i=0;i<ret1.size();i++)
			System.out.println("sqlList5:"+ret1.get(i).getName());

		ret1=(List<PeopleShardColModShardByDBOnSqlServer>) ret.get(6);
		assertEquals(12, ret1.size());
		for(int i=0;i<ret1.size();i++)
			System.out.println("sqlList6:"+ret1.get(i).getName());

		ret1=(List<PeopleShardColModShardByDBOnSqlServer>) ret.get(7);
		assertEquals(12, ret1.size());
		for(int i=0;i<ret1.size();i++)
			System.out.println("sqlList7:"+ret1.get(i).getName());

		List<Integer> count1=(List<Integer>) ret.get(8);
		assertEquals(2, count1.size());
		for(int i=0;i<count1.size();i++)
			System.out.println("sqlCount8:"+count1.get(i).intValue());

		List<Integer> count2=(List<Integer>) ret.get(9);
		assertEquals(2, count2.size());
		for(int i=0;i<count2.size();i++)
			System.out.println("sqlCount9:"+count2.get(i).intValue());

		List<Integer> count3=(List<Integer>) ret.get(10);
		assertEquals(2, count3.size());
		for(int i=0;i<count3.size();i++)
			System.out.println("sqlCount10:"+count3.get(i).intValue());

		List<Integer> count4=(List<Integer>) ret.get(11);
		assertEquals(2, count4.size());
		for(int i=0;i<count4.size();i++)
			System.out.println("sqlCount11:"+count4.get(i).intValue());

		List<Integer> count5=(List<Integer>) ret.get(12);
		assertEquals(2, count5.size());
		for(int i=0;i<count5.size();i++)
			System.out.println("sqlCount12:"+count5.get(i).intValue());

		List<Integer> count6=(List<Integer>) ret.get(13);
		assertEquals(0, count6.size());

		List<Integer> count7=(List<Integer>) ret.get(14);
		assertEquals(2, count7.size());
		for(int i=0;i<count7.size();i++)
			System.out.println("sqlCount14:"+count7.get(i).intValue());

		List<String> field1=(List<String>) ret.get(15);
		assertEquals(12, field1.size());
		for(int i=0;i<field1.size();i++)
			System.out.println("sqlFieldList15:"+field1.get(i).toString());

		List<String> field2=(List<String>) ret.get(16);
		assertEquals(12, field2.size());
		for(int i=0;i<field2.size();i++)
			System.out.println("sqlFieldList16:"+field2.get(i).toString());

		List<String> field3=(List<String>) ret.get(17);
		assertEquals(12, field3.size());
		for(int i=0;i<field3.size();i++)
			System.out.println("sqlFieldList17:"+field3.get(i).toString());

		List<String> field4=(List<String>) ret.get(18);
		assertEquals(12, field4.size());
		for(int i=0;i<field4.size();i++)
			System.out.println("sqlFieldList18:"+field4.get(i).toString());

		String field5=(String) ret.get(19);
		assertNotNull(field5);
		System.out.println("sqlFieldList19:"+field5.toString());

		List<String> field6=(List<String>) ret.get(20);
		assertEquals(0, field6.size());

		List<String> field7=(List<String>) ret.get(21);
		assertEquals(12, field7.size());
		for(int i=0;i<field7.size();i++)
			System.out.println("sqlFieldList21:"+field7.get(i).toString());

		List<String> field8=(List<String>) ret.get(22);
		assertEquals(12, field8.size());
		for(int i=0;i<field8.size();i++)
			System.out.println("sqlFieldList22:"+field8.get(i).toString());

		List<PeopleShardColModShardByDBOnSqlServer> first1=(List<PeopleShardColModShardByDBOnSqlServer>) ret.get(23);
		assertEquals(2, first1.size());
		for(int i=0;i<first1.size();i++)
			System.out.println("sqlFirst23:"+first1.get(i).getName());

		List< PeopleShardColModShardByDBOnSqlServer> first2=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(24);
		assertEquals(2, first2.size());
		for(int i=0;i<first2.size();i++)
			System.out.println("sqlFirst24:"+first2.get(i).getName());

		List< PeopleShardColModShardByDBOnSqlServer> first3=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(25);
		assertEquals(2, first3.size());
		for(int i=0;i<first3.size();i++)
			System.out.println("sqlFirst25:"+first3.get(i).getName());

		List< PeopleShardColModShardByDBOnSqlServer> first4=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(26);
		assertEquals(2, first4.size());
		for(int i=0;i<first4.size();i++)
			System.out.println("sqlFirst26:"+first4.get(i).getName());

		PeopleShardColModShardByDBOnSqlServer first5=(PeopleShardColModShardByDBOnSqlServer) ret.get(27);
		assertNotNull(first5.getName());
		System.out.println("sqlFirst27:"+first5.getName());

		List< PeopleShardColModShardByDBOnSqlServer> first6=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(28);
		assertEquals(0, first6.size());

		List< PeopleShardColModShardByDBOnSqlServer> first7=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(29);
		assertEquals(2, first7.size());
		for(int i=0;i<first7.size();i++)
			System.out.println("sqlFirst29:"+first7.get(i).getName());

		List< PeopleShardColModShardByDBOnSqlServer> first8=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(30);
		assertEquals(2, first8.size());
		for(int i=0;i<first8.size();i++)
			System.out.println("sqlFirst30:"+first8.get(i).getName());

		List<PeopleShardColModShardByDBOnSqlServer> object1=(List<PeopleShardColModShardByDBOnSqlServer>) ret.get(31);
		assertEquals(1, object1.size());
		for(int i=0;i<object1.size();i++)
			System.out.println("sqlObject31:"+object1.get(i).getName());

		List< PeopleShardColModShardByDBOnSqlServer> object2=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(32);
		assertEquals(1, object2.size());
		for(int i=0;i<object2.size();i++)
			System.out.println("sqlObject32:"+object2.get(i).getName());

		List< PeopleShardColModShardByDBOnSqlServer> object3=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(33);
		assertEquals(1, object3.size());
		for(int i=0;i<object3.size();i++)
			System.out.println("sqlObject33:"+object3.get(i).getName());

		List< PeopleShardColModShardByDBOnSqlServer> object4=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(34);
		assertEquals(1, object4.size());
		for(int i=0;i<object4.size();i++)
			System.out.println("sqlObject34:"+object4.get(i).getName());

		PeopleShardColModShardByDBOnSqlServer object5=(PeopleShardColModShardByDBOnSqlServer) ret.get(35);
		assertEquals("Initial_Shard_11", object5.getName());
		System.out.println("sqlObject35:"+object5.getName());

		List< PeopleShardColModShardByDBOnSqlServer> object6=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(36);
		assertEquals(0, object6.size());

		List< PeopleShardColModShardByDBOnSqlServer> object7=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(37);
		assertEquals(1, object7.size());
		for(int i=0;i<object7.size();i++)
			System.out.println("sqlObject37:"+object7.get(i).getName());

		List< PeopleShardColModShardByDBOnSqlServer> object8=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(38);
		assertEquals(1, object8.size());
		for(int i=0;i<object8.size();i++)
			System.out.println("sqlObject38:"+object8.get(i).getName());

		List<PeopleShardColModShardByDBOnSqlServer> inRet1=(List<PeopleShardColModShardByDBOnSqlServer>) ret.get(39);
		assertEquals(3, inRet1.size());
		for(int i=0;i<inRet1.size();i++)
			System.out.println("sqlInParam39:"+inRet1.get(i).getName());

		List< PeopleShardColModShardByDBOnSqlServer> inRet2=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(40);
		assertEquals(3, inRet2.size());
		for(int i=0;i<inRet2.size();i++)
			System.out.println("sqlInParam40:"+inRet2.get(i).getName());

		List< PeopleShardColModShardByDBOnSqlServer> inRet3=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(41);
		assertEquals(3, inRet3.size());
		for(int i=0;i<inRet3.size();i++)
			System.out.println("sqlInParam41:"+inRet3.get(i).getName());

		List< PeopleShardColModShardByDBOnSqlServer> inRet4=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(42);
		assertEquals(3, inRet4.size());
		for(int i=0;i<inRet4.size();i++)
			System.out.println("sqlInParam42:"+inRet4.get(i).getName());

		PeopleShardColModShardByDBOnSqlServer inRet5=(PeopleShardColModShardByDBOnSqlServer) ret.get(43);
		assertNotNull(inRet5.getName());
		System.out.println("sqlInParam43:"+inRet5.getName());

		List< PeopleShardColModShardByDBOnSqlServer> inRet6=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(44);
		assertEquals(0, inRet6.size());

		List< PeopleShardColModShardByDBOnSqlServer> inRet7=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(45);
		assertEquals(3, inRet7.size());
		for(int i=0;i<inRet7.size();i++)
			System.out.println("sqlInParam45:"+inRet7.get(i).getName());

		List< PeopleShardColModShardByDBOnSqlServer> inRet8=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(46);
		assertEquals(3, inRet8.size());
		for(int i=0;i<inRet8.size();i++)
			System.out.println("sqlInParam46:"+inRet8.get(i).getName());

		List<PeopleShardColModShardByDBOnSqlServer> noRet1=(List<PeopleShardColModShardByDBOnSqlServer>) ret.get(47);
		assertEquals(0, noRet1.size());

		List< PeopleShardColModShardByDBOnSqlServer> noRet2=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(48);
		assertEquals(0, noRet2.size());

		List< PeopleShardColModShardByDBOnSqlServer> noRet3=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(49);
		assertEquals(0, noRet3.size());

		List< PeopleShardColModShardByDBOnSqlServer> noRet4=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(50);
		assertEquals(0, noRet4.size());

		PeopleShardColModShardByDBOnSqlServer noRet5=(PeopleShardColModShardByDBOnSqlServer) ret.get(51);
		assertNull(noRet5);

		List< PeopleShardColModShardByDBOnSqlServer> noRet6=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(52);
		assertEquals(0, noRet6.size());

		List< PeopleShardColModShardByDBOnSqlServer> noRet7=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(53);
		assertEquals(0, noRet7.size());

		List< PeopleShardColModShardByDBOnSqlServer> noRet8=(List< PeopleShardColModShardByDBOnSqlServer>) ret.get(54);
		assertEquals(0, noRet8.size());

		List<Integer> count8=(List<Integer>) ret.get(55);
		assertEquals(2, count8.size());
		for(int i=0;i<count8.size();i++)
			System.out.println("sqlCount55:"+count8.get(i).intValue());
	}

	@Test
	public void testTransPass() throws Exception {
		DalCommand command = new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				PeopleShardColModShardByDBOnSqlServer ret = dao.queryByPk(1,
						new DalHints().inShard(0));
				ret.setCityID(1000);
				dao.update(new DalHints().inShard(0), ret);
				return true;
			}
		};
		try {
			client.execute(command, new DalHints().inShard(0));
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(1000, dao.queryByPk(1l, new DalHints().inShard(0)).getCityID().intValue());
	}

	@Test
	public void testTransFail() throws Exception{
		DalCommand command = new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				PeopleShardColModShardByDBOnSqlServer pojo=new PeopleShardColModShardByDBOnSqlServer();
				pojo.setPeopleID(2l);
				dao.delete(new DalHints().inShard(0), pojo);
				PeopleShardColModShardByDBOnSqlServer ret = dao.queryByPk(1,
						new DalHints().inShard(3));
				ret.setCityID(2000);
				dao.update(new DalHints().inShard(6), ret);//不存在shardid 6
				dao.insert(new DalHints().inShard(0), pojo);
				return true;
			}
		};
		try {
			client.execute(command, new DalHints().inShard(0));
			fail();
		} catch (Exception e) {
//			e.printStackTrace();
		}

		assertEquals(22, dao.queryByPk(2, new DalHints().inShard(0)).getCityID().intValue());
		assertEquals(6, dao.count(new DalHints().inShard(0)));
	}

	@Test
	public void testTransCommandsPass() throws Exception {
		List<DalCommand> cmds = new LinkedList<DalCommand>();
		cmds.add(new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				PeopleShardColModShardByDBOnSqlServer ret = dao.queryByPk(1,
						new DalHints().inShard(0));
				ret.setCityID(1000);
				dao.update(new DalHints().inShard(0), ret);
				return true;
			}
		});
		cmds.add(new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				PeopleShardColModShardByDBOnSqlServer pojo=new PeopleShardColModShardByDBOnSqlServer();
				pojo.setPeopleID(2l);
				dao.delete(new DalHints().inShard(0), pojo);
				PeopleShardColModShardByDBOnSqlServer ret = dao.queryByPk(3,
						new DalHints().inShard(0));
				ret.setCityID(2000);
				dao.update(new DalHints().inShard(0), ret);
//				pojo.setPeopleID(4l);
				pojo.setCityID(100);
				pojo.setName("trans");
//				pojo.setProvinceID(200);
//				pojo.setCountryID(300);
				dao.insert(new DalHints().inShard(0), pojo);
				return true;
			}
		});

		try {
			client.execute(cmds, new DalHints().inShard(0));

		} catch (Exception e) {

			e.printStackTrace();
		}
		assertEquals(1000, dao.queryByPk(1, new DalHints().inShard(0)).getCityID().intValue());
		assertEquals(2000, dao.queryByPk(3, new DalHints().inShard(0)).getCityID().intValue());
		assertEquals(100, dao.queryByPk(7, new DalHints().inShard(0)).getCityID().intValue());
		assertEquals(6, dao.count(new DalHints().inShard(0)));
//		Thread.sleep(60000);
	}

	@Test
	public void testTransCommandsFail() throws Exception {
		List<DalCommand> cmds = new LinkedList<DalCommand>();
		cmds.add(new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				PeopleShardColModShardByDBOnSqlServer ret = dao.queryByPk(1,
						new DalHints().inShard(0));
				ret.setCityID(1000);
				dao.update(new DalHints().inShard(0), ret);
				return true;
			}
		});
		cmds.add(new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				PeopleShardColModShardByDBOnSqlServer pojo=new PeopleShardColModShardByDBOnSqlServer();
				pojo.setPeopleID(2l);
				dao.delete(new DalHints().inShard(0), pojo);
				PeopleShardColModShardByDBOnSqlServer ret = dao.queryByPk(3,
						new DalHints().inShard(0));
				ret.setCityID(2000);
				dao.update(new DalHints().inShard(6), ret);//不存在shardid 6
				dao.insert(new DalHints().inShard(0), pojo);
				return true;
			}
		});
		try {
			client.execute(cmds, new DalHints().inShard(0));
			fail();
		} catch (Exception e) {
//			e.printStackTrace();
		}
		assertEquals(20, dao.queryByPk(1, new DalHints().inShard(0)).getCityID().intValue());
		assertEquals(22, dao.queryByPk(2, new DalHints().inShard(0)).getCityID().intValue());
		assertEquals(24, dao.queryByPk(3, new DalHints().inShard(0)).getCityID().intValue());
		assertEquals(6, dao.count(new DalHints().inShard(0)));


	}
}
