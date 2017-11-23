package noShardTest;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import org.junit.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit test of SpTestDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class SpInsertPerformanceTest {

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
//		List<SpTest> pojos=new ArrayList<>();
		/*data setup for update performance test*/
		//for(int i=0li<10000;i++){
//		for(int i=0;i<10;i++){
//			SpTest pojo=new SpTest();
//			pojo.setColumn1("setUp"+i);
//			pojo.setColumn2(i);
//			pojo.setColumn4(Timestamp.valueOf("2017-8-4 12:23:36"));
//			pojos.add(pojo);
//		}
//		dao.insert(pojos);
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
	
	
//	@Test
//	public void testCount() throws Exception {
//		int affected = dao.count(new DalHints());
//		assertEquals(10, affected);
//	}
//
//	@Test
//	public void testDelete1() throws Exception {
//	    DalHints hints = new DalHints();
//		SpTest daoPojo = new SpTest();
//		daoPojo.setID(10);
//        dao.delete(daoPojo);
//		assertNull(dao.queryByPk(10));
//	}
//
//	@Test
//	public void testDelete2() throws Exception {
//		DalHints hints = new DalHints();
//		List<SpTest> daoPojos = dao.queryAll(null);
//		dao.delete(hints, daoPojos);
//		assertEquals(0,dao.count());
//	}
//
//	@Test
//	public void testBatchDelete() throws Exception {
//		//entity字段少于表字段，不支持tvp
////		DalHints hints = new DalHints();
////		List<SpTest> daoPojos = dao.queryAll(null);
////
////		dao.batchDelete(hints, daoPojos);
////		assertEquals(0,  dao.count());
//	}
//
//	@Test
//	public void testQueryAll() throws Exception {
//		List<SpTest> list = dao.queryAll(new DalHints());
//		assertEquals(10, list.size());
//	}
	
//	@Test
//	public void testInsert1() throws Exception {
//		/*performance test insert*/
//		System.out.println("start test1...");
//		SpTest daoPojo = new SpTest();
//		daoPojo.setColumn1("insert1");
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test1 started at %d", start));
//		for(int i=0;i<10000;i++){
//			dao.insert(daoPojo);
//		}
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test1 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000, dao.count());
//		assertEquals("insert1",dao.queryByPk(400).getColumn1());
//
//	}
//
//	@Test
//	public void testInsert2() throws Exception {
//		/*performance test*/
//		System.out.println("start test2...");
//		SpTest daoPojo = new SpTest();
//		daoPojo.setColumn1("insert2");
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test2 started at %d", start));
//		for(int i=0;i<10000;i++){
//			dao.insert(daoPojo);
//		}
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test2 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000, dao.count());
//		assertEquals("insert2",dao.queryByPk(400).getColumn1());
//
//	}
//
//	@Test
//	public void testInsert3() throws Exception {
//		/*performance test*/
//		System.out.println("start test3...");
//		SpTest daoPojo = new SpTest();
//		daoPojo.setColumn1("insert3");
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test3 started at %d", start));
//		for(int i=0;i<10000;i++){
//			dao.insert(daoPojo);
//		}
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test3 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000, dao.count());
//		assertEquals("insert3",dao.queryByPk(400).getColumn1());
//
//	}
//
//	@Test
//	public void testInsert4() throws Exception {
//		/*performance test*/
//		System.out.println("start test4...");
//		SpTest daoPojo = new SpTest();
//		daoPojo.setColumn1("insert4");
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test4 started at %d", start));
//		for(int i=0;i<10000;i++){
//			dao.insert(daoPojo);
//		}
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test4 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000, dao.count());
//		assertEquals("insert4",dao.queryByPk(400).getColumn1());
//
//	}
//
//	@Test
//	public void testInsert5() throws Exception {
//		/*performance test*/
//		System.out.println("start test5...");
//		SpTest daoPojo = new SpTest();
//		daoPojo.setColumn1("insert5");
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test5 started at %d", start));
//		for(int i=0;i<10000;i++){
//			dao.insert(daoPojo);
//		}
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test5 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000, dao.count());
//		assertEquals("insert5",dao.queryByPk(400).getColumn1());
//
//	}
//
//	@Test
//	public void testInsert6() throws Exception {
//		/*performance test*/
//		System.out.println("start test6...");
//		SpTest daoPojo = new SpTest();
//		daoPojo.setColumn1("insert6");
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test6 started at %d", start));
//		for(int i=0;i<10000;i++){
//			dao.insert(daoPojo);
//		}
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test6 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000, dao.count());
//		assertEquals("insert6",dao.queryByPk(400).getColumn1());
//
//	}
//
//	@Test
//	public void testInsert7() throws Exception {
//		/*performance test*/
//		System.out.println("start test7...");
//		SpTest daoPojo = new SpTest();
//		daoPojo.setColumn1("insert7");
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test7 started at %d", start));
//		for(int i=0;i<10000;i++){
//			dao.insert(daoPojo);
//		}
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test7 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000, dao.count());
//		assertEquals("insert7",dao.queryByPk(400).getColumn1());
//
//	}
//
//	@Test
//	public void testInsert8() throws Exception {
//		/*performance test*/
//		System.out.println("start test8...");
//		SpTest daoPojo = new SpTest();
//		daoPojo.setColumn1("insert8");
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test8 started at %d", start));
//		for(int i=0;i<10000;i++){
//			dao.insert(daoPojo);
//		}
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test8 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000, dao.count());
//		assertEquals("insert8",dao.queryByPk(400).getColumn1());
//
//	}
//
//	@Test
//	public void testInsert9() throws Exception {
//		/*performance test*/
//		System.out.println("start test9...");
//		SpTest daoPojo = new SpTest();
//		daoPojo.setColumn1("insert9");
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test9 started at %d", start));
//		for(int i=0;i<10000;i++){
//			dao.insert(daoPojo);
//		}
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test9 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000, dao.count());
//		assertEquals("insert9",dao.queryByPk(400).getColumn1());
//
//	}
//
//	@Test
//	public void testInsert10() throws Exception {
//		/*performance test*/
//		System.out.println("start test10...");
//		SpTest daoPojo = new SpTest();
//		daoPojo.setColumn1("insert10");
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test10 started at %d", start));
//		for(int i=0;i<10000;i++){
//			dao.insert(daoPojo);
//		}
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test10 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000, dao.count());
//		assertEquals("insert10",dao.queryByPk(400).getColumn1());
//
//	}

	@Test
	public void testInsertList1() throws Exception {
		/*performance test insert list*/
		System.out.println("start test1...");
		List<SpTest> daoPojos = new ArrayList<>();
		SpTest pojo=new SpTest();
		for(int i=0;i<10000;i++){
			pojo.setColumn1("insertList1");
			daoPojos.add(pojo);
		}
		DalHints hints = new DalHints();
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test1 started at %d", start));
		dao.insert(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test1 finished at %d, costs %d ms", end, end - start));
		assertEquals(10000,dao.count());
		assertEquals("insertList1",dao.queryByPk(2333).getColumn1());
	}

	@Test
	public void testInsertList2() throws Exception {
		/*performance test insert list*/
		System.out.println("start test2...");
		List<SpTest> daoPojos = new ArrayList<>();
		SpTest pojo=new SpTest();
		for(int i=0;i<10000;i++){
			pojo.setColumn1("insertList2");
			daoPojos.add(pojo);
		}
		DalHints hints = new DalHints();
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test2 started at %d", start));
		dao.insert(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test2 finished at %d, costs %d ms", end, end - start));
		assertEquals(10000,dao.count());
		assertEquals("insertList2",dao.queryByPk(2333).getColumn1());
	}

	@Test
	public void testInsertList3() throws Exception {
		/*performance test insert list*/
		System.out.println("start test3...");
		List<SpTest> daoPojos = new ArrayList<>();
		SpTest pojo=new SpTest();
		for(int i=0;i<10000;i++){
			pojo.setColumn1("insertList3");
			daoPojos.add(pojo);
		}
		DalHints hints = new DalHints();
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test3 started at %d", start));
		dao.insert(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test3 finished at %d, costs %d ms", end, end - start));
		assertEquals(10000,dao.count());
		assertEquals("insertList3",dao.queryByPk(2333).getColumn1());
	}

	@Test
	public void testInsertList4() throws Exception {
		/*performance test insert list*/
		System.out.println("start test4...");
		List<SpTest> daoPojos = new ArrayList<>();
		SpTest pojo=new SpTest();
		for(int i=0;i<10000;i++){
			pojo.setColumn1("insertList4");
			daoPojos.add(pojo);
		}
		DalHints hints = new DalHints();
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test4 started at %d", start));
		dao.insert(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test4 finished at %d, costs %d ms", end, end - start));
		assertEquals(10000,dao.count());
		assertEquals("insertList4",dao.queryByPk(2333).getColumn1());
	}

	@Test
	public void testInsertList5() throws Exception {
		/*performance test insert list*/
		System.out.println("start test5...");
		List<SpTest> daoPojos = new ArrayList<>();
		SpTest pojo=new SpTest();
		for(int i=0;i<10000;i++){
			pojo.setColumn1("insertList5");
			daoPojos.add(pojo);
		}
		DalHints hints = new DalHints();
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test5 started at %d", start));
		dao.insert(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test5 finished at %d, costs %d ms", end, end - start));
		assertEquals(10000,dao.count());
		assertEquals("insertList5",dao.queryByPk(2333).getColumn1());
	}

	@Test
	public void testInsertList6() throws Exception {
		/*performance test insert list*/
		System.out.println("start test6...");
		List<SpTest> daoPojos = new ArrayList<>();
		SpTest pojo=new SpTest();
		for(int i=0;i<10000;i++){
			pojo.setColumn1("insertList6");
			daoPojos.add(pojo);
		}
		DalHints hints = new DalHints();
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test6 started at %d", start));
		dao.insert(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test6 finished at %d, costs %d ms", end, end - start));
		assertEquals(10000,dao.count());
		assertEquals("insertList6",dao.queryByPk(2333).getColumn1());
	}

	@Test
	public void testInsertList7() throws Exception {

		/*performance test insert list*/
		System.out.println("start test7...");
		List<SpTest> daoPojos = new ArrayList<>();
		SpTest pojo=new SpTest();
		for(int i=0;i<10000;i++){
			pojo.setColumn1("insertList7");
			daoPojos.add(pojo);
		}
		DalHints hints = new DalHints();
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test7 started at %d", start));
		dao.insert(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test7 finished at %d, costs %d ms", end, end - start));
		assertEquals(10000,dao.count());
		assertEquals("insertList7",dao.queryByPk(2333).getColumn1());

	}

	@Test
	public void testInsertList8() throws Exception {


		/*performance test insert list*/
		System.out.println("start test8...");
		List<SpTest> daoPojos = new ArrayList<>();
		SpTest pojo=new SpTest();
		for(int i=0;i<10000;i++){
			pojo.setColumn1("insertList8");
			daoPojos.add(pojo);
		}
		DalHints hints = new DalHints();
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test8 started at %d", start));
		dao.insert(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test8 finished at %d, costs %d ms", end, end - start));
		assertEquals(10000,dao.count());
		assertEquals("insertList8",dao.queryByPk(2333).getColumn1());

	}

	@Test
	public void testInsertList9() throws Exception {
		/*performance test insert list*/
		System.out.println("start test9...");
		List<SpTest> daoPojos = new ArrayList<>();
		SpTest pojo=new SpTest();
		for(int i=0;i<10000;i++){
			pojo.setColumn1("insertList9");
			daoPojos.add(pojo);
		}
		DalHints hints = new DalHints();
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test9 started at %d", start));
		dao.insert(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test9 finished at %d, costs %d ms", end, end - start));
		assertEquals(10000,dao.count());
		assertEquals("insertList9",dao.queryByPk(2333).getColumn1());

	}

	@Test
	public void testInsertList10() throws Exception {

		/*performance test insert list*/
		System.out.println("start test10...");
		List<SpTest> daoPojos = new ArrayList<>();
		SpTest pojo=new SpTest();
		for(int i=0;i<10000;i++){
			pojo.setColumn1("insertList10");
			daoPojos.add(pojo);
		}
		DalHints hints = new DalHints();
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test10 started at %d", start));
		dao.insert(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test10 finished at %d, costs %d ms", end, end - start));
		assertEquals(10000,dao.count());
		assertEquals("insertList10",dao.queryByPk(2333).getColumn1());

	}

//	@Test
//	public void testBatchInsert1() throws Exception {
//		/*performance test batchInsert*/
//		System.out.println("start test1...");
//		List<SpTest> daoPojos = new ArrayList<>();
//		SpTest pojo=new SpTest();
//		for(int i=0;i<10000;i++){
//			pojo.setColumn1("batchInsert1");
//			daoPojos.add(pojo);
//		}
//		DalHints hints = new DalHints();
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test1 started at %d", start));
//		dao.batchInsert(hints, daoPojos);
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test1 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000,dao.count());
//		assertEquals("batchInsert1",dao.queryByPk(5333).getColumn1());
//	}
//
//	@Test
//	public void testBatchInsert2() throws Exception {
//
//		/*performance test batchInsert*/
//		System.out.println("start test2...");
//		List<SpTest> daoPojos = new ArrayList<>();
//		SpTest pojo=new SpTest();
//		for(int i=0;i<10000;i++){
//			pojo.setColumn1("batchInsert2");
//			daoPojos.add(pojo);
//		}
//		DalHints hints = new DalHints();
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test2 started at %d", start));
//		dao.batchInsert(hints, daoPojos);
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test2 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000,dao.count());
//		assertEquals("batchInsert2",dao.queryByPk(5333).getColumn1());
//	}
//
//	@Test
//	public void testBatchInsert3() throws Exception {
//		/*performance test batchInsert*/
//		System.out.println("start test3...");
//		List<SpTest> daoPojos = new ArrayList<>();
//		SpTest pojo=new SpTest();
//		for(int i=0;i<10000;i++){
//			pojo.setColumn1("batchInsert3");
//			daoPojos.add(pojo);
//		}
//		DalHints hints = new DalHints();
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test3 started at %d", start));
//		dao.batchInsert(hints, daoPojos);
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test3 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000,dao.count());
//		assertEquals("batchInsert3",dao.queryByPk(5333).getColumn1());
//	}
//
//	@Test
//	public void testBatchInsert4() throws Exception {
//
//		/*performance test batchInsert*/
//		System.out.println("start test4...");
//		List<SpTest> daoPojos = new ArrayList<>();
//		SpTest pojo=new SpTest();
//		for(int i=0;i<10000;i++){
//			pojo.setColumn1("batchInsert4");
//			daoPojos.add(pojo);
//		}
//		DalHints hints = new DalHints();
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test4 started at %d", start));
//		dao.batchInsert(hints, daoPojos);
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test4 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000,dao.count());
//		assertEquals("batchInsert4",dao.queryByPk(5333).getColumn1());
//	}
//
//	@Test
//	public void testBatchInsert5() throws Exception {
//
//		/*performance test batchInsert*/
//		System.out.println("start test5...");
//		List<SpTest> daoPojos = new ArrayList<>();
//		SpTest pojo=new SpTest();
//		for(int i=0;i<10000;i++){
//			pojo.setColumn1("batchInsert5");
//			daoPojos.add(pojo);
//		}
//		DalHints hints = new DalHints();
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test5 started at %d", start));
//		dao.batchInsert(hints, daoPojos);
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test5 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000,dao.count());
//		assertEquals("batchInsert5",dao.queryByPk(5333).getColumn1());
//	}
//
//	@Test
//	public void testBatchInsert6() throws Exception {
//
//		/*performance test batchInsert*/
//		System.out.println("start test6...");
//		List<SpTest> daoPojos = new ArrayList<>();
//		SpTest pojo=new SpTest();
//		for(int i=0;i<10000;i++){
//			pojo.setColumn1("batchInsert6");
//			daoPojos.add(pojo);
//		}
//		DalHints hints = new DalHints();
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test6 started at %d", start));
//		dao.batchInsert(hints, daoPojos);
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test6 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000,dao.count());
//		assertEquals("batchInsert6",dao.queryByPk(5333).getColumn1());
//	}
//
//	@Test
//	public void testBatchInsert7() throws Exception {
//
//		/*performance test batchInsert*/
//		System.out.println("start test7...");
//		List<SpTest> daoPojos = new ArrayList<>();
//		SpTest pojo=new SpTest();
//		for(int i=0;i<10000;i++){
//			pojo.setColumn1("batchInsert7");
//			daoPojos.add(pojo);
//		}
//		DalHints hints = new DalHints();
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test7 started at %d", start));
//		dao.batchInsert(hints, daoPojos);
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test7 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000,dao.count());
//		assertEquals("batchInsert7",dao.queryByPk(5333).getColumn1());
//	}
//
//	@Test
//	public void testBatchInsert8() throws Exception {
//
//		/*performance test batchInsert*/
//		System.out.println("start test8...");
//		List<SpTest> daoPojos = new ArrayList<>();
//		SpTest pojo=new SpTest();
//		for(int i=0;i<10000;i++){
//			pojo.setColumn1("batchInsert8");
//			daoPojos.add(pojo);
//		}
//		DalHints hints = new DalHints();
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test8 started at %d", start));
//		dao.batchInsert(hints, daoPojos);
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test8 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000,dao.count());
//		assertEquals("batchInsert8",dao.queryByPk(5333).getColumn1());
//	}
//
//	@Test
//	public void testBatchInsert9() throws Exception {
//		/*performance test batchInsert*/
//		System.out.println("start test9...");
//		List<SpTest> daoPojos = new ArrayList<>();
//		SpTest pojo=new SpTest();
//		for(int i=0;i<10000;i++){
//			pojo.setColumn1("batchInsert9");
//			daoPojos.add(pojo);
//		}
//		DalHints hints = new DalHints();
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test9 started at %d", start));
//		dao.batchInsert(hints, daoPojos);
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test9 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000,dao.count());
//		assertEquals("batchInsert9",dao.queryByPk(5333).getColumn1());
//	}
//
//	@Test
//	public void testBatchInsert10() throws Exception {
//		/*performance test batchInsert*/
//		System.out.println("start test10...");
//		List<SpTest> daoPojos = new ArrayList<>();
//		SpTest pojo=new SpTest();
//		for(int i=0;i<10000;i++){
//			pojo.setColumn1("batchInsert10");
//			daoPojos.add(pojo);
//		}
//		DalHints hints = new DalHints();
//		long start = System.currentTimeMillis();
//		System.out.println(String.format("Test10 started at %d", start));
//		dao.batchInsert(hints, daoPojos);
//		long end = System.currentTimeMillis();
//		System.out.println(String.format("Test10 finished at %d, costs %d ms", end, end - start));
//		assertEquals(10000,dao.count());
//		assertEquals("batchInsert10",dao.queryByPk(5333).getColumn1());
//	}
	
//	@Test
//	public void testInsert2() throws Exception {
//		/*function test*/
//		DalHints hints = new DalHints();
//		List<SpTest> daoPojos = dao.queryAll(new DalHints());
//		dao.insert(hints, daoPojos);
//		assertEquals(20,  dao.count());
//		assertEquals(20,dao.queryByPk(20).getID().intValue());
//
//		/*performance test*/
////		System.out.println("start test...");
////		List<SpTest> daoPojos = new ArrayList<>();
////		for(int i=0;i<10;i++){
////			SpTest pojo=new SpTest();
////			pojo.setColumn1("insert2");
////			daoPojos.add(pojo);
////		}
//// 		DalHints hints = new DalHints();
////		long start = System.currentTimeMillis();
////		System.out.println(String.format("Test started at %d", start));
////		for(int i=0;i<1000;i++){
////			dao.insert(hints, daoPojos);
////		}
////		long end = System.currentTimeMillis();
////		System.out.println(String.format("Test finished at %d, costs %d ms", end, end - start));
////		assertEquals(10010,dao.count());
//	}
//
//	@Test
//	public void testInsert3() throws Exception {
//		DalHints hints = new DalHints();
//		KeyHolder keyHolder = new KeyHolder();
//		SpTest daoPojo = createPojo(1);
//		dao.insert(hints, keyHolder, daoPojo);
//		assertEquals(1, keyHolder.size());
//		assertEquals(11,keyHolder.getKey());
//	}
//
//	@Test
//	public void testInsert4() throws Exception {
//		DalHints hints = new DalHints();
//		KeyHolder keyHolder = new KeyHolder();
//		List<SpTest> daoPojos = dao.queryAll(new DalHints());
//		dao.insert(hints, keyHolder, daoPojos);
//		assertEquals(20,  dao.count());
//		assertEquals(10, keyHolder.size());
//		assertEquals(11, keyHolder.getKey(0));
//	}
//
//	@Test
//	public void testInsert5() throws Exception {
//        /*performance test*/
////		System.out.println("start test...");
////		List<SpTest> daoPojos = new ArrayList<>();
////		for(int i=0;i<10;i++){
////			SpTest pojo=new SpTest();
////			pojo.setColumn1("insert2");
////			daoPojos.add(pojo);
////		}
//// 		DalHints hints = new DalHints();
////		long start = System.currentTimeMillis();
////		System.out.println(String.format("Test started at %d", start));
////		for(int i=0;i<1000;i++){
////			dao.batchInsert(hints, daoPojos);
////		}
////		long end = System.currentTimeMillis();
////		System.out.println(String.format("Test finished at %d, costs %d ms", end, end - start));
////		assertEquals(10010,dao.count());
//
//		/*function test*/
////		DalHints hints = new DalHints();
////		List<SpTest> daoPojos = dao.queryAll(new DalHints());
////		dao.batchInsert(hints, daoPojos);
////		assertEquals(20,  dao.count());
////		assertEquals(20,dao.queryByPk(20).getID().intValue());
//	}
//
//	@Test
//	public void testQueryAllByPage() throws Exception {
//		DalHints hints = new DalHints();
//		int pageSize = 100;
//		int pageNo = 1;
//		List<SpTest> list = dao.queryAllByPage(pageNo, pageSize, hints);
//		assertEquals(10, list.size());
//	}
//
//	@Test
//	public void testQueryByPk1() throws Exception {
//		Number id = 1;
//		DalHints hints = new DalHints();
//		SpTest affected = dao.queryByPk(id, hints);
//		assertNotNull(affected);
//	}
//
//	@Test
//	public void testQueryByPk2() throws Exception {
//		SpTest pk = createPojo(1);
//		pk.setID(6);
//		DalHints hints = new DalHints();
//		SpTest affected = dao.queryByPk(pk, hints);
//		assertNotNull(affected);
//	}
//
//	@Test
//	public void testUpdate1() throws Exception {
//        /*performance test*/
////		System.out.println("start test...");
////		long start = System.currentTimeMillis();
////		System.out.println(String.format("Test started at %d", start));
//
////		DalHints hints = new DalHints();
////		SpTest daoPojo = new SpTest();
////		for(int i=0;i<10000;i++) {
////			daoPojo.setID(i+1);
////			daoPojo.setColumn1("update1");
////			dao.update(hints, daoPojo);
////		}
//
////		long end = System.currentTimeMillis();
////		System.out.println(String.format("Test finished at %d, costs %d ms", end, end - start));
//
////		daoPojo = dao.queryByPk(400, null);
////		assertEquals("update1",daoPojo.getColumn1());
//
//
//        /*function test*/
//		DalHints hints = new DalHints();
//		SpTest daoPojo = new SpTest();
//		daoPojo.setID(1);
//		daoPojo.setColumn1("update1");
//		dao.update(hints, daoPojo);
//		daoPojo = dao.queryByPk(1, null);
//		assertEquals("update1",daoPojo.getColumn1());
//	}
//
//	@Test
//	public void testUpdate2() throws Exception {
//
//        /*performance test*/
////		DalHints hints = new DalHints();
////		List<SpTest> daoPojos = new ArrayList<>();
////
////		for(int i=0;i<10000;i++){
////			SpTest pojo=new SpTest();
////		pojo.setID(i+1);
////		pojo.setColumn1("update2");
////		daoPojos.add(pojo);
////		}
//
////		System.out.println("start test...");
//
////		long start = System.currentTimeMillis();
////		System.out.println(String.format("Test started at %d", start));
//
////		dao.update(hints, daoPojos);
//
////		long end = System.currentTimeMillis();
////		System.out.println(String.format("Test finished at %d, costs %d ms", end, end - start));
//
////		assertEquals("update2",dao.queryByPk(200).getColumn1());
////		assertEquals("update2",dao.queryByPk(600).getColumn1());
////		assertEquals("update2",dao.queryByPk(1000).getColumn1());
//
//
//
//		/*function test*/
//		DalHints hints = new DalHints();
//		List<SpTest> daoPojos = new ArrayList<>();
//
//		for(int i=0;i<10;i++){
//			SpTest pojo=new SpTest();
//			pojo.setID(i+1);
//			pojo.setColumn1("update2");
//			daoPojos.add(pojo);
//		}
//		dao.update(hints, daoPojos);
//		assertEquals("update2",dao.queryByPk(2).getColumn1());
//		assertEquals("update2",dao.queryByPk(6).getColumn1());
//		assertEquals("update2",dao.queryByPk(10).getColumn1());
//	}
//
//	@Test
//	public void testBatchUpdate() throws Exception {
//		/*performance test*/
////		DalHints hints = new DalHints();
////		List<SpTest> daoPojos = new ArrayList<>();
////
////		for(int i=0;i<10000;i++){
////			SpTest pojo=new SpTest();
////			pojo.setID(i+1);
////			pojo.setColumn1("batchUpdate1");
////			daoPojos.add(pojo);
////		}
//
////		System.out.println("start test...");
//
////		long start = System.currentTimeMillis();
////		System.out.println(String.format("Test started at %d", start));
//
////		dao.batchUpdate(hints, daoPojos);
//
////		long end = System.currentTimeMillis();
////		System.out.println(String.format("Test finished at %d, costs %d ms", end, end - start));
//
////		assertEquals("batchUpdate1",dao.queryByPk(200).getColumn1());
////		assertEquals("batchUpdate1",dao.queryByPk(600).getColumn1());
////		assertEquals("batchUpdate1",dao.queryByPk(10000).getColumn1());
//
//		/*function test*/
//		DalHints hints = new DalHints();
//		List<SpTest> daoPojos = new ArrayList<>();
//
//		for(int i=0;i<10;i++){
//			SpTest pojo=new SpTest();
//			pojo.setID(i+1);
//			pojo.setColumn1("batchUpdate");
//			daoPojos.add(pojo);
//		}
//		dao.batchUpdate(hints, daoPojos);
//		assertEquals("batchUpdate",dao.queryByPk(2).getColumn1());
//		assertEquals("batchUpdate",dao.queryByPk(6).getColumn1());
//		assertEquals("batchUpdate",dao.queryByPk(10).getColumn1());
//	}

}
