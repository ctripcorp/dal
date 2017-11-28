package noShardTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.*;

import shardTest.newVersionCodeTest.PeopleShardColModShardByDBOnSqlServer;
import shardTest.newVersionCodeTest.PeopleShardColModShardByDBOnSqlServerDao;

import static org.junit.Assert.*;


import com.ctrip.platform.dal.dao.*;

/**
 * JUnit test of MasterOnlyOnSqlServerDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class MasterOnlyOnSqlServerDaoUnitTest {

	private static final String DATA_BASE = "testMaterOnlyOnSqlServer";

	private static DalClient client = null;
	private static MasterOnlyOnSqlServerDao dao = null;
	private static PeopleShardColModShardByDBOnSqlServerDao dao1= null;
	
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
		dao = new MasterOnlyOnSqlServerDao();
		dao1 = new PeopleShardColModShardByDBOnSqlServerDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
//		for(int i = 0; i < 10; i++) {
//			MasterOnlyOnSqlServer daoPojo = createPojo(i);
//
//			try {
//				dao.insert(new DalHints().enableIdentityInsert(), daoPojo);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
		dao.test_def_truncate(new DalHints());
		dao1.test_def_truncate(new DalHints().inShard(1));
		
		List<MasterOnlyOnSqlServer> daoPojos1 = new ArrayList<MasterOnlyOnSqlServer>(3);
		for(int i=0;i<3;i++)
		{
			MasterOnlyOnSqlServer daoPojo = new MasterOnlyOnSqlServer();
			daoPojo.setName("Master_"+i);	
			daoPojo.setCityID(i+20);
			daoPojo.setProvinceID(i+30);
			daoPojo.setCountryID(i+40);
			daoPojos1.add(daoPojo);
		}
		dao.insert(new DalHints(), daoPojos1);
		
		List<PeopleShardColModShardByDBOnSqlServer> daoPojos2 = new ArrayList<PeopleShardColModShardByDBOnSqlServer>(6);
		for(int i=0;i<6;i++)
		{
			PeopleShardColModShardByDBOnSqlServer daoPojo = new PeopleShardColModShardByDBOnSqlServer();
			daoPojo.setName("Slave_"+i);
			daoPojo.setCityID(i+30);
			daoPojo.setProvinceID(i+30);
			daoPojo.setCountryID(i+40);
			daoPojos2.add(daoPojo);
		}
		dao1.insert(new DalHints().inShard(1), daoPojos2);
	}
	
	private MasterOnlyOnSqlServer createPojo(int index) {
		MasterOnlyOnSqlServer daoPojo = new MasterOnlyOnSqlServer();

		//daoPojo.setId(index);
		//daoPojo set not null field
		
		return daoPojo;
	}

	private void changePojo(MasterOnlyOnSqlServer daoPojo) {
		// Change a field to make pojo different with original one
	}
	
	private void changePojos(List<MasterOnlyOnSqlServer> daoPojos) {
		for(MasterOnlyOnSqlServer daoPojo: daoPojos)
			changePojo(daoPojo);
	}
	
	private void verifyPojo(MasterOnlyOnSqlServer daoPojo) {
		//assert changed value
	}
	
	private void verifyPojos(List<MasterOnlyOnSqlServer> daoPojos) {
		for(MasterOnlyOnSqlServer daoPojo: daoPojos)
			verifyPojo(daoPojo);
	}
	
	@After
	public void tearDown() throws Exception {
		dao.test_def_truncate(new DalHints());
		dao1.test_def_truncate(new DalHints().inShard(1));
	} 
	
	
	@Test
	public void testCount() throws Exception {
		int affected = dao.count(new DalHints());
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().masterOnly());
		assertEquals(3, affected);
	}
	
	@Test
	public void testDelete1() throws Exception {
	    DalHints hints = new DalHints();
		MasterOnlyOnSqlServer daoPojo = createPojo(1);
		daoPojo.setPeopleID(2l);
		dao.delete(hints, daoPojo); 
//		assertEquals(1, affected);
		int affected = dao.count(new DalHints());
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().masterOnly());
		assertEquals(2, affected);
	}
	
	@Test
	public void testDelete2() throws Exception {
//		DalHints hints = new DalHints();
//		List<MasterOnlyOnSqlServer> daoPojos = dao.queryAll(null);
//		int[] affected = dao.delete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
		
		DalHints hints = new DalHints();
		List<MasterOnlyOnSqlServer> daoPojos =new ArrayList<MasterOnlyOnSqlServer>(2);
		MasterOnlyOnSqlServer daoPojo1 = createPojo(1);
		daoPojo1.setPeopleID(2l);
		MasterOnlyOnSqlServer daoPojo2 = createPojo(1);
		daoPojo2.setPeopleID(3l);
		daoPojos.add(daoPojo1);
		daoPojos.add(daoPojo2);
		
		dao.delete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1},  affected1);
		
		int affected = dao.count(new DalHints());
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().masterOnly());
		assertEquals(1, affected);
	}
	
	@Test
	public void testBatchDelete() throws Exception {
		DalHints hints = new DalHints();
		List<MasterOnlyOnSqlServer> daoPojos =new ArrayList<MasterOnlyOnSqlServer>(2);
		MasterOnlyOnSqlServer daoPojo1 = createPojo(1);
		daoPojo1.setPeopleID(2l);
		MasterOnlyOnSqlServer daoPojo2 = createPojo(1);
		daoPojo2.setPeopleID(3l);
		daoPojos.add(daoPojo1);
		daoPojos.add(daoPojo2);
		dao.batchDelete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
		int affected = dao.count(new DalHints());
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().masterOnly());
		assertEquals(1, affected);
	}
	
	@Test
	public void testQueryAll() throws Exception {
		List<MasterOnlyOnSqlServer> list = dao.queryAll(new DalHints());
		assertEquals(6, list.size());
		
		list = dao.queryAll(new DalHints().masterOnly());
		assertEquals(3, list.size());
	}
	
	@Test
	public void testInsert1() throws Exception {
//		DalHints hints = new DalHints();
//		MasterOnlyOnSqlServer daoPojo = createPojo(1);
//		int affected = dao.insert(hints, daoPojo);
//		assertEquals(1, affected);
		
		DalHints hints = new DalHints();
		MasterOnlyOnSqlServer daoPojo = createPojo(1);
		daoPojo.setName("masteronly");
		dao.insert(hints, daoPojo);
//		assertEquals(1, affected1);
		
		int affected = dao.count(new DalHints());
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().masterOnly());
		assertEquals(4, affected);
	}
	
	@Test
	public void testInsert2() throws Exception {
//		DalHints hints = new DalHints();
//		List<MasterOnlyOnSqlServer> daoPojos = dao.queryAll(new DalHints());
//		int[] affected = dao.insert(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
		
		DalHints hints = new DalHints();
		List<MasterOnlyOnSqlServer> daoPojos = dao.queryAll(new DalHints());
		dao.insert(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1},  affected1);
		
		int affected = dao.count(new DalHints());
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().masterOnly());
		assertEquals(9, affected);		
	}
	
	@Test
	public void testInsert3() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		MasterOnlyOnSqlServer daoPojo = createPojo(1);
		daoPojo.setName("masteronly");
		dao.insert(hints, keyHolder, daoPojo);
		assertEquals(1, keyHolder.size());
		
		int affected = dao.count(new DalHints());
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().masterOnly());
		assertEquals(4, affected);
	}
	
	@Test
	public void testInsert4() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<MasterOnlyOnSqlServer> daoPojos = dao.queryAll(new DalHints());
		dao.insert(hints, keyHolder, daoPojos);
		assertEquals(6, keyHolder.size());
		int affected = dao.count(new DalHints());
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().masterOnly());
		assertEquals(9, affected);	
	}
	
	@Test
	public void testInsert5() throws Exception {
		DalHints hints = new DalHints();
		List<MasterOnlyOnSqlServer> daoPojos = dao.queryAll(new DalHints());
		dao.batchInsert(hints, daoPojos);
		
		int affected = dao.count(new DalHints());
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().masterOnly());
		assertEquals(9, affected);	
	}
	
	@Test
	public void testQueryAllByPage() throws Exception {
		DalHints hints = new DalHints();
		int pageSize = 100;
		int pageNo = 1;
		List<MasterOnlyOnSqlServer> list = dao.queryAllByPage(pageNo, pageSize, hints);
		assertEquals(6, list.size());
		
		list = dao.queryAllByPage(pageNo, pageSize, hints.masterOnly());
		assertEquals(3, list.size());
	}
	
	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 1l;
		DalHints hints = new DalHints();
		MasterOnlyOnSqlServer affected = dao.queryByPk(id, hints);
		assertNotNull(affected);
		assertEquals("Slave_0", affected.getName());
		
		affected = dao.queryByPk(id, hints.masterOnly());
		assertNotNull(affected);
		assertEquals("Master_0", affected.getName());
		
	}
	
	@Test
	public void testQueryByPk2() throws Exception {
		MasterOnlyOnSqlServer pk = createPojo(1);
		pk.setPeopleID(1l);
		DalHints hints = new DalHints();
		MasterOnlyOnSqlServer affected = dao.queryByPk(pk, hints);
		assertNotNull(affected);
		assertEquals("Slave_0", affected.getName());
		
		affected = dao.queryByPk(pk, hints.masterOnly());
		assertEquals("Master_0", affected.getName());
	}
	
	@Test
	public void testUpdate1() throws Exception {
//		DalHints hints = new DalHints();
//		MasterOnlyOnSqlServer daoPojo = dao.queryByPk(createPojo(1), hints);
//		changePojo(daoPojo);
//		int affected = dao.update(hints, daoPojo);
//		assertEquals(1, affected);
//		daoPojo = dao.queryByPk(createPojo(1), null);
//		verifyPojo(daoPojo);
		
		DalHints hints = new DalHints();
		MasterOnlyOnSqlServer daoPojo = createPojo(1);
		daoPojo.setPeopleID(2l);
		daoPojo.setName("update");
		dao.update(hints, daoPojo);
		
		daoPojo = dao.queryByPk(2l, null);
		assertEquals("Slave_1", daoPojo.getName());
		
		daoPojo = dao.queryByPk(2,hints.masterOnly());
		assertEquals("update", daoPojo.getName());
	}
	
	@Test
	public void testUpdate2() throws Exception {
//		DalHints hints = new DalHints();
//		List<MasterOnlyOnSqlServer> daoPojos = dao.queryAll(new DalHints());
//		changePojos(daoPojos);
//		int[] affected = dao.update(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		verifyPojos(dao.queryAll(new DalHints()));
		
		DalHints hints = new DalHints();
		List<MasterOnlyOnSqlServer> daoPojos =new ArrayList<MasterOnlyOnSqlServer>(2);
		MasterOnlyOnSqlServer daoPojo1 = createPojo(1);
		daoPojo1.setPeopleID(2l);
		daoPojo1.setName("update2");
		MasterOnlyOnSqlServer daoPojo2 = createPojo(1);
		daoPojo2.setPeopleID(3l);
		daoPojo2.setName("update3");
		daoPojos.add(daoPojo1);
		daoPojos.add(daoPojo2);
		
	    dao.update(hints, daoPojos);
		
	    MasterOnlyOnSqlServer daoPojo=dao.queryByPk(2l, hints);
		assertEquals("Slave_1", daoPojo.getName());
		
		daoPojo=dao.queryByPk(3l, hints);
		assertEquals("Slave_2", daoPojo.getName());
		
		daoPojo=dao.queryByPk(2l, hints.masterOnly());
		assertEquals("update2", daoPojo.getName());
		
		daoPojo=dao.queryByPk(3l, hints.masterOnly());
		assertEquals("update3", daoPojo.getName());
	}
	
	@Test
	public void testBatchUpdate() throws Exception {
//		DalHints hints = new DalHints();
//		List<MasterOnlyOnSqlServer> daoPojos = dao.queryAll(new DalHints());
//		changePojos(daoPojos);
//		int[] affected = dao.batchUpdate(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		verifyPojos(dao.queryAll(new DalHints()));
		DalHints hints = new DalHints();
		List<MasterOnlyOnSqlServer> daoPojos =new ArrayList<MasterOnlyOnSqlServer>(2);
		MasterOnlyOnSqlServer daoPojo1 = createPojo(1);
		daoPojo1.setPeopleID(2l);
		daoPojo1.setName("update2");
		MasterOnlyOnSqlServer daoPojo2 = createPojo(1);
		daoPojo2.setPeopleID(3l);
		daoPojo2.setName("update3");
		daoPojos.add(daoPojo1);
		daoPojos.add(daoPojo2);
		
		dao.batchUpdate(hints, daoPojos);
		
		MasterOnlyOnSqlServer daoPojo=dao.queryByPk(2l, hints);
		assertEquals("Slave_1", daoPojo.getName());
		
		daoPojo=dao.queryByPk(3l, hints);
		assertEquals("Slave_2", daoPojo.getName());
		
		daoPojo=dao.queryByPk(2l, hints.masterOnly());
		assertEquals("update2", daoPojo.getName());
		
		daoPojo=dao.queryByPk(3l, hints.masterOnly());
		assertEquals("update3", daoPojo.getName());
	}
	
	@Test
	public void testtest_build_delete() throws Exception {
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(22);
		CityID.add(30);
	    int ret = dao.test_build_delete(CityID, new DalHints());
	   
	    int affected = dao.count(new DalHints());
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().masterOnly());
		assertEquals(1, affected);
	}
	
	@Test
	public void testtest_build_insert() throws Exception {
		Integer CityID = 50;// Test value here
		String Name = "insert";// Test value here
	    int ret = dao.test_build_insert(CityID, Name, new DalHints());
	    
	    int affected = dao.count(new DalHints());
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().masterOnly());
		assertEquals(4, affected);
	}
	
	@Test
	public void testtest_build_update() throws Exception {
		String Name = "update";// Test value here
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(22);
		CityID.add(30);
	    int ret = dao.test_build_update(Name, CityID, new DalHints());
	    
	    MasterOnlyOnSqlServer daoPojo=dao.queryByPk(1l, new DalHints());
		assertEquals("Slave_0", daoPojo.getName());
		
	
		daoPojo=dao.queryByPk(1l, new DalHints().masterOnly());
		assertEquals("update", daoPojo.getName());
		
		daoPojo=dao.queryByPk(3l, new DalHints().masterOnly());
		assertEquals("update", daoPojo.getName());
	}
	
	@Test
	public void testtest_build_queryFirst() throws Exception {
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(22);
		CityID.add(30);
	    MasterOnlyOnSqlServer ret = dao.test_build_queryFirst(CityID, new DalHints());
	    assertEquals("Slave_0",ret.getName());
	   
	    ret = dao.test_build_queryFirst(CityID, new DalHints().masterOnly());
	    assertEquals("Master_0",ret.getName());
	}

	@Test
	public void testtest_build_queryList() throws Exception {
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(22);
		CityID.add(30);
	    List<MasterOnlyOnSqlServer> ret = dao.test_build_queryList(CityID, new DalHints());
	    assertEquals(1, ret.size());
	    
	    ret = dao.test_build_queryList(CityID, new DalHints().masterOnly());
	    assertEquals(2, ret.size());
	}
	
	@Test
	public void testtest_build_querySingle() throws Exception {
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
//		CityID.add(22);
		CityID.add(30);
	    MasterOnlyOnSqlServer ret = dao.test_build_querySingle(CityID, new DalHints());
	    assertEquals("Slave_0",ret.getName());
	    
	    ret = dao.test_build_querySingle(CityID, new DalHints().masterOnly());
	    assertEquals("Master_0",ret.getName());
	}
	
	@Test
	public void testtest_build_queryFieldFirst() throws Exception {
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(22);
		CityID.add(30);
	    String ret = dao.test_build_queryFieldFirst(CityID, new DalHints());
	    assertEquals("Slave_0",ret);
	    
	    ret = dao.test_build_queryFieldFirst(CityID, new DalHints().masterOnly());
	    assertEquals("Master_0",ret);
	}

	@Test
	public void testtest_build_queryFieldList() throws Exception {
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(22);
		CityID.add(30);
	    List<String> ret = dao.test_build_queryFieldList(CityID, new DalHints());
	    assertEquals(1, ret.size());
	    
	    ret = dao.test_build_queryFieldList(CityID, new DalHints().masterOnly());
	    assertEquals(2, ret.size());    
	}

	
	@Test
	public void testtest_build_queryFieldSingle() throws Exception {
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
//		CityID.add(22);
		CityID.add(30);
	    String ret = dao.test_build_queryFieldSingle(CityID, new DalHints());
	    assertEquals("Slave_0", ret);
	    
	    ret = dao.test_build_queryFieldSingle(CityID, new DalHints().masterOnly());
	    assertEquals("Master_0", ret);
	}
	
	@Test
	public void testtest_def_truncate() throws Exception {
	    dao.test_def_truncate(new DalHints());
	    int affected = dao.count(new DalHints());
		assertEquals(6, affected);
		
		affected = dao.count(new DalHints().masterOnly());
		assertEquals(0, affected);
	}
	
	@Test
	public void testtest_def_queryList() throws Exception {
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(22);
		CityID.add(30);
		List<MasterOnlyOnSqlServer> ret = dao.test_def_queryList(CityID, new DalHints());
		assertEquals(1, ret.size());
		
		ret = dao.test_def_queryList(CityID, new DalHints().masterOnly());
		assertEquals(2, ret.size());
		
	}
}
