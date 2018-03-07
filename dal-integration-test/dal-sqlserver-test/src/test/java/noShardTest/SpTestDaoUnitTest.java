package noShardTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;



import org.junit.*;
import static org.junit.Assert.*;
import com.ctrip.platform.dal.dao.*;

/**
 * JUnit test of SpTestDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class SpTestDaoUnitTest {

	private static final String DATA_BASE = "noShardTestOnSqlServer";

	private static DalClient client = null;
	private static SpTestDao dao = null;
	
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
		dao = new SpTestDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
        dao.cleanData();
		List<SpTest> pojos=new ArrayList<>();
		for(int i=0;i<10;i++){
			SpTest pojo=new SpTest();
			pojo.setColumn1("setUp"+i);
			pojo.setColumn2(i);
			pojo.setColumn4(Timestamp.valueOf("2017-8-4 12:23:36"));
			pojos.add(pojo);
		}
		dao.insert(pojos);
	}
	
	private SpTest createPojo(int index) {
		SpTest daoPojo = new SpTest();

		//daoPojo.setId(index);
		//daoPojo set not null field
		
		return daoPojo;
	}

	private void changePojo(SpTest daoPojo) {
		// Change a field to make pojo different with original one
	}
	
	private void changePojos(List<SpTest> daoPojos) {
		for(SpTest daoPojo: daoPojos)
			changePojo(daoPojo);
	}
	
	private void verifyPojo(SpTest daoPojo) {
		//assert changed value
	}
	
	private void verifyPojos(List<SpTest> daoPojos) {
		for(SpTest daoPojo: daoPojos)
			verifyPojo(daoPojo);
	}
	
	@After
	public void tearDown() throws Exception {
//		To clean up all test data
//		dao.delete(null, dao.queryAll(null));
//		System.out.println("Test case ended");
	} 
	
	
	@Test
	public void testCount() throws Exception {
		int affected = dao.count(new DalHints());
		assertEquals(10, affected);
	}
	
	@Test
	public void testDelete1() throws Exception {
	    DalHints hints = new DalHints();
		SpTest daoPojo = new SpTest();
		daoPojo.setID(10);
        dao.delete(daoPojo);
		assertNull(dao.queryByPk(10));
	}
	
	@Test
	public void testDelete2() throws Exception {
		DalHints hints = new DalHints();
		List<SpTest> daoPojos = dao.queryAll(null);
		dao.delete(hints, daoPojos);
		assertEquals(0,dao.count());
	}
	
	@Test
	public void testBatchDelete() throws Exception {
//		DalHints hints = new DalHints();
//		List<SpTest> daoPojos = dao.queryAll(null);
//
//		dao.batchDelete(hints, daoPojos);
//		assertEquals(0,  dao.count());
	}
	
	@Test
	public void testQueryAll() throws Exception {
		List<SpTest> list = dao.queryAll(new DalHints());
		assertEquals(10, list.size());
	}
	
	@Test
	public void testInsert1() throws Exception {
//		System.out.println("Waiting 10 seconds to start test...");
//		try {
//			TimeUnit.SECONDS.sleep(10);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test started at %d", start));

		SpTest daoPojo = new SpTest();
		daoPojo.setColumn1("insert1");
//		for(int i=0;i<10000;i++){
			dao.insert(daoPojo);
//		}

//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test finished at %d, costs %d ms", end, end - start));


		assertEquals(11, dao.count());
		assertEquals("insert1",dao.queryByPk(11).getColumn1());
	}
	
	@Test
	public void testInsert2() throws Exception {
		DalHints hints = new DalHints();
		List<SpTest> daoPojos = dao.queryAll(new DalHints());
		dao.insert(hints, daoPojos);
		assertEquals(20,  dao.count());
		assertEquals(20,dao.queryByPk(20).getID().intValue());

//		System.out.println("Waiting 10 seconds to start test...");
//		try {
//			TimeUnit.SECONDS.sleep(10);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test started at %d", start));
//
//		DalHints hints = new DalHints();
//		List<SpTest> daoPojos = dao.queryAll(new DalHints());
//		for(int i=0;i<1000;i++){
//			dao.insert(hints, daoPojos);
//		}
//
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test finished at %d, costs %d ms", end, end - start));
//
//
//		assertEquals(10010,dao.count());
	}
	
	@Test
	public void testInsert3() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		SpTest daoPojo = createPojo(1);
		dao.insert(hints, keyHolder, daoPojo);
		assertEquals(1, keyHolder.size());
		assertEquals(11,keyHolder.getKey());
	}
	
	@Test
	public void testInsert4() throws Exception {
		DalHints hints = new DalHints();
		KeyHolder keyHolder = new KeyHolder();
		List<SpTest> daoPojos = dao.queryAll(new DalHints());
		dao.insert(hints, keyHolder, daoPojos);
		assertEquals(20,  dao.count());
		assertEquals(10, keyHolder.size());
		assertEquals(11, keyHolder.getKey(0));
	}
	
	@Test
	public void testInsert5() throws Exception {
//		System.out.println("Waiting 10 seconds to start test...");
//		try {
//			TimeUnit.SECONDS.sleep(10);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test started at %d", start));

//		DalHints hints = new DalHints();
//		List<SpTest> daoPojos = dao.queryAll(new DalHints());
//		for(int i=0;i<1000;i++){
//			dao.batchInsert(hints, daoPojos);
//		}

//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test finished at %d, costs %d ms", end, end - start));


//		assertEquals(10010,dao.count());
	}
	
	@Test
	public void testQueryAllByPage() throws Exception {
		DalHints hints = new DalHints();
		int pageSize = 100;
		int pageNo = 1;
		List<SpTest> list = dao.queryAllByPage(pageNo, pageSize, hints);
		assertEquals(10, list.size());
	}
	
	@Test
	public void testQueryByPk1() throws Exception {
		Number id = 1;
		DalHints hints = new DalHints();
		SpTest affected = dao.queryByPk(id, hints);
		assertNotNull(affected);
	}
	
	@Test
	public void testQueryByPk2() throws Exception {
		SpTest pk = createPojo(1);
		pk.setID(6);
		DalHints hints = new DalHints();
		SpTest affected = dao.queryByPk(pk, hints);
		assertNotNull(affected);
	}
	
	@Test
	public void testUpdate1() throws Exception {

//		System.out.println("Waiting 10 seconds to start test...");
//		try {
//			TimeUnit.SECONDS.sleep(10);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test started at %d", start));

		DalHints hints = new DalHints();
		SpTest daoPojo = new SpTest();
//		for(int i=0;i<10000;i++) {
			daoPojo.setID(1);
			daoPojo.setColumn1("update1");
			dao.update(hints, daoPojo);
//		}

//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test finished at %d, costs %d ms", end, end - start));

		daoPojo = dao.queryByPk(1, null);
		assertEquals("update1",daoPojo.getColumn1());

//		assertEquals(10000,dao.count());
	}
	
	@Test
	public void testUpdate2() throws Exception {


		DalHints hints = new DalHints();
		List<SpTest> daoPojos = new ArrayList<>();

		for(int i=0;i<10;i++){
			SpTest pojo=new SpTest();
		pojo.setID(i+1);
		pojo.setColumn1("update2");
		daoPojos.add(pojo);
		}

//		System.out.println("Waiting 10 seconds to start test...");

//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test started at %d", start));

		dao.update(hints, daoPojos);

//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test finished at %d, costs %d ms", end, end - start));

		assertEquals("update2",dao.queryByPk(2).getColumn1());
		assertEquals("update2",dao.queryByPk(6).getColumn1());
		assertEquals("update2",dao.queryByPk(10).getColumn1());
	}
	
	@Test
	public void testBatchUpdate() throws Exception {
//		DalHints hints = new DalHints();
//		List<SpTest> daoPojos = new ArrayList<>();
//
//		for(int i=0;i<10;i++){
//			SpTest pojo=new SpTest();
//			pojo.setID(i+1);
//			pojo.setColumn1("batchUpdate1");
//			daoPojos.add(pojo);
//		}

//		System.out.println("Waiting 10 seconds to start test...");

//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test started at %d", start));

//		dao.batchUpdate(hints, daoPojos);

//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test finished at %d, costs %d ms", end, end - start));

//		assertEquals("batchUpdate1",dao.queryByPk(2).getColumn1());
//		assertEquals("batchUpdate1",dao.queryByPk(6).getColumn1());
//		assertEquals("batchUpdate1",dao.queryByPk(10).getColumn1());
	}

}
