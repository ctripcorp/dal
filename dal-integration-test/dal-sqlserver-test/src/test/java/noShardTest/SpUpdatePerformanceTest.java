package noShardTest;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import org.junit.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by lilj on 2017/8/10.
 */
public class SpUpdatePerformanceTest {


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

            dao.cleanData();
		/*data setup for update performance test*/
            List<SpTest> pojos=new ArrayList<>();

            for(int i=0;i<10000;i++){
                SpTest pojo=new SpTest();
                pojo.setColumn1("setUp");
                pojo.setColumn2(i);
                pojo.setColumn4(Timestamp.valueOf("2017-8-10 12:23:36"));
                pojos.add(pojo);
            }
            dao.batchInsert(pojos);
            System.out.println("data set up");
        }

        @AfterClass
        public static void tearDownAfterClass() throws Exception {

        }

        @Before
        public void setUp() throws Exception {
//        dao.cleanData();

        }

        @After
        public void tearDown() throws Exception {
//		To clean up all test data
//		dao.delete(null, dao.queryAll(null));
//		System.out.println("Test case ended");
        }

    @Test
    public void testUpdate1() throws Exception {
        /*performance test update*/
        System.out.println("start test1...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test1 started at %d", start));

        DalHints hints = new DalHints();
        SpTest daoPojo = new SpTest();
        for(int i=0;i<10000;i++) {
            daoPojo.setID(i+1);
            daoPojo.setColumn1("update1");
            dao.update(hints, daoPojo);
        }

        long end = System.currentTimeMillis();
        System.out.println(String.format("Test1 finished at %d, costs %d ms", end, end - start));

        daoPojo = dao.queryByPk(400, null);
        assertEquals("update1",daoPojo.getColumn1());

    }

    @Test
    public void testUpdate2() throws Exception {
        /*performance test update*/
        System.out.println("start test2...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test2 started at %d", start));

        DalHints hints = new DalHints();
        SpTest daoPojo = new SpTest();
        for(int i=0;i<10000;i++) {
            daoPojo.setID(i+1);
            daoPojo.setColumn1("update2");
            dao.update(hints, daoPojo);
        }

        long end = System.currentTimeMillis();
        System.out.println(String.format("Test2 finished at %d, costs %d ms", end, end - start));

        daoPojo = dao.queryByPk(400, null);
        assertEquals("update2",daoPojo.getColumn1());

    }

    @Test
    public void testUpdate3() throws Exception {
        /*performance test update*/
        System.out.println("start test3...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test3 started at %d", start));

        DalHints hints = new DalHints();
        SpTest daoPojo = new SpTest();
        for(int i=0;i<10000;i++) {
            daoPojo.setID(i+1);
            daoPojo.setColumn1("update3");
            dao.update(hints, daoPojo);
        }

        long end = System.currentTimeMillis();
        System.out.println(String.format("Test3 finished at %d, costs %d ms", end, end - start));

        daoPojo = dao.queryByPk(400, null);
        assertEquals("update3",daoPojo.getColumn1());

    }

    @Test
    public void testUpdate4() throws Exception {
        /*performance test update*/
        System.out.println("start test4...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test4 started at %d", start));

        DalHints hints = new DalHints();
        SpTest daoPojo = new SpTest();
        for(int i=0;i<10000;i++) {
            daoPojo.setID(i+1);
            daoPojo.setColumn1("update4");
            dao.update(hints, daoPojo);
        }

        long end = System.currentTimeMillis();
        System.out.println(String.format("Test4 finished at %d, costs %d ms", end, end - start));

        daoPojo = dao.queryByPk(400, null);
        assertEquals("update4",daoPojo.getColumn1());

    }

    @Test
    public void testUpdate5() throws Exception {
        /*performance test update*/
        System.out.println("start test5...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test5 started at %d", start));

        DalHints hints = new DalHints();
        SpTest daoPojo = new SpTest();
        for(int i=0;i<10000;i++) {
            daoPojo.setID(i+1);
            daoPojo.setColumn1("update5");
            dao.update(hints, daoPojo);
        }

        long end = System.currentTimeMillis();
        System.out.println(String.format("Test5 finished at %d, costs %d ms", end, end - start));

        daoPojo = dao.queryByPk(400, null);
        assertEquals("update5",daoPojo.getColumn1());

    }

    @Test
    public void testUpdate6() throws Exception {
        /*performance test update*/
        System.out.println("start test6...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test6 started at %d", start));

        DalHints hints = new DalHints();
        SpTest daoPojo = new SpTest();
        for(int i=0;i<10000;i++) {
            daoPojo.setID(i+1);
            daoPojo.setColumn1("update6");
            dao.update(hints, daoPojo);
        }

        long end = System.currentTimeMillis();
        System.out.println(String.format("Test6 finished at %d, costs %d ms", end, end - start));

        daoPojo = dao.queryByPk(400, null);
        assertEquals("update6",daoPojo.getColumn1());

    }

    @Test
    public void testUpdate7() throws Exception {
        /*performance test update*/
        System.out.println("start test7...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test7 started at %d", start));

        DalHints hints = new DalHints();
        SpTest daoPojo = new SpTest();
        for(int i=0;i<10000;i++) {
            daoPojo.setID(i+1);
            daoPojo.setColumn1("update7");
            dao.update(hints, daoPojo);
        }

        long end = System.currentTimeMillis();
        System.out.println(String.format("Test7 finished at %d, costs %d ms", end, end - start));

        daoPojo = dao.queryByPk(400, null);
        assertEquals("update7",daoPojo.getColumn1());

    }

    @Test
    public void testUpdate8() throws Exception {
        /*performance test update*/
        System.out.println("start test8...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test8 started at %d", start));

        DalHints hints = new DalHints();
        SpTest daoPojo = new SpTest();
        for(int i=0;i<10000;i++) {
            daoPojo.setID(i+1);
            daoPojo.setColumn1("update8");
            dao.update(hints, daoPojo);
        }

        long end = System.currentTimeMillis();
        System.out.println(String.format("Test8 finished at %d, costs %d ms", end, end - start));

        daoPojo = dao.queryByPk(400, null);
        assertEquals("update8",daoPojo.getColumn1());

    }

    @Test
    public void testUpdate9() throws Exception {
        /*performance test update*/
        System.out.println("start test9...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test9 started at %d", start));

        DalHints hints = new DalHints();
        SpTest daoPojo = new SpTest();
        for(int i=0;i<10000;i++) {
            daoPojo.setID(i+1);
            daoPojo.setColumn1("update9");
            dao.update(hints, daoPojo);
        }

        long end = System.currentTimeMillis();
        System.out.println(String.format("Test9 finished at %d, costs %d ms", end, end - start));

        daoPojo = dao.queryByPk(400, null);
        assertEquals("update9",daoPojo.getColumn1());

    }

    @Test
    public void testUpdate10() throws Exception {
        /*performance test update*/
        System.out.println("start test10...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test10 started at %d", start));

        DalHints hints = new DalHints();
        SpTest daoPojo = new SpTest();
        for(int i=0;i<10000;i++) {

            daoPojo.setID(i+1);
            daoPojo.setColumn1("update10");
            dao.update(hints, daoPojo);
        }

        long end = System.currentTimeMillis();
        System.out.println(String.format("Test10 finished at %d, costs %d ms", end, end - start));


        assertEquals("update10",dao.queryByPk(400, null).getColumn1());

    }

        @Test
        public void testUpdateList1() throws Exception {
		/*performance test update list*/
		DalHints hints = new DalHints();
		List<SpTest> daoPojos = new ArrayList<>();

		for(int i=0;i<10000;i++){
 		SpTest pojo=new SpTest();
		pojo.setID(i+1);
		pojo.setColumn1("updateList1");
		daoPojos.add(pojo);
		}
		System.out.println("start test1...");
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test1 started at %d", start));
		dao.update(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test1 finished at %d, costs %d ms", end, end - start));
		assertEquals("updateList1",dao.queryByPk(200).getColumn1());
		assertEquals("updateList1",dao.queryByPk(600).getColumn1());
		assertEquals("updateList1",dao.queryByPk(1000).getColumn1());

        }

        @Test
        public void testUpdateList2() throws Exception {

		/*performance test update list*/
		DalHints hints = new DalHints();
		List<SpTest> daoPojos = new ArrayList<>();

		for(int i=0;i<10000;i++){
    		SpTest pojo=new SpTest();
		pojo.setID(i+1);
		pojo.setColumn1("updateList2");
		daoPojos.add(pojo);
		}
		System.out.println("start test2...");
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test2 started at %d", start));
		dao.update(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test2 finished at %d, costs %d ms", end, end - start));
		assertEquals("updateList2",dao.queryByPk(200).getColumn1());
		assertEquals("updateList2",dao.queryByPk(600).getColumn1());
		assertEquals("updateList2",dao.queryByPk(1000).getColumn1());

        }

        @Test
        public void testUpdateList3() throws Exception {

		/*performance test update list*/
		DalHints hints = new DalHints();
		List<SpTest> daoPojos = new ArrayList<>();

		for(int i=0;i<10000;i++){
    		SpTest pojo=new SpTest();
		pojo.setID(i+1);
		pojo.setColumn1("updateList3");
		daoPojos.add(pojo);
		}
		System.out.println("start test3...");
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test3 started at %d", start));
		dao.update(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test3 finished at %d, costs %d ms", end, end - start));
		assertEquals("updateList3",dao.queryByPk(200).getColumn1());
		assertEquals("updateList3",dao.queryByPk(600).getColumn1());
		assertEquals("updateList3",dao.queryByPk(1000).getColumn1());

        }

        @Test
        public void testUpdateList4() throws Exception {

		/*performance test update list*/
		DalHints hints = new DalHints();
		List<SpTest> daoPojos = new ArrayList<>();

		for(int i=0;i<10000;i++){
    		SpTest pojo=new SpTest();
		pojo.setID(i+1);
		pojo.setColumn1("updateList4");
		daoPojos.add(pojo);
		}
		System.out.println("start test4...");
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test4 started at %d", start));
		dao.update(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test4 finished at %d, costs %d ms", end, end - start));
		assertEquals("updateList4",dao.queryByPk(200).getColumn1());
		assertEquals("updateList4",dao.queryByPk(600).getColumn1());
		assertEquals("updateList4",dao.queryByPk(1000).getColumn1());

        }

        @Test
        public void testUpdateList5() throws Exception {

		/*performance test update list*/
		DalHints hints = new DalHints();
		List<SpTest> daoPojos = new ArrayList<>();

		for(int i=0;i<10000;i++){
    		SpTest pojo=new SpTest();
		pojo.setID(i+1);
		pojo.setColumn1("updateList5");
		daoPojos.add(pojo);
		}
		System.out.println("start test5...");
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test5 started at %d", start));
		dao.update(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test5 finished at %d, costs %d ms", end, end - start));
		assertEquals("updateList5",dao.queryByPk(200).getColumn1());
		assertEquals("updateList5",dao.queryByPk(600).getColumn1());
		assertEquals("updateList5",dao.queryByPk(1000).getColumn1());

        }

        @Test
        public void testUpdateList6() throws Exception {

		/*performance test update list*/
		DalHints hints = new DalHints();
		List<SpTest> daoPojos = new ArrayList<>();

		for(int i=0;i<10000;i++){
    		SpTest pojo=new SpTest();
		pojo.setID(i+1);
		pojo.setColumn1("updateList6");
		daoPojos.add(pojo);
		}
		System.out.println("start test6...");
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test6 started at %d", start));
		dao.update(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test6 finished at %d, costs %d ms", end, end - start));
		assertEquals("updateList6",dao.queryByPk(200).getColumn1());
		assertEquals("updateList6",dao.queryByPk(600).getColumn1());
		assertEquals("updateList6",dao.queryByPk(1000).getColumn1());

        }

        @Test
        public void testUpdateList7() throws Exception {

		/*performance test update list*/
		DalHints hints = new DalHints();
		List<SpTest> daoPojos = new ArrayList<>();

		for(int i=0;i<10000;i++){
    		SpTest pojo=new SpTest();
		pojo.setID(i+1);
		pojo.setColumn1("updateList7");
		daoPojos.add(pojo);
		}
		System.out.println("start test7...");
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test7 started at %d", start));
		dao.update(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test7 finished at %d, costs %d ms", end, end - start));
		assertEquals("updateList7",dao.queryByPk(200).getColumn1());
		assertEquals("updateList7",dao.queryByPk(600).getColumn1());
		assertEquals("updateList7",dao.queryByPk(1000).getColumn1());

        }

        @Test
        public void testUpdateList8() throws Exception {

		/*performance test update list*/
		DalHints hints = new DalHints();
		List<SpTest> daoPojos = new ArrayList<>();

		for(int i=0;i<10000;i++){
    		SpTest pojo=new SpTest();
		pojo.setID(i+1);
		pojo.setColumn1("updateList8");
		daoPojos.add(pojo);
		}
		System.out.println("start test8...");
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test8 started at %d", start));
		dao.update(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test8 finished at %d, costs %d ms", end, end - start));
		assertEquals("updateList8",dao.queryByPk(200).getColumn1());
		assertEquals("updateList8",dao.queryByPk(600).getColumn1());
		assertEquals("updateList8",dao.queryByPk(1000).getColumn1());

        }

        @Test
        public void testUpdateList9() throws Exception {

		/*performance test update list*/
		DalHints hints = new DalHints();
		List<SpTest> daoPojos = new ArrayList<>();

		for(int i=0;i<10000;i++){
    		SpTest pojo=new SpTest();
		pojo.setID(i+1);
		pojo.setColumn1("updateList9");
		daoPojos.add(pojo);
		}
		System.out.println("start test9...");
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test9 started at %d", start));
		dao.update(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test9 finished at %d, costs %d ms", end, end - start));
		assertEquals("updateList9",dao.queryByPk(200).getColumn1());
		assertEquals("updateList9",dao.queryByPk(600).getColumn1());
		assertEquals("updateList9",dao.queryByPk(1000).getColumn1());

        }

        @Test
        public void testUpdateList10() throws Exception {

		/*performance test update list*/
		DalHints hints = new DalHints();
		List<SpTest> daoPojos = new ArrayList<>();

		for(int i=0;i<10000;i++){
    		SpTest pojo=new SpTest();
		pojo.setID(i+1);
		pojo.setColumn1("updateList10");
		daoPojos.add(pojo);
		}
		System.out.println("start test10...");
		long start = System.currentTimeMillis();
		System.out.println(String.format("Test10 started at %d", start));
		dao.update(hints, daoPojos);
		long end = System.currentTimeMillis();
		System.out.println(String.format("Test10 finished at %d, costs %d ms", end, end - start));
		assertEquals("updateList10",dao.queryByPk(200).getColumn1());
		assertEquals("updateList10",dao.queryByPk(600).getColumn1());
		assertEquals("updateList10",dao.queryByPk(1000).getColumn1());
        }

    @Test
    public void testBatchUpdate1() throws Exception {

		/*performance test batchUpdate*/
        DalHints hints = new DalHints();
        List<SpTest> daoPojos = new ArrayList<>();
        for(int i=0;i<10000;i++){
            SpTest pojo=new SpTest();
            pojo.setID(i+1);
            pojo.setColumn1("batchUpdate1");
            daoPojos.add(pojo);
        }
        System.out.println("start test1...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test1 started at %d", start));
        dao.batchUpdate(hints, daoPojos);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Test1 finished at %d, costs %d ms", end, end - start));
        assertEquals("batchUpdate1",dao.queryByPk(200).getColumn1());
        assertEquals("batchUpdate1",dao.queryByPk(600).getColumn1());
        assertEquals("batchUpdate1",dao.queryByPk(10000).getColumn1());
    }

    @Test
    public void testBatchUpdate2() throws Exception {

		/*performance test batchUpdate*/
        DalHints hints = new DalHints();
        List<SpTest> daoPojos = new ArrayList<>();

        for(int i=0;i<10000;i++){
            SpTest pojo=new SpTest();
            pojo.setID(i+1);
            pojo.setColumn1("batchUpdate2");
            daoPojos.add(pojo);
        }
        System.out.println("start test2...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test2 started at %d", start));
        dao.batchUpdate(hints, daoPojos);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Test2 finished at %d, costs %d ms", end, end - start));
        assertEquals("batchUpdate2",dao.queryByPk(200).getColumn1());
        assertEquals("batchUpdate2",dao.queryByPk(600).getColumn1());
        assertEquals("batchUpdate2",dao.queryByPk(10000).getColumn1());
    }

    @Test
    public void testBatchUpdate3() throws Exception {

		/*performance test batchUpdate*/
        DalHints hints = new DalHints();
        List<SpTest> daoPojos = new ArrayList<>();

        for(int i=0;i<10000;i++){
            SpTest pojo=new SpTest();
            pojo.setID(i+1);
            pojo.setColumn1("batchUpdate3");
            daoPojos.add(pojo);
        }
        System.out.println("start test3...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test3 started at %d", start));
        dao.batchUpdate(hints, daoPojos);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Test3 finished at %d, costs %d ms", end, end - start));
        assertEquals("batchUpdate3",dao.queryByPk(200).getColumn1());
        assertEquals("batchUpdate3",dao.queryByPk(600).getColumn1());
        assertEquals("batchUpdate3",dao.queryByPk(10000).getColumn1());
    }

    @Test
    public void testBatchUpdate4() throws Exception {

		/*performance test batchUpdate*/
        DalHints hints = new DalHints();
        List<SpTest> daoPojos = new ArrayList<>();

        for(int i=0;i<10000;i++){
            SpTest pojo=new SpTest();
            pojo.setID(i+1);
            pojo.setColumn1("batchUpdate4");
            daoPojos.add(pojo);
        }
        System.out.println("start test4...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test4 started at %d", start));
        dao.batchUpdate(hints, daoPojos);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Test4 finished at %d, costs %d ms", end, end - start));
        assertEquals("batchUpdate4",dao.queryByPk(200).getColumn1());
        assertEquals("batchUpdate4",dao.queryByPk(600).getColumn1());
        assertEquals("batchUpdate4",dao.queryByPk(10000).getColumn1());
    }

    @Test
    public void testBatchUpdate5() throws Exception {

		/*performance test batchUpdate*/
        DalHints hints = new DalHints();
        List<SpTest> daoPojos = new ArrayList<>();

        for(int i=0;i<10000;i++){
            SpTest pojo=new SpTest();
            pojo.setID(i+1);
            pojo.setColumn1("batchUpdate5");
            daoPojos.add(pojo);
        }
        System.out.println("start test5...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test5 started at %d", start));
        dao.batchUpdate(hints, daoPojos);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Test5 finished at %d, costs %d ms", end, end - start));
        assertEquals("batchUpdate5",dao.queryByPk(200).getColumn1());
        assertEquals("batchUpdate5",dao.queryByPk(600).getColumn1());
        assertEquals("batchUpdate5",dao.queryByPk(10000).getColumn1());
    }

    @Test
    public void tesBatchUpdate6() throws Exception {

		/*performance test batchUpdate*/
        DalHints hints = new DalHints();
        List<SpTest> daoPojos = new ArrayList<>();

        for(int i=0;i<10000;i++){
            SpTest pojo=new SpTest();
            pojo.setID(i+1);
            pojo.setColumn1("batchUpdate6");
            daoPojos.add(pojo);
        }
        System.out.println("start test6...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test6 started at %d", start));
        dao.batchUpdate(hints, daoPojos);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Test6 finished at %d, costs %d ms", end, end - start));
        assertEquals("batchUpdate6",dao.queryByPk(200).getColumn1());
        assertEquals("batchUpdate6",dao.queryByPk(600).getColumn1());
        assertEquals("batchUpdate6",dao.queryByPk(10000).getColumn1());
    }

    @Test
    public void testBatchUpdate7() throws Exception {

		/*performance test batchUpdate*/
        DalHints hints = new DalHints();
        List<SpTest> daoPojos = new ArrayList<>();

        for(int i=0;i<10000;i++){
            SpTest pojo=new SpTest();
            pojo.setID(i+1);
            pojo.setColumn1("batchUpdate7");
            daoPojos.add(pojo);
        }
        System.out.println("start test7...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test7 started at %d", start));
        dao.batchUpdate(hints, daoPojos);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Test7 finished at %d, costs %d ms", end, end - start));
        assertEquals("batchUpdate7",dao.queryByPk(200).getColumn1());
        assertEquals("batchUpdate7",dao.queryByPk(600).getColumn1());
        assertEquals("batchUpdate7",dao.queryByPk(10000).getColumn1());
    }

    @Test
    public void testBatchUpdate8() throws Exception {

		/*performance test batchUpdate*/
        DalHints hints = new DalHints();
        List<SpTest> daoPojos = new ArrayList<>();

        for(int i=0;i<10000;i++){
            SpTest pojo=new SpTest();
            pojo.setID(i+1);
            pojo.setColumn1("batchUpdate8");
            daoPojos.add(pojo);
        }
        System.out.println("start test8...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test8 started at %d", start));
        dao.batchUpdate(hints, daoPojos);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Test8 finished at %d, costs %d ms", end, end - start));
        assertEquals("batchUpdate8",dao.queryByPk(200).getColumn1());
        assertEquals("batchUpdate8",dao.queryByPk(600).getColumn1());
        assertEquals("batchUpdate8",dao.queryByPk(10000).getColumn1());
    }

    @Test
    public void testBatchUpdate9() throws Exception {

		/*performance test batchUpdate*/
        DalHints hints = new DalHints();
        List<SpTest> daoPojos = new ArrayList<>();

        for(int i=0;i<10000;i++){
            SpTest pojo=new SpTest();
            pojo.setID(i+1);
            pojo.setColumn1("batchUpdate9");
            daoPojos.add(pojo);
        }
        System.out.println("start test9...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test9 started at %d", start));
        dao.batchUpdate(hints, daoPojos);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Test9 finished at %d, costs %d ms", end, end - start));
        assertEquals("batchUpdate9",dao.queryByPk(200).getColumn1());
        assertEquals("batchUpdate9",dao.queryByPk(600).getColumn1());
        assertEquals("batchUpdate9",dao.queryByPk(10000).getColumn1());
    }

    @Test
    public void testBatchUpdate10() throws Exception {

		/*performance test batchUpdate*/
        DalHints hints = new DalHints();
        List<SpTest> daoPojos = new ArrayList<>();

        for(int i=0;i<10000;i++){
            SpTest pojo=new SpTest();
            pojo.setID(i+1);
            pojo.setColumn1("batchUpdate10");
            daoPojos.add(pojo);
        }
        System.out.println("start test10...");
        long start = System.currentTimeMillis();
        System.out.println(String.format("Test10 started at %d", start));
        dao.batchUpdate(hints, daoPojos);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Test10 finished at %d, costs %d ms", end, end - start));
        assertEquals("batchUpdate10",dao.queryByPk(200).getColumn1());
        assertEquals("batchUpdate10",dao.queryByPk(600).getColumn1());
        assertEquals("batchUpdate10",dao.queryByPk(10000).getColumn1());
    }

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
