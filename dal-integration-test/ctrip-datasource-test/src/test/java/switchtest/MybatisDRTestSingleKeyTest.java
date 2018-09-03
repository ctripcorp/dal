package switchtest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mybatis.mysql.datasource1.DRTestMapperDao;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import util.ConnectionStringSwitch;
import util.PoolPropertiesSwitch;
import util.netstat.NetStat;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

/**
 * Created by lilj on 2018/3/5.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/application-context-singlekey.xml")
public class MybatisDRTestSingleKeyTest {
    @Autowired
    private DRTestMapperDao drTestMapperDao;
    private String keyName1 = "mysqldaltest01db_W";
    private static Logger log = LoggerFactory.getLogger(MybatisDRTestSingleKeyTest.class);
    private static ConnectionStringSwitch connectionStringSwitch = new ConnectionStringSwitch();
    private static PoolPropertiesSwitch poolPropertiesSwitch = new PoolPropertiesSwitch();
    private static NetStat netStat = new NetStat();
    private static Boolean isPro = true;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
//        poolPropertiesSwitch.resetPoolProperties();
    }

    @Before
    public void setUp() throws Exception {
        connectionStringSwitch.resetConnectionString(isPro);
//        Thread.sleep(5000);
//        poolPropertiesSwitch.resetPoolProperties();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        connectionStringSwitch.resetConnectionString(isPro);
//        Thread.sleep(5000);
//        poolPropertiesSwitch.resetPoolProperties();
    }
    /*@Test
    public void testLongQuery() throws Exception {
        *//*log.info("clear data ");
        drTestMapperDao.truncateTable();
        log.info("insert data ");
        drTestMapperDao.addDRTestMybatisPojo();*//*
        DalDataSourceFactory dalDataSourceFactory=new DalDataSourceFactory();
        DataSource ds = dalDataSourceFactory.createDataSource("mysqldaltest01db_W");
        Connection connection=ds.getConnection();
        int i = 1;
//        while (i < 10) {
        try {
//                if (i == 2) {
            log.info("30s query...");
//                    drTestMapperDao.testLongQuery();

            connection.createStatement().execute("select name from testTable where sleep(30) = 0 limit 1");
//                } else {
//                    log.info(String.format("currentHostname is: %s", drTestMapperDao.getHostNameMySQL()));
//                }

            log.info(String.format("test %d passed",i));
        } catch (Exception e) {
            e.printStackTrace();
            log.info(String.format("test %d failed",i));
//                Thread.sleep(100000);
        }
        finally {
            log.info("close connection");
            connection.close();
            log.info("connection closed");
        }
        i++;
        Thread.sleep(100000);
//        }
    }*/


    /*@Test
    public void testDynamicDatasourceWithSingleKey() throws Exception {
        int i=0;
        while(1==1){
            try{
                log.info(String.format("Test %d started",i));
                log.info(String.format("---------------current hostname is: %s ",drTestMapperDao.getHostNameMySQL()));
                log.info("clear data ");
                drTestMapperDao.truncateTable();
                log.info("insert data ");
                drTestMapperDao.addDRTestMybatisPojo();
                log.info("update data ");
                drTestMapperDao.updateDRTestMybatisPojo();
                log.info("query data ");
                drTestMapperDao.getDRTestMybatisPojo();
                log.info(String.format("Test %d passed",i));
            }
            catch (Exception e){
                log.error(String.format("Test %d failed",i),e);
            }
            i++;
            Thread.sleep(1000);
        }

    }*/


    @Test
    public void autoTestSwitchSingleKeyWithInvalidIp() throws Exception {
        String invalidIp = "10.2.74.1111";

        JsonArray jsonArray = new JsonArray();
        JsonObject subJson = new JsonObject();
        subJson.addProperty("keyname", keyName1);
        subJson.addProperty("server", invalidIp);
        subJson.addProperty("port", "55111");
        jsonArray.add(subJson);


        //切换前
        log.info(String.format("before switch"));

        //检查当前hostname
        String hostname = drTestMapperDao.getHostNameMySQL();
        log.info(String.format("---------------current hostname is: %s ", hostname));
//        assertEquals(currentHostname, queryHostName(keyName1, null, autoTestSwitchSingleKeyWithInvalidIpDao));

        //开始用无效ip切换
        log.info(String.format("start switch with invalid serverIp"));
        connectionStringSwitch.postByQconfig(jsonArray, isPro);

        try {
            //本次切换假设通知推送正常，无需30秒轮询，故保险起见把等待时间延长至5秒
            log.info("3 seconds wait...");
            Thread.sleep(3000);

            //检查切换是否生效
            log.info("start the first validation after invalid ip switch");
            String currentHostname = drTestMapperDao.getHostNameMySQL();
            log.info(String.format("---------------current hostname is: %s ", currentHostname));
            assertNotEquals(hostname, currentHostname);

            //如果查询hostName没有抛异常则测试案例失败
            fail("创建数据源本该失败，但是没有，请检查是否没有收到QConfig通知");

        } catch (Exception e) {
            log.info("切换无效IP生效", e.getMessage());

            //重新切换到正确的ip
            log.info("reset ip");
            connectionStringSwitch.postByMHA(isPro);

            try {
                //等待3秒重新创建数据源
                log.info("3 seconds wait...");
                Thread.sleep(3000);

                log.info("start the second time validation after the recovery switch");
                String currentHostname = drTestMapperDao.getHostNameMySQL();
                log.info(String.format("---------------current hostname is: %s ", currentHostname));
                assertNotEquals(hostname, currentHostname);

                log.info("clear data ");
                drTestMapperDao.truncateTable();
                log.info("insert data ");
                drTestMapperDao.addDRTestMybatisPojo();

                log.info("恢复IP切换生效");
            } catch (Exception ex) {
                log.error("恢复IP后Qconfig没有在5秒内推送切换通知，请检查问题", ex);
                fail();
            }
        }
    }

    @Test
    public void confirmNoNewConnectionsToOldMaster() throws Exception {
        String currentHostname = drTestMapperDao.getHostNameMySQL();
        log.info(String.format("****************query hostname before switch: %s", currentHostname));
        netStat.netstatCMD(currentHostname, true);
        Thread.sleep(2000);
        log.info("switch starts");
        connectionStringSwitch.postByMHA(isPro);
        /*log.info(String.format("****************sleep 5s after switch..."));
        Thread.sleep(5000);*/
        //检查切换是否生效
        try {
            log.info("开始切换生效检查");
            long startTime = System.currentTimeMillis();
            String hostname;
            boolean toBeContinued = true;
            int times = 1;
            while (toBeContinued) {
                try {
                    log.info(String.format("第 %d 次请求开始 ", times));
                    hostname = drTestMapperDao.getHostNameMySQL();
                    //如果斷言成立，即hostname已經改變，則說明切換生效
                    assertNotEquals(currentHostname, hostname);
                    //切換后修改當前hostname
                    currentHostname = hostname;
                    toBeContinued = false;
                    log.info(String.format("第 %d 次请求成功 ", times));
                } catch (Throwable e) {
                    //斷言失敗則說明切換還未生效，需要繼續等待
                    log.warn(String.format("第 %d 次请求失败，切換還在進行中 ", times));
                    //如果等待時間已經超過40秒（30秒輪詢一次），說明此次切換失敗，結束等待；否則繼續等待
                    if ((System.currentTimeMillis() - startTime) < 40000) {
                        toBeContinued = true;
                        times++;
                    } else {
                        log.warn(String.format("等待切换时间超过40秒，本次切换失败 "));
                        toBeContinued = false;
                        fail();
                    }
                }
            }
            //检查读写操作是否正常
            log.info("开始检查写操作是否正常");
            drTestMapperDao.addDRTestMybatisPojo();
//            checkQueryAndUpdate("setNameAfterTheFirstSwitch", null, dao);
            log.info("读写操作正常，切换生效");
        } catch (Exception e) {
            log.warn("切换检查失败，本次切換失敗", e);
            fail();
        }
        netStat.netstatCMD(currentHostname, true);
        log.info(String.format("Done"));
    }
    /*@Test
    public void testCatLog() throws Exception{
        log.info("start");
//        DalClientFactory.initClientFactory();
        log.info(drTestMapperDao.getHostNameMySQL());
        log.info("sleep 100s");
        Thread.sleep(100000);
        log.info(drTestMapperDao.getHostNameMySQL());
        log.info("done");
    }*/
}
