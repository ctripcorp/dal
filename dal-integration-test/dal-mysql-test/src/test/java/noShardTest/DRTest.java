package noShardTest;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by lilj on 2017/7/6.
 */
public class DRTest {
//    private static final String DATA_BASE = "noShardTestOnMysql";

//    private static DalClient client = null;
    private static DRTestDao dao = null;
    private static Logger log= LoggerFactory.getLogger(DRTest.class);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        /**
         * Initialize DalClientFactory.
         * The Dal.config can be specified from class-path or local file path.
         * One of follow three need to be enabled.
         **/
//        java.security.Security.setProperty("networkaddress.cache.ttl" , "1");
        DalClientFactory.initClientFactory(); // load from class-path Dal.config
        DalClientFactory.warmUpConnections();
//        client = DalClientFactory.getClient(DATA_BASE);
        dao = new DRTestDao();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
//        dao.test_def_update(new DalHints());
//
//        List<DRTestPojo> daoPojos = new ArrayList<>(
//                6);
//        for (int i = 0; i < 6; i++) {
//            DRTestPojo daoPojo = new DRTestPojo();
//
//            if (i % 2 == 0) {
//                daoPojo.setAge(20);
//                daoPojo.setName("Initial_Shard_0" + i);
//            } else {
//                daoPojo.setAge(21);
//                daoPojo.setName("Initial_Shard_1" + i);
//            }
//            daoPojos.add(daoPojo);
//        }
//        dao.insert(new DalHints(), daoPojos);
    }

    @After
    public void tearDown() throws Exception {
//		dao.test_def_update(new DalHints());
//        Thread.sleep(5000);
    }

    @Test
    public void testDynamicDatasource() throws Exception {

        int i=0;
//        long startTime=System.currentTimeMillis();
        while(1==1){
            try{

//                try (Connection conn= DalClientFactory.getDalConfigure().getLocator().getConnection("DalService2DB_W")) {
//                    System.out.println("Connection: " + conn);
//                }
                log.info(String.format("Test %d started",i));
                log.info(String.format("------------------current database is: %s ",dao.selectHostname(null)));
                log.info(String.format("------------------current database is: %s ",dao.selectDatabase(null)));
                log.info("clear data ",dao.test_def_update(new DalHints()));
                DRTestPojo daoPojo = new DRTestPojo();
                daoPojo.setName(dao.selectHostname(null));
                log.info("insert data ",dao.insert(null, daoPojo));
                log.info(String.format("10 seconds query..."));
                log.info("query data ",dao.testLongQuery(10,null));
                log.info(String.format("Test %d passed",i));
            }
            catch (Exception e){
                log.error(String.format("Test %d failed",i),e);
            }
            i++;
            log.info("sleep 1 seconds...");
            Thread.sleep(1000);
//            long endTime=System.currentTimeMillis();
//            long costTime=endTime-startTime;
//            System.out.println(String.format("costTime: %d",costTime));
//            if(costTime>=25000&&costTime<30000){
//                System.out.println("25s...waiting for validation...");
//                Thread.sleep(5000);}
        }

    }

    @Test
    public void testDR() throws Exception {
        int i=0;
        long startTime=System.currentTimeMillis();
        while(1==1){
            try{
                log.info(String.format("Test %d started",i));
                log.info(String.format("current hostname: %s ",dao.selectHostname(null)));
                log.info("clear data ",dao.test_def_update(new DalHints()));
                DRTestPojo daoPojo = new DRTestPojo();
                daoPojo.setName(dao.selectHostname(null));
                log.info("insert data ",dao.insert(null, daoPojo));
                log.info("query data "+dao.queryByPk(1, null).getName());
                log.info(String.format("Test %d passed",i));
            }
            catch (Exception e){
                log.error(String.format("Test %d failed",i),e);
            }
            i++;
            Thread.sleep(1000);
            long endTime=System.currentTimeMillis();
            long costTime=endTime-startTime;
            System.out.println(String.format("costTime: %d",costTime));
            if(costTime>=25000&&costTime<30000){
                System.out.println("25s...waiting for validation...");
                Thread.sleep(5000);}
        }

    }

    @Test
    public void testQueryByPk1() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
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

    public void testQueryByPk25() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    public void testQueryByPk26() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    public void testQueryByPk27() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    public void testQueryByPk28() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    public void testQueryByPk29() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    public void testQueryByPk30() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    public void testQueryByPk31() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    public void testQueryByPk32() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    public void testQueryByPk33() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    public void testQueryByPk34() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    public void testQueryByPk35() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    public void testQueryByPk36() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    public void testQueryByPk37() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    public void testQueryByPk38() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    public void testQueryByPk39() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }

    public void testQueryByPk40() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        DRTestPojo affected = dao.queryByPk(id, hints);
        System.out.println(affected.getName());
    }
}
