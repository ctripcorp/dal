import com.ctrip.platform.dal.dao.DalClientFactory;
//import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;
//import com.ctrip.platform.dal.dao.datasource.SingleDataSource;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lilj on 2017/7/6.
 */
public class DRTestOnSqlServer {
//    private static final String DATA_BASE = "noShardTestOnMysql";

//    private static DalClient client = null;
    private static DRTestOnSqlServerDao dao = null;
    private static Logger log= LoggerFactory.getLogger(DRTestOnSqlServer.class);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        /**
         * Initialize DalClientFactory.
         * The Dal.config can be specified from class-path or local file path.
         * One of follow three need to be enabled.
         **/
//        DalClientFactory.initClientFactory(); // load from class-path Dal.config
//        DalClientFactory.warmUpConnections();
//        client = DalClientFactory.getClient(DATA_BASE);
        dao = new DRTestOnSqlServerDao();

//        dao.dropTable();
//        dao.createTable();

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
//        dao.test_def_update(new DalHints());
//       DRTestOnSqlServerPojo daoPojo = newDRTestOnSqlServerPojo();
//        daoPojo.setName(dao.selectHostname(null));
//        dao.insert(new DalHints(), daoPojo);

    }

    @After
    public void tearDown() throws Exception {
//		dao.test_def_update(new DalHints());
//        Thread.sleep(5000);
    }


    @Test
    public void testDynamicDatasourcePoolConfigSqlServer() throws Exception {
        log.info(String.format("current sqlserver servername : %s",dao.selectHostnameSqlserver(null)));
    }




//    @Test
//    public void testDynamicDatasourcePoolConfig() throws Exception {
//
////        log.info( DataSourceConfigureParser.getInstance()
////                .getDataSourceConfigure("mysqldaltest01db_W")
////                .getProperty("connectionProperties"));
//        List<DRTestPojo> insertList=new ArrayList<>();
//        for(int i=0;i<3;i++){
//           DRTestOnSqlServerPojo insertPojo=newDRTestOnSqlServerPojo();
//            insertPojo.setName("pojo"+i);
//            insertList.add(insertPojo);
//        }
//        int i=1;
//        while(1==1) {
//            try {
//                log.info(String.format("Test %d started", i));
//                log.info("clear data ", dao.test_def_update(new DalHints()));
//                log.info("batchInsert data ");
//                int[] ret = dao.batchInsert(null, insertList);
//                log.info("------------------ print result : " + ret[0]+","+ret[1]+","+ret[2]);
////                for (int j = 0; j < ret.length; j++) {
////                    System.out.print(ret[j] + " ");
////                }
////                System.out.println();
//                log.info(String.format("Test %d passed",i));
//            }
//            catch (Exception e){
//                log.error(String.format("Test %d failed",i),e);
//            }
//            i++;
//            Thread.sleep(3000);
//        }
//    }

    @Test
    public void test() throws Exception{
        DalClientFactory.initClientFactory("target\\test-classes\\DalConfig\\Dal.config");
    }


    /*@Test
    public void testDynamicDatasourceWithSingleKey() throws Exception {
        int i=0;
//        long startTime=System.currentTimeMillis();
        while(1==1){
            try{
//                try (Connection conn= DalClientFactory.getDalConfigure().getLocator().getConnection("DalServiceDB")) {
//					System.out.println("Connection: " + conn);
//				}
                log.info(String.format("Test %d started",i));
                log.info(String.format("---------------current hostname is: %s ",dao.selectHostnameSqlserver(null)));
//                log.info(String.format("---------------current database is: %s ",dao.selectDatabase(null)));
                log.info("clear data ",dao.test_def_update(new DalHints()));
                DRTestOnSqlServerPojo daoPojo = new DRTestOnSqlServerPojo();
                daoPojo.setName(dao.selectHostnameSqlserver(null));
                log.info("insert data ",dao.insert(null, daoPojo));
                log.info("query data "+dao.queryByPk(1, null).getName());
//                log.info("10 seconds query...");
//                dao.testLongQuery(10,null);
                log.info(String.format("Test %d passed",i));
            }
            catch (Exception e){
                log.error(String.format("Test %d failed",i),e);
            }
            i++;
            Thread.sleep(1000);
//            long endTime=System.currentTimeMillis();
//            long costTime=endTime-startTime;
//            System.out.println(String.format("costTime: %d",costTime));
//            if(costTime>=25000&&costTime<30000){
//                System.out.println("25s...waiting for validation...");
//                Thread.sleep(5000);}
        }

    }*/
