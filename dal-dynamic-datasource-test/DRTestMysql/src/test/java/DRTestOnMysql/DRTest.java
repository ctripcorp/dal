package DRTestOnMysql;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.datasource.DataSourceTerminator;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.validation.constraints.AssertTrue;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by lilj on 2017/7/6.
 */
public class DRTest {
//    private static final String DATA_BASE = "noShardTestOnMysql";

    //    private static DalClient client = null;
    private static DRTestDao dao = null;
    //    private static DRTestPojo pojo=null;
    private static Logger log = LoggerFactory.getLogger(DRTest.class);


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
        dao = new DRTestDao();
//         pojo = new DRTestPojo();
//        pojo.setName("test");
//        dao.dropTable();
//        dao.createTable();

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
//        dao.test_def_update(new DalHints());
//        DRTestPojo daoPojo = new DRTestPojo();
//        daoPojo.setName(dao.selectHostname(null));
//        dao.insert(new DalHints(), daoPojo);

    }

    @After
    public void tearDown() throws Exception {
//		dao.test_def_update(new DalHints());
//        Thread.sleep(5000);
    }

    @Test
    public void test() throws Exception{
        Thread.sleep(30000);
        log.info("Done");
    }

    @Test
    public void testCatLog() throws Exception {
        Connection connection = new DalDataSourceFactory().createDataSource("mysqldaltest01db_w").getConnection();


        try {
            log.info("query start");
/*// dal
            dao.testLongQuery(30, null);*/
// datasource
            connection.createStatement().execute("select name from testTable where sleep(90) = 0 limit 1");


//singleDatasource
            log.info("query done");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("query interrupt");
        } finally {
            /*log.info("close connection");
            connection.close();*/
        }

        /*try{
            log.info("use old connection to query");
            connection.createStatement().execute("select @@hostname");
            log.info("old connection work");
        }
        catch (Exception e){
            e.printStackTrace();
            log.info("old connection doesn't work");
        }finally {
            *//*log.info("close connection");
            connection.close();*//*
        }*/
        Thread.sleep(100000);
    }
    /*@Test
    public void testDataSource() throws Exception {
        DalDataSourceFactory dalDataSourceFactory=new DalDataSourceFactory();
//            DataSource ds= dalDataSourceFactory.createDataSource("DalService2DB_W","https://ws.titan2.fws.qa.nt.ctripcorp.com/titanservice/query");
//            Assert.assertNotNull(ds);
        DataSource ds2= dalDataSourceFactory.createDataSource("mysqldaltest01db_w","https://ws.titan2.fws.qa.nt.ctripcorp.com/titanservice/query");
        Assert.assertNotNull(ds2);

    }*/

    /*@Test
    public void testDynamicDatasourcePoolConfigSqlServer() throws Exception {
        List<DRTestPojo> insertList=new ArrayList<>();
        for(int i=0;i<3;i++){
            DRTestPojo insertPojo=new DRTestPojo();
            insertPojo.setName("pojo"+i);
            insertList.add(insertPojo);
        }
        int i=1;
        while(1==1) {
            try {
                log.info(String.format("Test %d started", i));
                log.info("clear data ", dao.test_def_update(new DalHints()));
                log.info("batchInsert data ");
                int[] ret = dao.batchInsert(null, insertList);
                log.info("------------------ print result : " + ret[0]+","+ret[1]+","+ret[2]);
                log.info(String.format("current sqlserver servername : %s",dao.selectHostnameSqlserver(null)));
//                for (int j = 0; j < ret.length; j++) {
//                    System.out.print(ret[j] + " ");
//                }
//                System.out.println();
                log.info(String.format("Test %d passed",i));
            }
            catch (Exception e){
                log.error(String.format("Test %d failed",i),e);
            }
            i++;
            Thread.sleep(1000);
        }

    }*/


   /* @Test
    public void test() throws Exception {
//        final DRTestDao dao = new DRTestDao();
//        dao.test_def_update(null);

        for (int i = 0; i < 200; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int j = 0;
                    while (j < 100) {
                        try {
                            dao.insert(null, pojo);
                            System.out.println(j);
                        } catch (Exception e) {
                            e.printStackTrace();
                            increment();
                        }
                        j++;
                    }
                }
            }).start();
        }
        System.out.println("exceptionNum: " + getCount());
//        System.exit(-1);
//    }
    }*/

    @Test
    public void testDSCloseTrue() throws Exception {
        /*DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureLocator.getInstance();
        DataSourceConfigure dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure("mysqldaltest01db_W");
        final SingleDataSource singleDataSource = new SingleDataSource("mysqldaltest01db_W", dataSourceConfigure);
        final DataSource dataSource = singleDataSource.getDataSource();
        final Connection connection = dataSource.getConnection();*/

        final CountDownLatch latch = new CountDownLatch(1);
        Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
        Properties properties = new Properties();
        properties.put("user", "tt_daltest1_2");
        properties.put("password", "k4AvZUIdDAcbyUvLirWG");

        final Connection connection = DriverManager.getConnection("jdbc:mysql://10.2.74.122:55111/mysqldaltest01db?useUnicode=true&characterEncoding=UTF-8", properties);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("90s query start.");
                    connection.createStatement().execute("select name from testTable where sleep(90) = 0 limit 1");
                    log.info("90s query done.");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        }).start();

        Thread.sleep(5000);

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("close start");
                    org.apache.tomcat.jdbc.pool.DataSource ds = (org.apache.tomcat.jdbc.pool.DataSource) dataSource;
                    ds.close(true);
