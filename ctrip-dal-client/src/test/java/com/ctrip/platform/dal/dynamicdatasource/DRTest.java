package com.ctrip.platform.dal.dynamicdatasource;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lilj on 2017/7/6.
 */
public class DRTest {
    // private static final String DATA_BASE = "noShardTestOnMysql";

    // private static DalClient client = null;
    private static DRTestDao dao = null;
    private static Logger log = LoggerFactory.getLogger(DRTest.class);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        /**
         * Initialize DalClientFactory. The Dal.config can be specified from class-path or local file path. One of
         * follow three need to be enabled.
         **/
        DalClientFactory.initClientFactory(); // load from class-path Dal.config
        DalClientFactory.warmUpConnections();
        // client = DalClientFactory.getClient(DATA_BASE);
        dao = new DRTestDao();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        // dao.test_def_update(new DalHints());
        // DRTestPojo daoPojo = new DRTestPojo();
        // daoPojo.setName(dao.selectHostname(null));
        // dao.insert(new DalHints(), daoPojo);
    }

    @After
    public void tearDown() throws Exception {
        // dao.test_def_update(new DalHints());
        // Thread.sleep(5000);
    }

    @Test
    public void testDynamicDatasource() throws Exception {
        int i = 0;
        // long startTime=System.currentTimeMillis();
        while (1 == 1) {
            try {
                log.info(String.format("Test %d started", i));
                log.info(String.format("current database is: %s ", dao.selectDatabase(null)));
                // log.info("clear data ",dao.test_def_update(new DalHints()));
                // DRTestPojo daoPojo = new DRTestPojo();
                // daoPojo.setName(dao.selectHostname(null));
                // log.info("insert data ",dao.insert(null, daoPojo));
                // log.info("query data "+dao.queryByPk(1, null).getName());
                // log.info(String.format("Test %d passed",i));
                Thread.sleep(3 * 1000);
            } catch (Exception e) {
                log.error(String.format("Test %d failed", i), e);
            }
            i++;
            // Thread.sleep(1000);
            // long endTime=System.currentTimeMillis();
            // long costTime=endTime-startTime;
            // System.out.println(String.format("costTime: %d",costTime));
            // if(costTime>=25000&&costTime<30000){
            // System.out.println("25s...waiting for validation...");
            // Thread.sleep(5000);}
        }

    }

    @Test
    public void testDR() throws Exception {
        int i = 0;
        long startTime = System.currentTimeMillis();
        while (1 == 1) {
            try {
                log.info(String.format("Test %d started", i));
                log.info(String.format("current hostname: %s ", dao.selectHostname(null)));
                log.info("clear data ", dao.test_def_update(new DalHints()));
                DRTestPojo daoPojo = new DRTestPojo();
                daoPojo.setName(dao.selectHostname(null));
                log.info("insert data ", dao.insert(null, daoPojo));
                log.info("query data " + dao.queryByPk(1, null).getName());
                log.info(String.format("Test %d passed", i));
            } catch (Exception e) {
                log.error(String.format("Test %d failed", i), e);
            }
            i++;
            Thread.sleep(1000);
            long endTime = System.currentTimeMillis();
            long costTime = endTime - startTime;
            System.out.println(String.format("costTime: %d", costTime));
            if (costTime >= 25000 && costTime < 30000) {
                System.out.println("25s...waiting for validation...");
                Thread.sleep(5000);
            }
        }

    }

    @Test
    public void testQueryByPk2() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk3() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk4() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk5() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk6() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk7() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk8() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk9() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk10() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk11() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk12() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk13() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk14() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk15() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk16() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk17() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk18() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk19() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk20() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk21() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk22() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk23() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk24() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk25() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk26() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk27() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk28() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk29() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk30() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk31() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk32() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk33() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk34() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk35() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk36() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk37() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk38() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk39() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk40() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk41() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk42() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk43() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk44() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk45() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk46() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk47() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk48() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk49() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk50() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk51() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk52() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk53() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk54() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk55() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk56() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk57() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk58() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk59() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk60() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk61() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk62() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk63() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk64() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk65() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk66() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk67() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk68() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk69() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testQueryByPk70() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    @Test
    public void testhostname() throws Exception {
        String hostname = dao.selectHostname(null);
        System.out.print(hostname);
    }
}