//
//    @Test
//    public void testDynamicDatasourceWithRWKeys() throws Exception {
//        int i=0;
////        long startTime=System.currentTimeMillis();
//        while(1==1){
//            try{
//                log.info(String.format("Test %d started",i));
//                log.info(String.format("---------------current hostname of master db is: %s ",dao.selectHostname(new DalHints().masterOnly())));
//                log.info(String.format("---------------current hostname of slave db is: %s ",dao.selectHostname(null)));
////                log.info(String.format("---------------current database is: %s ",dao.selectDatabase(null)));
//                log.info("clear data ",dao.test_def_update(new DalHints()));
//               DRTestOnSqlServerPojo daoPojo = newDRTestOnSqlServerPojo();
//                daoPojo.setName(dao.selectHostname(new DalHints().masterOnly()));
//                log.info("insert data ",dao.insert(null, daoPojo));
//                log.info("query data "+dao.queryByPk(1, new DalHints().masterOnly()).getName());
////                log.info("10 seconds query...");
////                dao.testLongQuery(10,null);
//                log.info(String.format("Test %d passed",i));
//            }
//            catch (Exception e){
//                log.error(String.format("Test %d failed",i),e);
//            }
//            i++;
//            Thread.sleep(1000);
////            long endTime=System.currentTimeMillis();
////            long costTime=endTime-startTime;
////            System.out.println(String.format("costTime: %d",costTime));
////            if(costTime>=25000&&costTime<30000){
////                System.out.println("25s...waiting for validation...");
////                Thread.sleep(5000);}
//        }
//    }
//
//    @Test
//    public void testDynamicDatasourceWithMultipleKeys() throws Exception {
//        int i = 0;
////        long startTime=System.currentTimeMillis();
//        while (1 == 1) {
//            try {
//                log.info(String.format("Test %d started", i));
//                log.info(String.format("---------------current hostname of mysqldaltest01db_W is: %s ", dao.selectHostname(new DalHints().inShard(0))));
//                log.info(String.format("---------------current database of mysqldaltest01db_W is: %s ",dao.selectDatabase(new DalHints().inShard(0))));
////                log.info(String.format("---------------current hostname of mysqldaltest02db_W is: %s ", dao.selectHostname(new DalHints().inShard(1))));
////                log.info(String.format("---------------current database of mysqldaltest02db_W is: %s ",dao.selectDatabase(new DalHints().inShard(1))));
//                log.info("clear data in all shards", dao.test_def_update(new DalHints().inShard(0)));
//               DRTestOnSqlServerPojo daoPojo = newDRTestOnSqlServerPojo();
////                daoPojo.setName(dao.selectHostname(null));
//                daoPojo.setName("testDynamicDatasourceWithMultipleKeys");
//                log.info("insert data to testtable in mysqldaltest01db", dao.insert(new DalHints().inShard(0), daoPojo));
//                log.info("query data from testtable in mysqldaltest01db : " + dao.queryByPk(1, new DalHints().inShard(0)).getName());
////                log.info("insert data to testtable in mysqldaltest02db", dao.insert(new DalHints().inShard(1), daoPojo));
////                log.info("query data from testtable in mysqldaltest02db : " + dao.queryByPk(1, new DalHints().inShard(1)).getName());
////                log.info("10 seconds query...");
////                dao.testLongQuery(10,null);
//                log.info(String.format("Test %d passed", i));
//            } catch (Exception e) {
//                log.error(String.format("Test %d failed", i), e);
//            }
//            i++;
//            Thread.sleep(1000);
////            long endTime=System.currentTimeMillis();
////            long costTime=endTime-startTime;
////            System.out.println(String.format("costTime: %d",costTime));
////            if(costTime>=25000&&costTime<30000){
////                System.out.println("25s...waiting for validation...");
////                Thread.sleep(5000);}
//        }
//    }
//
//    @Test
//    public void testDR() throws Exception {
//        int i=0;
//        long startTime=System.currentTimeMillis();
//        while(1==1){
//            try{
//                log.info(String.format("Test %d started",i));
//                log.info(String.format("current hostname: %s ",dao.selectHostname(null)));
//                log.info("clear data ",dao.test_def_update(new DalHints()));
//               DRTestOnSqlServerPojo daoPojo = newDRTestOnSqlServerPojo();
//                daoPojo.setName(dao.selectHostname(null));
//                log.info("insert data ",dao.insert(null, daoPojo));
//                log.info("query data "+dao.queryByPk(1, null).getName());
//                log.info(String.format("Test %d passed",i));
//            }
//            catch (Exception e){
//                log.error(String.format("Test %d failed",i),e);
//            }
//            i++;
//            Thread.sleep(1000);
//            long endTime=System.currentTimeMillis();
//            long costTime=endTime-startTime;
//            System.out.println(String.format("costTime: %d",costTime));
//            if(costTime>=25000&&costTime<30000){
//                System.out.println("25s...waiting for validation...");
//                Thread.sleep(5000);}
//        }
//
//    }
}