//                   connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        }).start();*/


        latch.await();
        log.info("done");
    }


    @Test
    public void testLongQueryByDALAndLeakConnectionClosedByClient() throws Exception {

        DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureLocator.getInstance();
        DataSourceConfigure dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure("mysqldaltest01db_W");
        final SingleDataSource singleDataSource = new SingleDataSource("mysqldaltest01db_W", dataSourceConfigure);
//        final AtomicBoolean query_30s_success = new AtomicBoolean();
//        final AtomicBoolean query_90s_success = new AtomicBoolean();
       /* Connection connection = singleDataSource.getDataSource().getConnection();
        try {
            log.info("30s query start...");

            connection.createStatement().execute("select name from testTable where sleep(30) = 0 limit 1");

            log.info("30s query done.");
//                    query_30s_success.set(true);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("30s query interrupted.");

//                    query_30s_success.set(false);
//                    Assert.assertNotEquals(-1, e.getCause().toString().indexOf("EOFException"));
        } finally {
            log.info("close connection");
            connection.close();
            log.info("connection closed");
        }*/


        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection=null;
                try {
                    log.info("30s query start...");
                    connection = singleDataSource.getDataSource().getConnection();
                    connection.createStatement().execute("select name from testTable where sleep(30) = 0 limit 1");
//                    connection.close();
                    log.info("30s query done.");
//                    query_30s_success.set(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.warn("30s query interrupted.");
//                    query_30s_success.set(false);
//                    Assert.assertNotEquals(-1, e.getCause().toString().indexOf("EOFException"));
                }finally {
                    /*try {
                        log.info("close connection");
                        connection.close();
                        log.info("connection closed");
                    }catch (Exception e){
                        e.printStackTrace();
                    }*/
                }
            }
        }).start();

        /*new Thread(new Runnable() {
            @Override
            public void run() {

                Connection connection=null;

                try {
                    log.info("90s query start...");
                    connection = singleDataSource.getDataSource().getConnection();
                    connection.createStatement().execute("select name from testTable where sleep(90) = 0 limit 1");
                    connection.close();
                    log.info("90s query done");

                   *//* log.info("query again");
                    connection.createStatement().execute("select @@hostname");*//*
//                    query_90s_success.set(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("90s query interrupted");
//                    query_90s_success.set(false);
                }finally {
                    log.info("close connection");
                    try {
                        connection.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/

        log.info("close datasource after 10s");
        Thread.sleep(10000);

        log.info("close datasource start...");
        DataSourceTerminator.getInstance().close(singleDataSource);

        log.info("test case will end after 110s");
        Thread.sleep(110000);

//        log.info(dao.selectHostname(null));
//        assertTrue("30s query test failed", query_30s_success.get());
//        assertTrue("90s query test failed", query_90s_success.get());
        log.info("done");
    }

    @Test
    public void testDynamicDatasourcePoolConfigWithMultipleKeys() throws Exception {

//        log.info( DataSourceConfigureParser.getInstance()
//                .getDataSourceConfigure("mysqldaltest01db_W")
//                .getProperty("connectionProperties"));
        DRTestDao dao1 = new DRTestDao("shardTestOnMysql");
        List<DRTestPojo> insertList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            DRTestPojo insertPojo = new DRTestPojo();
            insertPojo.setName("pojo" + i);
            insertList.add(insertPojo);
        }
        int i = 1;
        while (1 == 1) {
            try {
                log.info(String.format("Test %d started", i));
                log.info("currentHostname in shard0:" + dao.selectHostname(new DalHints().inShard(0)));
                log.info("currentHostname in shard1:" + dao.selectHostname(new DalHints().inShard(1)));
                log.info("clear data in all shards", dao1.test_def_update(new DalHints().inAllShards()));
                log.info("batchInsert data to shard 0");
                int[] ret = dao1.batchInsert(new DalHints().inShard(0), insertList);
                log.info("------------------ print result of shard 0: " + ret[0] + "," + ret[1] + "," + ret[2]);
                log.info("batchInsert data to shard 1");
                ret = dao1.batchInsert(new DalHints().inShard(1), insertList);
                log.info("------------------ print result of shard 1: " + ret[0] + "," + ret[1] + "," + ret[2]);
//                for (int j = 0; j < ret.length; j++) {
//                    System.out.print(ret[j] + " ");
//                }
//                System.out.println();
                log.info(String.format("Test %d passed", i));
            } catch (Exception e) {
                log.error(String.format("Test %d failed", i), e);
            }
            i++;
            Thread.sleep(1000);
        }
    }

    @Test
    public void testDynamicDatasourcePoolConfigWithSingleKey() throws Exception {

//        log.info( DataSourceConfigureParser.getInstance()
//                .getDataSourceConfigure("mysqldaltest01db_W")
//                .getProperty("connectionProperties"));
        List<DRTestPojo> insertList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            DRTestPojo insertPojo = new DRTestPojo();
            insertPojo.setName("pojo" + i);
            insertList.add(insertPojo);
        }
        int i = 1;
        while (1 == 1) {
            try {
                log.info(String.format("Test %d started", i));
                log.info("clear data ", dao.test_def_update(new DalHints().inShard(0)));
                log.info("batchInsert data ");
                int[] ret = dao.batchInsert(null, insertList);
                log.info("------------------ print result : " + ret[0] + "," + ret[1] + "," + ret[2]);
//                for (int j = 0; j < ret.length; j++) {
//                    System.out.print(ret[j] + " ");
//                }
//                System.out.println();
                log.info(String.format("Test %d passed", i));
            } catch (Exception e) {
                log.error(String.format("Test %d failed", i), e);
            }
            i++;
            Thread.sleep(1000);
        }
    }

    @Test
    public void testConnectionPool() throws Exception {
        try {
            log.info("The first query...");
            dao.selectHostname(null);
            log.info("sleep 30 second...");
            Thread.sleep(30000);
            log.info("The second query...");
            dao.selectHostname(null);
            log.info("Test Passed");
        } catch (Exception e) {
            log.error("Test Failed");
            e.printStackTrace();
        }
    }

    @Test
    public void testLogAbandoned() throws Exception {
        try {
            dao.test_def_update(new DalHints());
            DRTestPojo pojo = new DRTestPojo();
            pojo.setName("testLogAbandoned");
            dao.insert(null, pojo);
            log.info("30 seconds query....");
            String name = dao.testLongQuery(30, null);
            log.info("query result: " + name);

        } catch (Exception e) {
            log.error("timed out");
            e.printStackTrace();
            log.info("15 seconds to change the config...");
            Thread.sleep(15000);
            log.info("30 seconds query again...");
            String name = dao.testLongQuery(30, null);
            log.info("query result: " + name);
        }
    }


   /* @Test
    public void autoTestDynamicDatasourceWithSingleKey() throws Exception {
        log.info(String.format("before switch"));
        String hostName1=dao.selectHostname(null);
        log.info(String.format("---------------current hostname is: %s ",hostName1));
        log.info(String.format("start switch"));

        try{
            HttpResponse response=dao.switchPostByDBAInterface();
            Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
        }
        catch (Exception e){
            log.error("切换失败",e);
        }

        try {
            log.info("10 seconds wait...");
            Thread.sleep(10000);
            log.info("After switch");
            String hostName2 = dao.selectHostname(null);
            log.info(String.format("---------------current hostname is: %s ", hostName2));
            Assert.assertNotEquals(hostName1, hostName2);
        }
        catch (Exception e){
            log.error("Qconfig没有在10秒内推送切换通知，请检查问题",e);
       }
    }*/

    @Test
    public void testDynamicDatasourceWithSingleKey() throws Exception {
        int i = 0;
//        long startTime=System.currentTimeMillis();
        while (1 == 1) {
            try {
                log.info(String.format("Test %d started", i));
                log.info(String.format("---------------current hostname is: %s ", dao.selectHostname(null)));
                log.info(String.format("---------------current database is: %s ", dao.selectDatabase(null)));
                log.info("clear data ", dao.test_def_update(new DalHints()));
                List<DRTestPojo> list=new ArrayList<>();
                DRTestPojo daoPojo1 = new DRTestPojo();
                daoPojo1.setName("name1");
                DRTestPojo daoPojo2 = new DRTestPojo();
                daoPojo2.setName("name2");
                list.add(daoPojo1);
                list.add(daoPojo2);
                log.info("insert data ");
                int[] ret=dao.batchInsert(null,list);
                log.info(String.format("batchInsert returned values: %d, %d",ret[0],ret[1]));
                log.info("query data " + dao.queryByPk(1, null).getName());
//                log.info("10 seconds query...");
//                dao.testLongQuery(10,null);
                log.info(String.format("Test %d passed", i));
            } catch (Exception e) {
                e.printStackTrace();
                log.error(String.format("Test %d failed", i), e);
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

    }

    @Test
    public void testDynamicDatasourceWithRWKeys() throws Exception {
        int i = 0;
//        long startTime=System.currentTimeMillis();
        while (1 == 1) {
            try {
                log.info(String.format("Test %d started", i));
                log.info(String.format("---------------current hostname of master db is: %s ", dao.selectHostname(new DalHints().masterOnly())));
                log.info(String.format("---------------current hostname of slave db is: %s ", dao.selectHostname(null)));
//                log.info(String.format("---------------current database is: %s ",dao.selectDatabase(null)));
                log.info("clear data ", dao.test_def_update(new DalHints()));
                DRTestPojo daoPojo = new DRTestPojo();
                daoPojo.setName(dao.selectHostname(new DalHints().masterOnly()));
                log.info("insert data ", dao.insert(null, daoPojo));
                log.info("query data " + dao.queryByPk(1, new DalHints().masterOnly()).getName());
//                log.info("10 seconds query...");
//                dao.testLongQuery(10,null);
                log.info(String.format("Test %d passed", i));
            } catch (Exception e) {
                log.error(String.format("Test %d failed", i), e);
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
    }

    @Test
    public void testDynamicDatasourceWithMultipleKeys() throws Exception {
        int i = 0;
//        long startTime=System.currentTimeMillis();
        DRTestDao dao = new DRTestDao("shardTestOnMysql");
        while (1 == 1) {
            try {
                log.info(String.format("Test %d started", i));
                log.info(String.format("---------------current hostname of mysqldaltest01db_W is: %s ", dao.selectHostname(new DalHints().inShard(0))));
                log.info(String.format("---------------current database of mysqldaltest01db_W is: %s ", dao.selectDatabase(new DalHints().inShard(0))));
                log.info(String.format("---------------current hostname of mysqldaltest02db_W is: %s ", dao.selectHostname(new DalHints().inShard(1))));
                log.info(String.format("---------------current database of mysqldaltest02db_W is: %s ", dao.selectDatabase(new DalHints().inShard(1))));
                log.info("clear data in all shards", dao.test_def_update(new DalHints().inShard(0)));
                DRTestPojo daoPojo = new DRTestPojo();
//                daoPojo.setName(dao.selectHostname(null));
                daoPojo.setName("testDynamicDatasourceWithMultipleKeys");
                log.info("insert data to testtable in mysqldaltest01db", dao.insert(new DalHints().inShard(0), daoPojo));
                log.info("query data from testtable in mysqldaltest01db : " + dao.queryByPk(1, new DalHints().inShard(0)).getName());
                log.info("insert data to testtable in mysqldaltest02db", dao.insert(new DalHints().inShard(1), daoPojo));
                log.info("query data from testtable in mysqldaltest02db : " + dao.queryByPk(1, new DalHints().inShard(1)).getName());
//                log.info("10 seconds query...");
//                dao.testLongQuery(10,null);
                log.info(String.format("Test %d passed", i));
            } catch (Exception e) {
                log.error(String.format("Test %d failed", i), e);
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
    public void testThread() throws Exception {
        int i = 0;
//        long startTime=System.currentTimeMillis();

        while (i < 30) {
            try {
                log.info(String.format("Test %d started", i));
                log.info(String.format("current hostnames: "));
                List<String> hostNames = dao.selectHostnameInAllShards(new DalHints().inAllShards());
                for (String hostName : hostNames) {
//                    log.info(hostName);
                    System.out.print(hostName + " ");
                }
                /*log.info("clear data ",dao.test_def_update(new DalHints()));
                DRTestPojo daoPojo = new DRTestPojo();
                daoPojo.setName(dao.selectHostname(null));
                log.info("insert data ",dao.insert(null, daoPojo));
                log.info("query data "+dao.queryByPk(1, null).getName());*/
                log.info(String.format("Test %d passed", i));
            } catch (Exception e) {
                log.error(String.format("Test %d failed", i), e);
            }
            i++;
            Thread.sleep(1000);
            /*long endTime=System.currentTimeMillis();
            long costTime=endTime-startTime;
            System.out.println(String.format("costTime: %d",costTime));
            if(costTime>=25000&&costTime<30000){
                System.out.println("25s...waiting for validation...");
                Thread.sleep(5000);}*/
        }

    }


    @Test
    public void testShard() throws Exception {
//        System.setOut(new PrintStream(new File("shard.txt")));
//        for(int i=0;i<500;i++){
//            System.out.println("<add name=\"mysqldaltest01db_W\" databaseType=\"Master\" sharding=\"\" connectionString=\"mysqldaltest01db_W\"/>");
//        }
        DalHints hints = new DalHints();
        List<String> hostNames = dao.selectHostnameInAllShards(hints.inAllShards());
//        Future<List<String>> fr = (Future<List<String>>) hints
//                .getAsyncResult();
//        hostNames = fr.get(); // 异步返回结果
//        Assert.assertEquals(51,hostNames.size());
        System.out.println(hostNames.size());
    }

    public void netstatCMD() throws Exception {
        String[] cmd = {"cmd", "/c", "netstat -ano | findstr \"10.2.74\" | findstr \"ESTABLISHED\""};
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            InputStream in = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line;
            log.info("current netstat");
            while (((line = br.readLine())) != null) {
                log.info(line);
//                Assert.assertEquals(-1,line.indexOf("10.32.21.149:3306"));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

   /* @Test
    public void testNetStat() throws Exception {
        log.info(String.format("****************query hostname before switch: %s", dao.selectHostname(null)));
        netstatCMD();
        Thread.sleep(2000);
        dao.postByMHA();
        log.info(String.format("****************sleep 5s after switch..."));
        Thread.sleep(5000);
        log.info(String.format("****************query hostname after switch: %s", dao.selectHostname(null)));
        Thread.sleep(2000);
        netstatCMD();
        log.info(String.format("Done"));
    }*/
}
