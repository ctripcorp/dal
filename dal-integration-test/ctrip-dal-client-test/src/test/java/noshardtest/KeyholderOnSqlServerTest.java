package noshardtest;



import dao.noshard.KeyholderTestOnSqlServerDao;
import entity.SqlServerKeyholderTestTable;
import org.junit.*;
import static org.junit.Assert.*;
import com.ctrip.platform.dal.dao.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JUnit test of SqlServerKeyholderTestTableOnSqlServerDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class KeyholderOnSqlServerTest {

	private static final String DATA_BASE = "DalServiceDB";

	private static DalClient client = null;
	private static KeyholderTestOnSqlServerDao dao = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/**
		* Initialize DalClientFactory.
		* The Dal.config can be specified from class-path or local file path.
		* One of follow three need to be enabled.
		**/
//		DalClientFactory.initClientFactory(); // load from class-path Dal.config
//		DalClientFactory.warmUpConnections();
//		client = DalClientFactory.getClient(DATA_BASE);
		dao = new KeyholderTestOnSqlServerDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
		dao.test_def_update(new DalHints());


		List<SqlServerKeyholderTestTable> daoPojos1 = new ArrayList<SqlServerKeyholderTestTable>(3);
		for (int i = 0; i < 6; i++) {
			SqlServerKeyholderTestTable daoPojo = new SqlServerKeyholderTestTable();
			daoPojo.setPeopleID(Long.valueOf(i) + 1);
			daoPojo.setName("Initial_" + i);
			daoPojo.setCityID(i + 20);
			daoPojo.setProvinceID(i + 30);
			daoPojos1.add(daoPojo);
		}
		dao.insert(new DalHints(), daoPojos1);
	}
	
	@After
	public void tearDown() throws Exception {
//		To clean up all test data
//		dao.delete(null, dao.queryAll(null));
	} 
	
	
	@Test
	public void testCount() throws Exception {
		int affected = dao.count(new DalHints());
		assertEquals(6, affected);
	}
	
	@Test
	public void testDelete1() throws Exception {
	    DalHints hints = new DalHints();
		SqlServerKeyholderTestTable daoPojo = new SqlServerKeyholderTestTable();
		daoPojo.setPeopleID(2l);
		/**
		 * WARNING !!!
		 * To test delete, please make sure you can easily restore all the data. otherwise data will not be revovered.
		 */
		dao.delete(hints, daoPojo); 
		assertEquals(5,dao.count());
	}
	
	@Test
	public void testDelete2() throws Exception {
		DalHints hints = new DalHints();
		List<SqlServerKeyholderTestTable> daoPojos = dao.queryAll(null);
		/**
		 * WARNING !!!
		 * To test delete, please make sure you can easily restore all the data. otherwise data will not be revovered.
		 */

		dao.delete(hints, daoPojos);
		assertEquals(0,dao.count());
	}
	
	@Test
	public void testBatchDelete() throws Exception {
		DalHints hints = new DalHints();
		List<SqlServerKeyholderTestTable> daoPojos = dao.queryAll(null);
		/**
		 * WARNING !!!
		 * To test batchDelete, please make sure you can easily restore all the data. otherwise data will not be revovered.
		 */
		dao.batchDelete(hints, daoPojos);
		assertEquals(0,dao.count());
	}
	
	@Test
	public void testQueryAll() throws Exception {
		List<SqlServerKeyholderTestTable> list = dao.queryAll(new DalHints());
		assertEquals(6, list.size());
	}
	
	@Test
	public void testInsert1() throws Exception {
		DalHints hints = new DalHints();
		SqlServerKeyholderTestTable daoPojo = new SqlServerKeyholderTestTable();
		daoPojo.setPeopleID(7L);
		daoPojo.setName("insert1");
		int affected = dao.insert(hints, daoPojo);

		SqlServerKeyholderTestTable insertPojo=dao.queryByPk(7L,null);
		assertEquals("insert1", insertPojo.getName());

		SqlServerKeyholderTestTable daoPojo1 = new SqlServerKeyholderTestTable();
		daoPojo1.setPeopleID(8L);
		daoPojo1.setName("insert2");
		dao.insert(hints.setIdentityBack(), daoPojo1);

		SqlServerKeyholderTestTable insertPojo2=dao.queryByPk(8L,null);
		assertEquals("insert2", insertPojo2.getName());
	}
	
	@Test
	public void testInsert2() throws Exception {
		DalHints hints = new DalHints();
		List<SqlServerKeyholderTestTable> daoPojos = new ArrayList<>();

		SqlServerKeyholderTestTable daoPojo1 = new SqlServerKeyholderTestTable();
		daoPojo1.setPeopleID(7L);
		daoPojo1.setName("insert1");
		SqlServerKeyholderTestTable daoPojo2 = new SqlServerKeyholderTestTable();
		daoPojo2.setPeopleID(8L);
		daoPojo2.setName("insert2");
		daoPojos.add(daoPojo1);
		daoPojos.add(daoPojo2);

		dao.insert(hints, daoPojos);
		assertEquals(8,dao.count());

		daoPojos.get(0).setPeopleID(9l);
		daoPojos.get(1).setPeopleID(10l);
		dao.insert(hints.setIdentityBack(), daoPojos);
		assertEquals(10,dao.count());
	}
	
	@Test
	public void testInsert3() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		SqlServerKeyholderTestTable daoPojo = new SqlServerKeyholderTestTable();
		daoPojo.setPeopleID(7L);
		daoPojo.setName("insert1");

		dao.insert(hints, keyHolder, daoPojo);
		assertEquals(7, dao.count());
	}
	
	@Test
	public void testInsert4() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<SqlServerKeyholderTestTable> daoPojos = new ArrayList<>();

		SqlServerKeyholderTestTable daoPojo1 = new SqlServerKeyholderTestTable();
		daoPojo1.setPeopleID(7L);
		daoPojo1.setName("insert1");
		SqlServerKeyholderTestTable daoPojo2 = new SqlServerKeyholderTestTable();
		daoPojo2.setPeopleID(8L);
		daoPojo2.setName("insert2");
		daoPojos.add(daoPojo1);
		daoPojos.add(daoPojo2);
		dao.insert(hints, keyHolder, daoPojos);

		assertEquals(8, dao.count(null));
	}
	
	@Test
	public void testInsert5() throws Exception {
		DalHints hints = new DalHints();
		List<SqlServerKeyholderTestTable> daoPojos = new ArrayList<>();

		SqlServerKeyholderTestTable daoPojo1 = new SqlServerKeyholderTestTable();
		daoPojo1.setPeopleID(7L);
		daoPojo1.setName("insert1");
		SqlServerKeyholderTestTable daoPojo2 = new SqlServerKeyholderTestTable();
		daoPojo2.setPeopleID(8L);
		daoPojo2.setName("insert2");
		daoPojos.add(daoPojo1);
		daoPojos.add(daoPojo2);

		dao.batchInsert(hints, daoPojos);
		assertEquals(8, dao.count(null));
	}
	
	@Test
	public void testQueryAllByPage() throws Exception {
		DalHints hints = new DalHints();
		int pageSize = 100;
		int pageNo = 1;
		List<SqlServerKeyholderTestTable> list = dao.queryAllByPage(pageNo, pageSize, hints);
		assertEquals(6, list.size());
	}
	
	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 1;
		DalHints hints = new DalHints();
		SqlServerKeyholderTestTable affected = dao.queryByPk(id, hints);
		assertEquals("Initial_1",affected.getName());
	}
	
	@Test
	public void testQueryByPk2() throws Exception {
		SqlServerKeyholderTestTable pk = new SqlServerKeyholderTestTable();
		pk.setPeopleID(2L);
		DalHints hints = new DalHints();
		SqlServerKeyholderTestTable affected = dao.queryByPk(pk, hints);
		assertEquals("Initial_2",affected.getName());
	}
	
	@Test
	public void testUpdate1() throws Exception {
		DalHints hints = new DalHints();
		SqlServerKeyholderTestTable daoPojo = dao.queryByPk(1, hints);
		daoPojo.setName("update1");
		dao.update(hints, daoPojo);

		daoPojo = dao.queryByPk(1, null);
		assertEquals("update1",daoPojo.getName());
	}
	
	@Test
	public void testUpdate2() throws Exception {
		DalHints hints = new DalHints();
		List<SqlServerKeyholderTestTable> daoPojos = dao.queryAll(new DalHints());
		daoPojos.get(1).setName("update1");
		daoPojos.get(4).setName("update4");
		dao.update(hints, daoPojos);

		assertEquals("update1",dao.queryByPk(2).getName());
		assertEquals("update4",dao.queryByPk(5).getName());
	}
	
	@Test
	public void testBatchUpdate() throws Exception {
		DalHints hints = new DalHints();
		List<SqlServerKeyholderTestTable> daoPojos = dao.queryAll(new DalHints());
		daoPojos.get(1).setName("update1");
		daoPojos.get(4).setName("update4");
		dao.batchUpdate(hints, daoPojos);
		assertEquals("update1",dao.queryByPk(2).getName());
		assertEquals("update4",dao.queryByPk(5).getName());
	}

}
