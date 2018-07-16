package noshardtest;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import dao.noshard.MasterOnlyOnSqlServerDao;
import dao.shard.newVersionCode.PeopleShardColModShardByDBOnSqlServerDao;
import entity.SqlServerPeopleTable;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * JUnit test of SqlServerPeopleTableDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class MasterOnlyOnSqlServerDaoUnitTest {

	private static final String DATA_BASE = "testMaterOnlyOnSqlServer";

	private static DalClient client = null;
	private static MasterOnlyOnSqlServerDao dao = null;
	private static PeopleShardColModShardByDBOnSqlServerDao dao1= null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		client = DalClientFactory.getClient(DATA_BASE);
		dao = new MasterOnlyOnSqlServerDao();
		dao1 = new PeopleShardColModShardByDBOnSqlServerDao();
		//        先查询一遍并等待2秒，确保所有逻辑库的读库freshness已更新
		dao.count(null);
		Thread.sleep(2000);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
//		for(int i = 0; i < 10; i++) {
//			SqlServerPeopleTable daoPojo = createPojo(i);
//
//			try {
//				dao.insert(new DalHints().enableIdentityInsert(), daoPojo);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
		dao.test_def_truncate(new DalHints());
		dao1.test_def_truncate(new DalHints().inShard(1));

		List<SqlServerPeopleTable> daoPojos1 = new ArrayList<SqlServerPeopleTable>(3);
		for(int i=0;i<3;i++)
		{
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setName("Master_"+i);
			daoPojo.setCityID(i+20);
			daoPojo.setProvinceID(i+30);
			daoPojo.setCountryID(i+40);
			daoPojos1.add(daoPojo);
		}
		dao.insert(new DalHints(), daoPojos1);

		List<SqlServerPeopleTable> daoPojos2 = new ArrayList<SqlServerPeopleTable>(6);
		for(int i=0;i<6;i++)
		{
			SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
			daoPojo.setName("Slave_"+i);
			daoPojo.setCityID(i+30);
			daoPojo.setProvinceID(i+30);
			daoPojo.setCountryID(i+40);
			daoPojos2.add(daoPojo);
		}
		dao1.insert(new DalHints().inShard(1), daoPojos2);
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
//		dao.test_def_truncate(new DalHints());
//		dao1.test_def_truncate(new DalHints().inShard(1));
	}


	@Test
	public void testCount() throws Exception {
		int affected = dao.count(new DalHints());
		assertEquals(6, affected);

		affected = dao.count(new DalHints().masterOnly());
		assertEquals(3, affected);

		affected=dao.count(new DalHints().slaveOnly());
		assertEquals(6,affected);

		Thread.sleep(5000);

		affected=dao.count(new DalHints().freshness(3));
		assertEquals(6,affected);

		affected=dao.count(new DalHints().freshness(2));
		assertEquals(6,affected);

		affected=dao.count(new DalHints().freshness(1));
		assertEquals(3,affected);

		affected=dao.count(new DalHints().freshness(3).masterOnly());
		assertEquals(3,affected);
	}

	@Test
	public void testDelete1() throws Exception {
	    DalHints hints = new DalHints();
		SqlServerPeopleTable daoPojo = createPojo(1);
		daoPojo.setPeopleID(2l);
		dao.delete(hints, daoPojo);
//		assertEquals(1, affected);
		int affected = dao.count(new DalHints());
		assertEquals(6, affected);

		affected = dao.count(new DalHints().masterOnly());
		assertEquals(2, affected);

		dao.delete(new DalHints().slaveOnly(),daoPojo);
		affected=dao.count(new DalHints());
		assertEquals(5,affected);

		affected=dao.count(new DalHints().masterOnly());
		assertEquals(2,affected);
	}

	@Test
	public void testDelete2() throws Exception {
//		DalHints hints = new DalHints();
//		List<SqlServerPeopleTable> daoPojos = dao.queryAll(null);
//		int[] affected = dao.delete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);

		DalHints hints = new DalHints();
		List<SqlServerPeopleTable> daoPojos =new ArrayList<SqlServerPeopleTable>(2);
		SqlServerPeopleTable daoPojo1 = createPojo(1);
		daoPojo1.setPeopleID(2l);
		SqlServerPeopleTable daoPojo2 = createPojo(1);
		daoPojo2.setPeopleID(3l);
		daoPojos.add(daoPojo1);
		daoPojos.add(daoPojo2);

		dao.delete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1},  affected1);

		int affected = dao.count(new DalHints());
		assertEquals(6, affected);

		affected = dao.count(new DalHints().masterOnly());
		assertEquals(1, affected);

		dao.delete(new DalHints().slaveOnly(),daoPojos);

		affected=dao.count(new DalHints());
		assertEquals(4,affected);

		affected=dao.count(new DalHints().masterOnly());
		assertEquals(1,affected);
	}

	@Test
	public void testBatchDelete() throws Exception {
		DalHints hints = new DalHints();
		List<SqlServerPeopleTable> daoPojos =new ArrayList<SqlServerPeopleTable>(2);
		SqlServerPeopleTable daoPojo1 = createPojo(1);
		daoPojo1.setPeopleID(2l);
		SqlServerPeopleTable daoPojo2 = createPojo(1);
		daoPojo2.setPeopleID(3l);
		daoPojos.add(daoPojo1);
		daoPojos.add(daoPojo2);
		dao.batchDelete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
		int affected = dao.count(new DalHints());
		assertEquals(6, affected);

		affected = dao.count(new DalHints().masterOnly());
		assertEquals(1, affected);

		dao.batchDelete(new DalHints().slaveOnly(),daoPojos);

		affected=dao.count(new DalHints());
		assertEquals(4,affected);

		affected=dao.count(new DalHints().masterOnly());
		assertEquals(1,affected);
	}

	@Test
	public void testQueryAll() throws Exception {
		List<SqlServerPeopleTable> list = dao.queryAll(new DalHints());
		assertEquals(6, list.size());

		list = dao.queryAll(new DalHints().masterOnly());
		assertEquals(3, list.size());

		list=dao.queryAll(new DalHints().slaveOnly());
		assertEquals(6,list.size());

		list=dao.queryAll(new DalHints().freshness(3));
		assertEquals(6,list.size());

		list=dao.queryAll(new DalHints().freshness(2));
		assertEquals(6,list.size());

		list=dao.queryAll(new DalHints().freshness(1));
		assertEquals(3,list.size());

		list=dao.queryAll(new DalHints().freshness(3).masterOnly());
		assertEquals(3,list.size());
	}

	@Test
	public void testInsert1() throws Exception {
//		DalHints hints = new DalHints();
//		SqlServerPeopleTable daoPojo = createPojo(1);
//		int affected = dao.insert(hints, daoPojo);
//		assertEquals(1, affected);

		DalHints hints = new DalHints();
		SqlServerPeopleTable daoPojo = createPojo(1);
		daoPojo.setName("masteronly");
		dao.insert(hints, daoPojo);
//		assertEquals(1, affected1);

		int affected = dao.count(new DalHints());
		assertEquals(6, affected);

		affected = dao.count(new DalHints().masterOnly());
		assertEquals(4, affected);

		dao.insert(new DalHints().slaveOnly(),daoPojo);

		affected=dao.count(new DalHints());
		assertEquals(7,affected);

		affected=dao.count(new DalHints().masterOnly());
		assertEquals(4,affected);
	}

	@Test
	public void testInsert2() throws Exception {
//		DalHints hints = new DalHints();
//		List<SqlServerPeopleTable> daoPojos = dao.queryAll(new DalHints());
//		int[] affected = dao.insert(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);

		DalHints hints = new DalHints();
		List<SqlServerPeopleTable> daoPojos = dao.queryAll(new DalHints());
		dao.insert(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1},  affected1);

		int affected = dao.count(new DalHints());
		assertEquals(6, affected);

		affected = dao.count(new DalHints().masterOnly());
		assertEquals(9, affected);

		dao.insert(new DalHints().slaveOnly(),daoPojos);

		affected=dao.count(new DalHints().slaveOnly());
		assertEquals(12,affected);

		affected=dao.count(new DalHints().masterOnly());
		assertEquals(9,affected);
	}

	@Test
	public void testInsert3() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		SqlServerPeopleTable daoPojo = createPojo(1);
		daoPojo.setName("masteronly");
		dao.insert(hints, keyHolder, daoPojo);
		assertEquals(1, keyHolder.size());

		int affected = dao.count(new DalHints());
		assertEquals(6, affected);

		affected = dao.count(new DalHints().masterOnly());
		assertEquals(4, affected);

		KeyHolder keyHolder2=new KeyHolder();
		dao.insert(new DalHints().slaveOnly(),keyHolder2,daoPojo);
		assertEquals(1,keyHolder2.size());

		affected=dao.count(new DalHints().slaveOnly());
		assertEquals(7, affected);

		affected=dao.count(new DalHints().masterOnly());
		assertEquals(4,affected);
	}

	@Test
	public void testInsert4() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<SqlServerPeopleTable> daoPojos = dao.queryAll(new DalHints());
		dao.insert(hints, keyHolder, daoPojos);
		assertEquals(6, keyHolder.size());
		int affected = dao.count(new DalHints());
		assertEquals(6, affected);

		affected = dao.count(new DalHints().masterOnly());
		assertEquals(9, affected);

		KeyHolder keyHolder2=new KeyHolder();
		dao.insert(new DalHints().slaveOnly(),keyHolder2,daoPojos);
		assertEquals(6,keyHolder2.size());
		affected=dao.count(new DalHints().slaveOnly());
		assertEquals(12,affected);
		affected=dao.count(new DalHints().masterOnly());
		assertEquals(9,affected);
	}

	@Test
	public void testInsert5() throws Exception {
		DalHints hints = new DalHints();
		List<SqlServerPeopleTable> daoPojos = dao.queryAll(new DalHints());
		dao.batchInsert(hints, daoPojos);

		int affected = dao.count(new DalHints());
		assertEquals(6, affected);

		affected = dao.count(new DalHints().masterOnly());
		assertEquals(9, affected);

		dao.batchInsert(new DalHints().slaveOnly(),daoPojos);
		affected=dao.count(new DalHints().slaveOnly());
		assertEquals(12,affected);
		affected=dao.count(new DalHints().masterOnly());
		assertEquals(9,affected);

	}

	@Test
	public void testQueryAllByPage() throws Exception {
		DalHints hints = new DalHints();
		int pageSize = 100;
		int pageNo = 1;
		List<SqlServerPeopleTable> list = dao.queryAllByPage(pageNo, pageSize, hints);
		assertEquals(6, list.size());

		list=dao.queryAllByPage(pageNo,pageSize,new DalHints().slaveOnly());
		assertEquals(6,list.size());

		list = dao.queryAllByPage(pageNo, pageSize, hints.masterOnly());
		assertEquals(3, list.size());

		list=dao.queryAllByPage(pageNo,pageSize,new DalHints().freshness(3));
		assertEquals(6,list.size());

		list=dao.queryAllByPage(pageNo,pageSize,new DalHints().freshness(2));
		assertEquals(6,list.size());

		list=dao.queryAllByPage(pageNo,pageSize,new DalHints().freshness(1));
		assertEquals(3,list.size());

		list=dao.queryAllByPage(pageNo,pageSize,new DalHints().freshness(3).masterOnly());
		assertEquals(3,list.size());
	}

	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 1l;
		DalHints hints = new DalHints();
		SqlServerPeopleTable affected = dao.queryByPk(id, hints);
		assertNotNull(affected);
		assertEquals("Slave_0", affected.getName());

		affected=dao.queryByPk(id,new DalHints().slaveOnly());
		assertEquals("Slave_0",affected.getName());

		affected = dao.queryByPk(id, hints.masterOnly());
		assertNotNull(affected);
		assertEquals("Master_0", affected.getName());

		affected=dao.queryByPk(id,new DalHints().freshness(3));
		assertEquals("Slave_0",affected.getName());

		affected=dao.queryByPk(id,new DalHints().freshness(2));
		assertEquals("Slave_0",affected.getName());

		affected=dao.queryByPk(id,new DalHints().freshness(1));
		assertEquals("Master_0",affected.getName());

		affected=dao.queryByPk(id,new DalHints().freshness(3).masterOnly());
		assertEquals("Master_0",affected.getName());
	}

	@Test
	public void testQueryByPk2() throws Exception {
		SqlServerPeopleTable pk = createPojo(1);
		pk.setPeopleID(1l);
		DalHints hints = new DalHints();
		SqlServerPeopleTable affected = dao.queryByPk(pk, hints);
		assertNotNull(affected);
		assertEquals("Slave_0", affected.getName());

		affected=dao.queryByPk(pk,new DalHints().slaveOnly());
		assertEquals("Slave_0",affected.getName());

		affected = dao.queryByPk(pk, hints.masterOnly());
		assertEquals("Master_0", affected.getName());

		affected=dao.queryByPk(pk,new DalHints().freshness(3));
		assertEquals("Slave_0",affected.getName());

		affected=dao.queryByPk(pk,new DalHints().freshness(2));
		assertEquals("Slave_0",affected.getName());

		affected=dao.queryByPk(pk,new DalHints().freshness(1));
		assertEquals("Master_0",affected.getName());

		affected=dao.queryByPk(pk,new DalHints().freshness(3).masterOnly());
		assertEquals("Master_0",affected.getName());
	}

	@Test
	public void testUpdate1() throws Exception {
//		DalHints hints = new DalHints();
//		SqlServerPeopleTable daoPojo = dao.queryByPk(createPojo(1), hints);
//		changePojo(daoPojo);
//		int affected = dao.update(hints, daoPojo);
//		assertEquals(1, affected);
//		daoPojo = dao.queryByPk(createPojo(1), null);
//		verifyPojo(daoPojo);

		DalHints hints = new DalHints();
		SqlServerPeopleTable daoPojo = createPojo(1);
		daoPojo.setPeopleID(2l);
		daoPojo.setName("update");
		dao.update(hints, daoPojo);

		daoPojo = dao.queryByPk(2l, null);
		assertEquals("Slave_1", daoPojo.getName());

		daoPojo = dao.queryByPk(2,hints.masterOnly());
		assertEquals("update", daoPojo.getName());

		daoPojo.setName("updateSlave");
		dao.update(new DalHints().slaveOnly(),daoPojo);

		daoPojo=dao.queryByPk(2l,new DalHints().slaveOnly());
		assertEquals("updateSlave",daoPojo.getName());
		daoPojo=dao.queryByPk(2l,new DalHints().masterOnly());
		assertEquals("update",daoPojo.getName());
	}

	@Test
	public void testUpdate2() throws Exception {
//		DalHints hints = new DalHints();
//		List<SqlServerPeopleTable> daoPojos = dao.queryAll(new DalHints());
//		changePojos(daoPojos);
//		int[] affected = dao.update(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		verifyPojos(dao.queryAll(new DalHints()));

		DalHints hints = new DalHints();
		List<SqlServerPeopleTable> daoPojos =new ArrayList<SqlServerPeopleTable>(2);
		SqlServerPeopleTable daoPojo1 = createPojo(1);
		daoPojo1.setPeopleID(2l);
		daoPojo1.setName("update2");
		SqlServerPeopleTable daoPojo2 = createPojo(1);
		daoPojo2.setPeopleID(3l);
		daoPojo2.setName("update3");
		daoPojos.add(daoPojo1);
		daoPojos.add(daoPojo2);

	    dao.update(hints, daoPojos);

	    SqlServerPeopleTable daoPojo=dao.queryByPk(2l, hints);
		assertEquals("Slave_1", daoPojo.getName());

		daoPojo=dao.queryByPk(3l, hints);
		assertEquals("Slave_2", daoPojo.getName());

		daoPojo=dao.queryByPk(2l, hints.masterOnly());
		assertEquals("update2", daoPojo.getName());

		daoPojo=dao.queryByPk(3l, hints.masterOnly());
		assertEquals("update3", daoPojo.getName());

		daoPojos.get(0).setName("updateSlave");
		daoPojos.get(1).setName("updateSlave");

		dao.update(new DalHints().slaveOnly(),daoPojos);
		daoPojo=dao.queryByPk(2,new DalHints().slaveOnly());
		assertEquals("updateSlave",daoPojo.getName());
		daoPojo=dao.queryByPk(3,new DalHints().slaveOnly());
		assertEquals("updateSlave",daoPojo.getName());

		daoPojo=dao.queryByPk(2,new DalHints().masterOnly());
		assertEquals("update2",daoPojo.getName());
		daoPojo=dao.queryByPk(3,new DalHints().masterOnly());
		assertEquals("update3",daoPojo.getName());
	}

	@Test
	public void testBatchUpdate() throws Exception {
//		DalHints hints = new DalHints();
//		List<SqlServerPeopleTable> daoPojos = dao.queryAll(new DalHints());
//		changePojos(daoPojos);
//		int[] affected = dao.batchUpdate(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		verifyPojos(dao.queryAll(new DalHints()));
		DalHints hints = new DalHints();
		List<SqlServerPeopleTable> daoPojos =new ArrayList<SqlServerPeopleTable>(2);
		SqlServerPeopleTable daoPojo1 = createPojo(1);
		daoPojo1.setPeopleID(2l);
		daoPojo1.setName("update2");
		SqlServerPeopleTable daoPojo2 = createPojo(1);
		daoPojo2.setPeopleID(3l);
		daoPojo2.setName("update3");
		daoPojos.add(daoPojo1);
		daoPojos.add(daoPojo2);

		dao.batchUpdate(hints, daoPojos);

		SqlServerPeopleTable daoPojo=dao.queryByPk(2l, hints);
		assertEquals("Slave_1", daoPojo.getName());

		daoPojo=dao.queryByPk(3l, hints);
		assertEquals("Slave_2", daoPojo.getName());

		daoPojo=dao.queryByPk(2l, hints.masterOnly());
		assertEquals("update2", daoPojo.getName());

		daoPojo=dao.queryByPk(3l, hints.masterOnly());
		assertEquals("update3", daoPojo.getName());

		daoPojos.get(0).setName("batchUpdateSlave");
		daoPojos.get(1).setName("batchUpdateSlave");

		dao.batchUpdate(new DalHints().slaveOnly(),daoPojos);
		daoPojo=dao.queryByPk(2,new DalHints().slaveOnly());
		assertEquals("batchUpdateSlave",daoPojo.getName());
		daoPojo=dao.queryByPk(3,new DalHints().slaveOnly());
		assertEquals("batchUpdateSlave",daoPojo.getName());

		daoPojo=dao.queryByPk(2,new DalHints().masterOnly());
		assertEquals("update2",daoPojo.getName());
		daoPojo=dao.queryByPk(3,new DalHints().masterOnly());
		assertEquals("update3",daoPojo.getName());
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

	    SqlServerPeopleTable daoPojo=dao.queryByPk(1l, new DalHints());
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
	    SqlServerPeopleTable ret = dao.test_build_queryFirst(CityID, new DalHints());
	    assertEquals("Slave_0",ret.getName());

		ret = dao.test_build_queryFirst(CityID, new DalHints().slaveOnly());
		assertEquals("Slave_0",ret.getName());

	    ret = dao.test_build_queryFirst(CityID, new DalHints().masterOnly());
	    assertEquals("Master_0",ret.getName());

	    ret=dao.test_build_queryFirst(CityID,new DalHints().freshness(3));
	    assertEquals("Slave_0",ret.getName());

	    ret=dao.test_build_queryFirst(CityID,new DalHints().freshness(2));
	    assertEquals("Slave_0",ret.getName());

	    ret=dao.test_build_queryFirst(CityID,new DalHints().freshness(1));
	    assertEquals("Master_0",ret.getName());

	    ret=dao.test_build_queryFirst(CityID,new DalHints().freshness(3).masterOnly());
	    assertEquals("Master_0",ret.getName());
	}

	@Test
	public void testtest_build_queryList() throws Exception {
		List<Integer> CityID=new ArrayList<Integer>();
		CityID.add(20);
		CityID.add(22);
		CityID.add(30);
	    List<SqlServerPeopleTable> ret = dao.test_build_queryList(CityID, new DalHints());
	    assertEquals(1, ret.size());

		ret = dao.test_build_queryList(CityID, new DalHints().slaveOnly());
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
	    SqlServerPeopleTable ret = dao.test_build_querySingle(CityID, new DalHints());
	    assertEquals("Slave_0",ret.getName());

		ret = dao.test_build_querySingle(CityID, new DalHints().slaveOnly());
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

		ret = dao.test_build_queryFieldFirst(CityID, new DalHints().slaveOnly());
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

		ret = dao.test_build_queryFieldList(CityID, new DalHints().slaveOnly());
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

		ret = dao.test_build_queryFieldSingle(CityID, new DalHints().slaveOnly());
		assertEquals("Slave_0", ret);

	    ret = dao.test_build_queryFieldSingle(CityID, new DalHints().masterOnly());
	    assertEquals("Master_0", ret);
	}

	@Test
	public void testtest_def_truncate() throws Exception {
	    dao.test_def_truncate(new DalHints());
	    int affected = dao.count(new DalHints());
		assertEquals(6, affected);

		affected = dao.count(new DalHints().slaveOnly());
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
		List<SqlServerPeopleTable> ret = dao.test_def_queryList(CityID, new DalHints());
		assertEquals(1, ret.size());

		ret = dao.test_def_queryList(CityID, new DalHints().slaveOnly());
		assertEquals(1, ret.size());

		ret = dao.test_def_queryList(CityID, new DalHints().masterOnly());
		assertEquals(2, ret.size());

	}
}
