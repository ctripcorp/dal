package shardTest.newVersionCodeTest;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import org.junit.*;

import static org.junit.Assert.*;


import com.ctrip.platform.dal.dao.*;

/**
 * JUnit test of PeopleGenDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class PeopleShardColModByDBTableOnSqlServerDaoUnitTest {

	private static final String DATA_BASE = "ShardColModByDBTableOnSqlServer";

	private static DalClient client = null;
	private static PeopleShardColModByDBTableOnSqlServerDao dao = null;
	
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
		dao = new PeopleShardColModByDBTableOnSqlServerDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
//		To prepare test data, you can simply uncomment the following.
//		In case of DB and table shard, please revise the code to reflect shard
//		for(int i = 0; i < 10; i++) {
//			PeopleGen daoPojo = createPojo(i);
//
//			try {
//				dao.insert(new DalHints().enableIdentityInsert(), daoPojo);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
		dao.truncate(new DalHints().inShard(0),"_0");
		dao.truncate(new DalHints().inShard(0),"_1");
		dao.truncate(new DalHints().inShard(1),"_0");
		dao.truncate(new DalHints().inShard(1),"_1");
	
		List<PeopleShardColModByDBTableOnSqlServer> daoPojos1 = new ArrayList<PeopleShardColModByDBTableOnSqlServer>(2);
		for (int i = 0; i < 6; i++) {
			PeopleShardColModByDBTableOnSqlServer daoPojo = new PeopleShardColModByDBTableOnSqlServer();
			daoPojo.setCityID(200);
			daoPojo.setProvinceID(i + 20);
			daoPojo.setName("InsertByfields_0fields_" + i);
			daoPojo.setCountryID(500);
			daoPojos1.add(daoPojo);
		}
	   dao.insert(new DalHints(), daoPojos1);
		
		List<PeopleShardColModByDBTableOnSqlServer> daoPojos2 = new ArrayList<PeopleShardColModByDBTableOnSqlServer>(2);
		for (int i = 0; i <6; i++) {
			PeopleShardColModByDBTableOnSqlServer daoPojo = new PeopleShardColModByDBTableOnSqlServer();
			daoPojo.setCityID(201);
			daoPojo.setProvinceID(i + 20);
			daoPojo.setName("InsertByfields_1fields_" + i);
			daoPojo.setCountryID(501);
			daoPojos2.add(daoPojo);
		}
	   dao.insert(new DalHints(), daoPojos2);
	}
	
	private PeopleShardColModByDBTableOnSqlServer createPojo(int index) {
		PeopleShardColModByDBTableOnSqlServer daoPojo = new PeopleShardColModByDBTableOnSqlServer();

		//daoPojo.setId(index);
		//daoPojo set not null field
		
		return daoPojo;
	}

	private void changePojo(PeopleShardColModByDBTableOnSqlServer daoPojo) {
		// Change a field to make pojo different with original one
	}
	
	private void changePojos(List<PeopleShardColModByDBTableOnSqlServer> daoPojos) {
		for(PeopleShardColModByDBTableOnSqlServer daoPojo: daoPojos)
			changePojo(daoPojo);
	}
	
	private void verifyPojo(PeopleShardColModByDBTableOnSqlServer daoPojo) {
		//assert changed value
	}
	
	private void verifyPojos(List<PeopleShardColModByDBTableOnSqlServer> daoPojos) {
		for(PeopleShardColModByDBTableOnSqlServer daoPojo: daoPojos)
			verifyPojo(daoPojo);
	}
	
	@After
	public void tearDown() throws Exception {
//		To clean up all test data
//		dao.delete(null, dao.queryAll(null));
	} 
	
	
	@Test
	public void testCount() throws Exception {
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, affected);
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(3, affected);
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	}
	
	@Test
	public void testDelete1() throws Exception {
	    DalHints hints = new DalHints();
		PeopleShardColModByDBTableOnSqlServer daoPojo = createPojo(1);
		daoPojo.setPeopleID(1l);
		daoPojo.setCityID(200);
		daoPojo.setProvinceID(300);
		daoPojo.setName("test");
		daoPojo.setCountryID(200);
		
		dao.delete(hints, daoPojo); 
//		assertEquals(1, affected);
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(2, affected);
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(3, affected);
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	}
	
	@Test
	public void testDelete() throws Exception {
	    DalHints hints = new DalHints();
	    PeopleShardColModByDBTableOnSqlServer daoPojo = createPojo(1);
		daoPojo.setPeopleID(1l);
		daoPojo.setCityID(200);
		daoPojo.setProvinceID(300);
		dao.delete(hints, daoPojo); 
//		assertEquals(1, affected);
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(2, affected);
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(3, affected);
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	}
	
	@Test
	public void testDelete2() throws Exception {
//		DalHints hints = new DalHints();
//		List<ShardColModByDBTableOnSqlServer> daoPojos = dao.queryAll(null);
//		int[] affected = dao.delete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
		
		List<PeopleShardColModByDBTableOnSqlServer> daoPojos = dao.queryAll(new DalHints().inShard(1).inTableShard(0));
		dao.delete(new DalHints(), daoPojos);
//		assertArrayEquals(new int[]{1,1,1},  affected1);
		
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(0, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	}
	
	@Test
	public void testBatchDelete() throws Exception {
//		DalHints hints = new DalHints();
//		List<ShardColModByDBTableOnSqlServer> daoPojos = dao.queryAll(null);
//		int[] affected = dao.batchDelete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
		
		DalHints hints = new DalHints();
		List<PeopleShardColModByDBTableOnSqlServer> daoPojos = dao.queryAll(new DalHints().inShard(0).inTableShard(1));
		dao.batchDelete(new DalHints(), daoPojos);
//		assertArrayEquals(new int[]{1,1,1},  affected1);
		
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(0, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	}
	
	@Test
	public void testQueryAll() throws Exception {
//		List<ShardColModByDBTableOnSqlServer> list = dao.queryAll(new DalHints().inShard(0).inShard(0));
//		assertEquals(0, list.size());
		
		List<PeopleShardColModByDBTableOnSqlServer> list = dao.queryAll(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, list.size());
		
		list = dao.queryAll(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, list.size());
		
		list = dao.queryAll(new DalHints().inShard(1).inTableShard(0));
		assertEquals(3, list.size());
		
		list = dao.queryAll(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, list.size());
	}
	
	@Test
	public void testInsert1() throws Exception {
//		DalHints hints = new DalHints();
//		ShardColModByDBTableOnSqlServer daoPojo = createPojo(1);
//		daoPojo.setCityID(200);
//		daoPojo.setProvinceID(201);
//		daoPojo.setName("insert");
//		int affected = dao.insert(hints.inShard(0).inTableShard(1), daoPojo);
//		assertEquals(1, affected);
		
		
		DalHints hints = new DalHints();
		PeopleShardColModByDBTableOnSqlServer daoPojo = createPojo(1);
		daoPojo.setCityID(307);
		daoPojo.setProvinceID(28);
		daoPojo.setName("insert");
		daoPojo.setCountryID(200);
		dao.insert(new DalHints(), daoPojo);
//		assertEquals(1, affected1);
		
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(4, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	}
	
	@Test
	public void testInsert2() throws Exception {
//		DalHints hints = new DalHints();
//		List<ShardColModByDBTableOnSqlServer> daoPojos = dao.queryAll(new DalHints());
//		int[] affected = dao.insert(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		
		DalHints hints = new DalHints();
		List<PeopleShardColModByDBTableOnSqlServer> daoPojos = dao.queryAll(new DalHints().inShard(0).inTableShard(0));
		dao.insert(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1},  affected1);
		
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	}
	
	@Test
	public void testInsert3() throws Exception {
//		DalHints hints = new DalHints();
//		KeyHolder keyHolder = new KeyHolder();
//		ShardColModByDBTableOnSqlServer daoPojo = createPojo(1);
//		int affected = dao.insert(hints, keyHolder, daoPojo);
//		assertEquals(1, affected);
//		assertEquals(1, keyHolder.size());
		
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		PeopleShardColModByDBTableOnSqlServer daoPojo = createPojo(1);
		daoPojo.setCityID(307);
		daoPojo.setProvinceID(28);
		daoPojo.setName("insert");
		daoPojo.setCountryID(200);
		dao.insert(hints, keyHolder, daoPojo);
//		assertEquals(1, affected1);
		assertEquals(1, keyHolder.size());
		assertEquals(4l, keyHolder.getKey());
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(4, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	}
	
	@Test
	public void testInsert4() throws Exception {
//		DalHints hints = new DalHints();
//		KeyHolder keyHolder = new KeyHolder();
//		List<ShardColModByDBTableOnSqlServer> daoPojos = dao.queryAll(new DalHints());
//		int[] affected = dao.insert(hints, keyHolder, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		assertEquals(10, keyHolder.size());
		
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<PeopleShardColModByDBTableOnSqlServer> daoPojos = dao.queryAll(new DalHints().inShard(1).inTableShard(1));
		dao.insert(new DalHints(), keyHolder, daoPojos);
//		assertArrayEquals(new int[]{1,1,1},  affected1);
		assertEquals(3, keyHolder.size());
		assertEquals(4l, keyHolder.getKey(0));
		assertEquals(5l, keyHolder.getKey(1));
		assertEquals(6l, keyHolder.getKey(2));
		
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(6, affected);
	}
	
	@Test
	public void testInsert5() throws Exception {
//		DalHints hints = new DalHints();
//		List<ShardColModByDBTableOnSqlServer> daoPojos = dao.queryAll(new DalHints());
//		int[] affected = dao.batchInsert(hints, daoPojos);
		
		DalHints hints = new DalHints();
		List<PeopleShardColModByDBTableOnSqlServer> daoPojos = dao.queryAll(new DalHints().inShard(1).inTableShard(0));
		dao.batchInsert(new DalHints(), daoPojos);
		
		int affected = dao.count(new DalHints().inShard(0).inTableShard(0));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(0).inTableShard(1));
		assertEquals(3, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(0));
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().inShard(1).inTableShard(1));
		assertEquals(3, affected);
	}
	
	@Test
	public void testQueryAllByPage() throws Exception {
//		DalHints hints = new DalHints();
//		int pageSize = 100;
//		int pageNo = 1;
//		List<ShardColModByDBTableOnSqlServer> list = dao.queryAllByPage(pageNo, pageSize, hints);
//		assertEquals(10, list.size());
		
		List<PeopleShardColModByDBTableOnSqlServer> list = dao.queryAllByPage(2, 2, new DalHints().inShard(1).inTableShard(0));
		assertEquals(1, list.size());
		assertEquals("InsertByfields_1fields_4", list.get(0).getName());
	}
	
	@Test
	public void testQueryByPk1() throws Exception {
//		Number id = 1;
//		DalHints hints = new DalHints();
//		ShardColModByDBTableOnSqlServer affected = dao.queryByPk(id, hints);
//		assertNotNull(affected);
		
		Number id = 1;
		DalHints hints = new DalHints();
		PeopleShardColModByDBTableOnSqlServer affected = dao.queryByPk(id,  new DalHints().inShard(1).setTableShardValue(0));
		assertNotNull(affected);
		assertEquals("InsertByfields_1fields_0", affected.getName());
	}
	
	@Test
	public void testQueryByPk2() throws Exception {
//		ShardColModByDBTableOnSqlServer pk = createPojo(1);
//		DalHints hints = new DalHints();
//		ShardColModByDBTableOnSqlServer affected = dao.queryByPk(pk, hints);
//		assertNotNull(affected);
		
		PeopleShardColModByDBTableOnSqlServer pk = createPojo(1);
		pk.setPeopleID(2l);
		DalHints hints = new DalHints();
		PeopleShardColModByDBTableOnSqlServer affected = dao.queryByPk(pk, new DalHints().inShard(0).inTableShard(1));
		assertNotNull(affected);
		assertEquals("InsertByfields_0fields_3", affected.getName());
	}
	
	@Test
	public void testUpdate1() throws Exception {
//		DalHints hints = new DalHints();
//		ShardColModByDBTableOnSqlServer daoPojo = dao.queryByPk(createPojo(1), hints);
//		changePojo(daoPojo);
//		int affected = dao.update(hints, daoPojo);
//		assertEquals(1, affected);
//		daoPojo = dao.queryByPk(createPojo(1), null);
//		verifyPojo(daoPojo);
		
		DalHints hints = new DalHints();
		PeopleShardColModByDBTableOnSqlServer daoPojo = new PeopleShardColModByDBTableOnSqlServer();
		daoPojo.setPeopleID(2l);
		daoPojo.setCityID(200);
		daoPojo.setProvinceID(20);
		daoPojo.setName("UpdateByfields_0fields_0");
		dao.update(new DalHints(), daoPojo);
//		assertEquals(1, affected);
		
		PeopleShardColModByDBTableOnSqlServer daoPojo1=dao.queryByPk(2, new DalHints().inShard(0).inTableShard(0));
		assertEquals("UpdateByfields_0fields_0", daoPojo1.getName());
	}
	
	@Test
	public void testUpdate2() throws Exception {
//		DalHints hints = new DalHints();
//		List<ShardColModByDBTableOnSqlServer> daoPojos = dao.queryAll(new DalHints());
//		changePojos(daoPojos);
//		int[] affected = dao.update(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		verifyPojos(dao.queryAll(new DalHints()));
		
		
		DalHints hints = new DalHints();
		List<PeopleShardColModByDBTableOnSqlServer> daoPojos = dao.queryAll(new DalHints().inShard(0).inTableShard(1));
		daoPojos.get(0).setName("Update0");
		daoPojos.get(1).setName("Update1");
		daoPojos.get(2).setName("Update2");
		dao.update(new DalHints(), daoPojos);
//		assertArrayEquals(new int[]{1,1,1},  affected);
		
		daoPojos = dao.queryAll(new DalHints().inShard(0).inTableShard(1));
		for(int i=0;i<daoPojos.size();i++)
			assertEquals("Update"+i, daoPojos.get(i).getName());
	}
	
	@Test
	public void testBatchUpdate() throws Exception {
//		DalHints hints = new DalHints();
//		List<ShardColModByDBTableOnSqlServer> daoPojos = dao.queryAll(new DalHints());
//		changePojos(daoPojos);
//		int[] affected = dao.batchUpdate(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		verifyPojos(dao.queryAll(new DalHints()));
		
		DalHints hints = new DalHints();
		List<PeopleShardColModByDBTableOnSqlServer> daoPojos = dao.queryAll(new DalHints().inShard(0).inTableShard(1));
		daoPojos.get(0).setName("Update0");
		daoPojos.get(1).setName("Update1");
		daoPojos.get(2).setName("Update2");
		dao.batchUpdate(new DalHints(), daoPojos);
//		assertArrayEquals(new int[]{1,1,1},  affected);
		daoPojos = dao.queryAll(new DalHints().inShard(0).inTableShard(1));
		for(int i=0;i<daoPojos.size();i++)
			assertEquals("Update"+i, daoPojos.get(i).getName());
	}

}
