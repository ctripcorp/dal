package shardtest.newVersionCodeTest;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;
import dao.shard.newVersionCode.NewHintsOfCodeGenOnSqlServerDao;
import entity.SqlServerPeopleTable;
import org.junit.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * JUnit test of PeopleGenDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class NewHintsOfCodeGenOnSqlServerDaoUnitTest {

	private static final String DATA_BASE = "SimpleShardByDBOnSqlServer";

	private static DalClient client = null;
	private static NewHintsOfCodeGenOnSqlServerDao dao = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		client = DalClientFactory.getClient(DATA_BASE);
		dao = new NewHintsOfCodeGenOnSqlServerDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
//		for(int i = 0; i < 10; i++) {
//			PeopleGen daoPojo = createPojo(i);
//
//			try {
//				dao.insert(new DalHints().enableIdentityInsert(), daoPojo);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
		dao.test_def_update(new DalHints().inShard(0));
		dao.test_def_update(new DalHints().inShard(1));

		List<SqlServerPeopleTable> daoPojos1 = new ArrayList<SqlServerPeopleTable>(
				3);
		for (int i = 0; i < 3; i++) {
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			//daoPojo.setPeopleID(Long.valueOf(i) + 1);
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
			//daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Initial_Shard_1" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojo.setCountryID(i + 40);
			daoPojos2.add(daoPojo);
		}
		dao.insert(new DalHints().inShard(1), daoPojos2);
	}
	
	private SqlServerPeopleTable createPojo(int index) {
		SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();

		//daoPojo.setId(index);
		//daoPojo set not null field
		
		return daoPojo;
	}

	private void changePojo(SqlServerPeopleTable daoPojo) {
		// Change a field to make pojo different with original one
	}
	
	private void changePojos(List<SqlServerPeopleTable> daoPojos) {
		for(SqlServerPeopleTable daoPojo: daoPojos)
			changePojo(daoPojo);
	}
	
	private void verifyPojo(SqlServerPeopleTable daoPojo) {
		//assert changed value
	}
	
	private void verifyPojos(List<SqlServerPeopleTable> daoPojos) {
		for(SqlServerPeopleTable daoPojo: daoPojos)
			verifyPojo(daoPojo);
	}
	
	@After
	public void tearDown() throws Exception {
//		dao.delete(null, dao.getAll(null));
//		dao.test_def_update(new DalHints().inShard(0));
//		dao.test_def_update(new DalHints().inShard(1));
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
//		PeopleGen daoPojo = createPojo(1);
//		int affected = dao.delete(hints, daoPojo); 
//		assertEquals(1, affected);
//	}
//	
//	@Test
//	public void testDelete2() throws Exception {
//		DalHints hints = new DalHints();
//		List<PeopleGen> daoPojos = dao.getAll(null);
//		int[] affected = dao.delete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//	}
//	
//	@Test
//	public void testBatchDelete() throws Exception {
//		DalHints hints = new DalHints();
//		List<PeopleGen> daoPojos = dao.getAll(null);
//		int[] affected = dao.batchDelete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//	}
//	
//	@Test
//	public void testGetAll() throws Exception {
//		List<PeopleGen> list = dao.getAll(new DalHints());
//		assertEquals(10, list.size());
//	}
//	
//	@Test
//	public void testInsert1() throws Exception {
//		DalHints hints = new DalHints();
//		PeopleGen daoPojo = createPojo(1);
//		int affected = dao.insert(hints, daoPojo);
//		assertEquals(1, affected);
//	}
//	
//	@Test
//	public void testInsert2() throws Exception {
//		DalHints hints = new DalHints();
//		List<SqlServerPeopleTable> daoPojos = dao.queryAll(new DalHints().inShard(0));
//		int[] affected = dao.insert(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//	}
//	
//	@Test
//	public void testInsert3() throws Exception {
//		DalHints hints = new DalHints();
//		KeyHolder keyHolder = new KeyHolder();
//		PeopleGen daoPojo = createPojo(1);
//		int affected = dao.insert(hints, keyHolder, daoPojo);
//		assertEquals(1, affected);
//		assertEquals(1, keyHolder.size());
//	}
//	
	@Test
	public void testInsert4() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<SqlServerPeopleTable> daoPojos = dao.queryAll(new DalHints().inShard(0));
		int[] affected = dao.insert(new DalHints().inShard(0), keyHolder, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
		assertEquals(3, keyHolder.size());
		List<Number> idlist=keyHolder.getIdList();
		for(int i=0;i<idlist.size();i++)
			System.out.println(idlist.get(i));
		
	}
//	
//	@Test
//	public void testInsert5() throws Exception {
//		DalHints hints = new DalHints();
//		List<PeopleGen> daoPojos = dao.getAll(new DalHints());
//		int[] affected = dao.batchInsert(hints, daoPojos);
//	}
//	
//	@Test
//	public void testQueryByPage() throws Exception {
//		DalHints hints = new DalHints();
//		int pageSize = 100;
//		int pageNo = 1;
//		List<PeopleGen> list = dao.queryByPage(pageSize, pageNo, hints);
//		assertEquals(10, list.size());
//	}
//	
//	@Test
//	public void testQueryByPk1() throws Exception {
//		Number id = 1;
//		DalHints hints = new DalHints();
//		PeopleGen affected = dao.queryByPk(id, hints);
//		assertNotNull(affected);
//	}
//	
//	@Test
//	public void testQueryByPk2() throws Exception {
//		PeopleGen pk = createPojo(1);
//		DalHints hints = new DalHints();
//		PeopleGen affected = dao.queryByPk(pk, hints);
//		assertNotNull(affected);
//	}
//	
//	@Test
//	public void testUpdate1() throws Exception {
//		DalHints hints = new DalHints();
//		PeopleGen daoPojo = dao.queryByPk(createPojo(1), hints);
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
//		List<PeopleGen> daoPojos = dao.getAll(new DalHints());
//		changePojos(daoPojos);
//		int[] affected = dao.update(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		verifyPojos(dao.getAll(new DalHints()));
//	}
//	
//	@Test
//	public void testBatchUpdate() throws Exception {
//		DalHints hints = new DalHints();
//		List<PeopleGen> daoPojos = dao.getAll(new DalHints());
//		changePojos(daoPojos);
//		int[] affected = dao.batchUpdate(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		verifyPojos(dao.getAll(new DalHints()));
//	}
	
	@Test
	public void testtest_build_delete_allShard_callback() throws Exception {
		//Integer CityID = null;// Test value here
	    //int ret = dao.test_build_delete_allShard_callback(CityID, new DalHints());
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
		//Integer CityID = null;// Test value here
	    //int ret = dao.test_build_delete_shards_async(CityID, new DalHints());
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
		//Integer CityID = null;// Test value here
		//String Name = "";// Test value here
		//Integer ProvinceID = null;// Test value here
		//Integer CountryID = null;// Test value here
	    //int ret = dao.test_build_insert_allShard_callback(CityID, Name, ProvinceID, CountryID, new DalHints());
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
		//Integer CityID = null;// Test value here
		//String Name = "";// Test value here
		//Integer ProvinceID = null;// Test value here
		//Integer CountryID = null;// Test value here
	    //int ret = dao.test_build_insert_shards_async(CityID, Name, ProvinceID, CountryID, new DalHints());
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
		//String Name = "";// Test value here
		//Integer CityID = null;// Test value here
	    //int ret = dao.test_build_update_allShard_callback(Name, CityID, new DalHints());
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
		//String Name = "";// Test value here
		//Integer CityID = null;// Test value here
	    //int ret = dao.test_build_update_shards_async(Name, CityID, new DalHints());
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
	public void testtest_build_query_allShard_callback() throws Exception {
		//Integer CityID = null;// Test value here
	    //List<PeopleGen> ret = dao.test_build_query_allShard_callback(CityID, new DalHints());
		Integer CityID = 20;// Test value here

		DalHints hints = new DalHints();

		DefaultResultCallback callback = new DefaultResultCallback();
		

		List<SqlServerPeopleTable> ret = dao
				.test_build_query_allShard_callback(CityID, hints, callback);

		assertNull(ret);
		
		callback.waitForDone();
		ret = (List<SqlServerPeopleTable>) callback.getResult(); // 异步返回结果

		assertEquals(2, ret.size());
	}

	@Test
	public void testtest_build_query_shards_async() throws Exception {
		//Integer CityID = null;// Test value here
	    //List<PeopleGen> ret = dao.test_build_query_shards_async(CityID, new DalHints());
		Integer CityID = 20;// Test value here

		DalHints hints = new DalHints();

		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");

		List<SqlServerPeopleTable> ret = dao
				.test_build_query_shards_async(CityID, hints, shards);

		assertNull(ret);
		Future<List<SqlServerPeopleTable>> fr = (Future<List<SqlServerPeopleTable>>) hints
				.getAsyncResult();
		ret = fr.get(); // 异步返回结果
		assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_def_update() throws Exception {
	    //int ret = dao.test_def_update(new DalHints());
	}
	
	@Test
	public void testtest_def_query_allShard_callback() throws Exception {
		//Integer CityID = 1;// Test value here
		//List<PojoPojo> ret = dao.test_def_query_allShard_callback(CityID, new DalHints());
		Integer CityID = 22;// Test value here
		DalHints hints = new DalHints();
		DefaultResultCallback callback = new DefaultResultCallback();
		List<SqlServerPeopleTable> ret = dao.test_def_query_allShard_callback(CityID, hints,callback);
        assertNull(ret);
		
		callback.waitForDone();
		ret = (List<SqlServerPeopleTable>) callback.getResult(); // 异步返回结果

		assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_def_query_shards_async() throws Exception {
		//Integer CityID = 1;// Test value here
		//List<PojoPojo> ret = dao.test_def_query_shards_async(CityID, new DalHints());
		Integer CityID = 22;// Test value here
		DalHints hints = new DalHints();
		
		 Set<String> shards = new HashSet<>();
			shards.add("0");
			shards.add("1");
			
		List<SqlServerPeopleTable> ret = dao.test_def_query_shards_async(CityID, hints,shards);
		assertNull(ret);
		Future<List<SqlServerPeopleTable>> fr = (Future<List<SqlServerPeopleTable>>) hints
				.getAsyncResult();
		ret = fr.get(); // 异步返回结果
		assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_def_query_shards_varchar() throws Exception {
		//Integer CityID = 1;// Test value here
		//List<PojoPojo> ret = dao.test_def_query_shards_async(CityID, new DalHints());
		String name ="hello";// Test value here
		DalHints hints = new DalHints();
		
		 Set<String> shards = new HashSet<>();
			shards.add("0");
			shards.add("1");
			
		List<SqlServerPeopleTable> ret = dao.test_def_query_shards_varchar(name, hints,shards);
		assertNull(ret);
		Future<List<SqlServerPeopleTable>> fr = (Future<List<SqlServerPeopleTable>>) hints
				.getAsyncResult();
		ret = fr.get(); // 异步返回结果
		assertEquals(0, ret.size());
	}

}
