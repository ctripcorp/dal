package switchtest.mybatis;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocatorManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mybatis.mysql.DRTestDao;
import mybatis.mysql.DRTestPojo;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testUtil.ConnectionStringSwitch;
import testUtil.PoolPropertiesSwitch;
import testUtil.netstat.NetStat;

import static org.junit.Assert.*;


/**
 * Created by lilj on 2018/2/27.
 */
public class ConnectionStringSwitchTest {
    private static DRTestDao dao = null;
    private static DRTestDao shardingDao = null;
    private static ConnectionStringSwitch connectionStringSwitch = null;
    private static PoolPropertiesSwitch poolPropertiesSwitch = null;
    private static NetStat netStat = null;
    private static Logger log = LoggerFactory.getLogger(ConnectionStringSwitchTest.class);
    private static String currentHostname;
    private String keyName1 = "mysqldaltest01db_W";
    private String keyName2 = "mysqldaltest02db_W";
    private static String databaseSet = "shardSwitchTestOnMysql";
    private static Boolean isPro = true;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.shutdownFactory();
        DalClientFactory.initClientFactory(ClassLoader.getSystemClassLoader().getResource(".").getPath() + "DalConfigForSwitch/Dal.config");
        dao = new DRTestDao();
        shardingDao = new DRTestDao(databaseSet);
        connectionStringSwitch = new ConnectionStringSwitch();
        poolPropertiesSwitch = new PoolPropertiesSwitch();
        netStat = new NetStat();
        poolPropertiesSwitch.resetPoolProperties();
    }


    @Before
    public void setUp() throws Exception {
        initialize();
//        poolPropertiesSwitch.resetPoolProperties();
    }

    @After
    public void tearDown() throws Exception {
//        connectionStringSwitch.resetConnectionString(isPro);
//        poolPropertiesSwitch.resetPoolProperties();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        connectionStringSwitch.resetConnectionString(isPro);
//        Thread.sleep(5000);
    }


    public void initialize() throws Exception {
        connectionStringSwitch.resetConnectionString(isPro);
        Thread.sleep(5000);
        currentHostname = dao.selectHostname(new DalHints());
        log.info(String.format("initialize currentHostname is：%s", currentHostname));
    }

    public String queryHostName(String keyName, DalHints hints, DRTestDao dao) throws Exception {
        String hostName = dao.selectHostname(hints);
        log.info(String.format("---------------current hostname of %s is: %s ", keyName, hostName));
        return hostName;
    }

    public void queryData(String name, DalHints hints, DRTestDao dao) throws Exception {
        log.info("query data ");
        try {
            assertEquals(name, dao.queryByPk(1, hints).getName());
        } catch (Error e) {
            e.printStackTrace();
            fail();
        }
    }

    public void clearData(DalHints hints, DRTestDao dao) throws Exception {
        log.info("clear data ", dao.test_def_update(hints));
    }

    public void insertData(String name, DalHints hints, DRTestDao dao) throws Exception {
        DRTestPojo daoPojo = new DRTestPojo();
        daoPojo.setName(name);
        log.info("insert data ", dao.insert(hints, daoPojo));
    }

    public void checkQueryAndUpdate(String name, DalHints hints, DRTestDao dao) throws Exception {
        clearData(hints, dao);
        insertData(name, hints, dao);
        queryData(name, hints, dao);
    }

    public void checkSwitchSucceed(DalHints hints, DRTestDao dao) throws Exception {
        try {
            log.info("开始切换生效检查");
            long startTime = System.currentTimeMillis();
            String hostname;
            boolean toBeContinued = true;
            int times = 1;
            while (toBeContinued) {
                try {
                    log.info(String.format("第 %d 次请求开始 ", times));
                    hostname = queryHostName(keyName1, hints, dao);
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
            checkQueryAndUpdate("setNameAfterTheFirstSwitch", hints, dao);
            log.info("读写操作正常，切换生效");
        } catch (Exception e) {
            log.warn("切换检查失败，本次切換失敗", e);
            fail();
        }
    }

    @Test
    public void testDomainToIp() throws Exception {
        String nullIp = "";
        String recoveryIp;
        String url;
        JsonArray jsonArray = new JsonArray();
        JsonObject subJsonWithNullIp = new JsonObject();
        subJsonWithNullIp.addProperty("keyname", keyName1);
        subJsonWithNullIp.addProperty("server", nullIp);
        subJsonWithNullIp.addProperty("port", "55111");
        jsonArray.add(subJsonWithNullIp);

        //将ip清空
        log.info(String.format("start clear serverIp"));
        connectionStringSwitch.postByQconfig(jsonArray, isPro);
        Thread.sleep(5000);

//        验证启动时拿的域名url
        DalClientFactory.initClientFactory();
        DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureLocatorManager.getInstance();
        DataSourceConfigure dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure(keyName1);
        url = dataSourceConfigure.getConnectionUrl();
        log.info(String.format("the initial url in domain mode is: %s", url));
        Assert.assertNotEquals(-1, url.indexOf("mysqldaltest01.mysql.db.fat.qa.nt.ctripcorp.com:55111"));

        if (currentHostname.equalsIgnoreCase("FAT1868"))
            recoveryIp = "10.2.74.111";
        else
            recoveryIp = "10.2.74.122";

        jsonArray.remove(0);
        JsonObject subJsonWithIp = new JsonObject();
        subJsonWithIp.addProperty("keyname", keyName1);
        subJsonWithIp.addProperty("server", recoveryIp);
        subJsonWithIp.addProperty("port", "55111");
        jsonArray.add(subJsonWithIp);

        log.info(String.format("switch domain to ip"));
        connectionStringSwitch.postByQconfig(jsonArray, isPro);
        Thread.sleep(5000);

        dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure(keyName1);
        url = dataSourceConfigure.getConnectionUrl();
        log.info(String.format("the url after switch in ip mode is: %s", url));
        Assert.assertNotEquals(-1, url.indexOf("10.2.74"));
        String hostnameAfterSwithToIp = dao.selectHostname(null);
        Assert.assertEquals(currentHostname, hostnameAfterSwithToIp);

        log.info(String.format("switch ip"));
        connectionStringSwitch.postByMHA(isPro);
        Thread.sleep(5000);

        dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure(keyName1);
        url = dataSourceConfigure.getConnectionUrl();
        log.info(String.format("the url after switch ip is: %s", url));
        Assert.assertNotEquals(-1, url.indexOf("10.2.74"));
        String hostnameAfterSwithIp = dao.selectHostname(null);
        Assert.assertNotEquals(currentHostname, hostnameAfterSwithIp);
    }

    @Test
    public void confirmNoNewConnectionsToOldMaster() throws Exception {
        log.info(String.format("****************query hostname before switch: %s", dao.selectHostname(null)));
        netStat.netstatCMD(currentHostname, true);
        log.info("switch starts");
        connectionStringSwitch.postByMHA(isPro);
        //检查切换是否生效
        checkSwitchSucceed(null, dao);
        netStat.netstatCMD(currentHostname, true);
        log.info(String.format("Done"));
    }


   /* @Test
    public void autoCycleTestDynamicDatasourceWithSingleKeyReturnCostTime() throws Exception {
        DRTestDao autoTestDynamicDatasourceWithSingleKeyDao = new DRTestDao();
        int i = 1;
        System.setOut(new PrintStream(new File("autoCycleTestDynamicDatasourceWithSingleKeyReturnCostTime.txt")));
        while (i <= 10) {
            log.info("Test " + i + " started");
            System.out.println("Test " + i + " started");

            try {
                log.info(String.format("切换前检查当前数据库状态"));
                log.info(String.format("1、检查当前hostname"));
                assertEquals(currentHostname, queryHostName(keyName1, null, autoTestDynamicDatasourceWithSingleKeyDao));
                log.info(String.format("2、检查当前数据库是否可写"));
                checkQueryAndUpdate("setNameBeforeTheFirstSwitch", null, autoTestDynamicDatasourceWithSingleKeyDao);
                log.info("当前数据库状态正常，准备切换");
            } catch (Throwable e) {
                log.warn("当前数据库状态异常，准备初始化", e);
                initialize();
            }

            log.info(String.format("开始切换"));
            System.out.println(String.format("开始切换"));

            long startTime = System.currentTimeMillis();
            long endTime = 0;
            long costTime = 0;

            //调用切换接口
            connectionStringSwitch.postByMHA(isPro);

            //检查切换是否生效
            try {
                log.info("开始切换生效检查");
                String hostname;
                boolean toBeContinued = true;
                int times = 1;
                while (toBeContinued) {
                    try {
                        log.info(String.format("第 %d 次请求开始 ", times));
                        hostname = queryHostName(keyName1, null, autoTestDynamicDatasourceWithSingleKeyDao);
                        //如果斷言成立，即hostname已經改變，則說明切換生效
                        assertNotEquals(currentHostname, hostname);
                        //切換一旦生效，則可以記錄生效時間
                        endTime = System.currentTimeMillis();
                        //計算切換所用時間
                        costTime = endTime - startTime;
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
                checkQueryAndUpdate("setNameAfterTheFirstSwitch", null, autoTestDynamicDatasourceWithSingleKeyDao);
                log.info("读写操作正常，切换生效");
                log.info(String.format("Test %d passed,cost %d ms", i, costTime));
                System.out.println(String.format("Test %d passed,cost %d ms", i, costTime));
            } catch (Exception e) {
                log.warn("切换检查失败，本次切換失敗", e);
                log.warn(String.format("Test %d failed", i));
                System.out.println(String.format(String.format("Test %d failed", i)));
                fail();
            }
            i++;
            Thread.sleep(1000);
        }
    }*/

    /*@Test
    public void autoCycleTestDynamicDatasourceWithSingleKey() throws Exception {
        DRTestDao autoTestDynamicDatasourceWithSingleKeyDao = new DRTestDao();
        int i = 1;
        while (i <= 200) {
            log.info("Test " + i + " started");
            log.info(String.format("切换前"));

            log.info(String.format("检查当前hostname"));
            assertEquals(currentHostname, queryHostName(keyName1, null, autoTestDynamicDatasourceWithSingleKeyDao));

            checkQueryAndUpdate("setNameBeforeTheFirstSwitch", null, autoTestDynamicDatasourceWithSingleKeyDao);

            log.info(String.format("开始切换"));
            postByMHA(autoTestDynamicDatasourceWithSingleKeyDao);

            try {
                //等待3秒钟切换生效
                log.info("3 seconds after the first switch...");
                Thread.sleep(3000);

                //检查切换是否生效
                log.info("切换后");
                try {
                    String hostname = queryHostName(keyName1, null, autoTestDynamicDatasourceWithSingleKeyDao);
                    assertNotEquals(currentHostname, hostname);
                    currentHostname = hostname;
                } catch (Error e) {
                    log.error("切换后hostname并未修改，切换未生效", e);
                }

                //检查读写操作是否正常
                checkQueryAndUpdate("setNameAfterTheFirstSwitch", null, autoTestDynamicDatasourceWithSingleKeyDao);

                log.info("切换生效");
                log.info("Test " + i + " passed");
            } catch (Exception e) {
                log.error("切换检查失败，或许要等待30秒轮询通知", e);

                try {
                    //再等30秒
                    log.info("30 seconds wait...");
                    Thread.sleep(30000);

                    //检查切换是否生效
                    log.info("start the second time validation");
                    try {
                        String hostname = queryHostName(keyName1, null, autoTestDynamicDatasourceWithSingleKeyDao);
                        assertNotEquals(currentHostname, hostname);
                        currentHostname = hostname;
                    } catch (Error error) {
                        log.error("切换后hostname并未修改，切换未生效", error);
                    }
                    //检查读写操作是否正常
                    checkQueryAndUpdate("setNameTheSecondValidation", null, autoTestDynamicDatasourceWithSingleKeyDao);

                    log.info("30秒轮询后切换生效");
                    log.info("Test " + i + " passed");
                } catch (Exception ex) {
                    log.error("30秒轮询检查依然失败，本次切换失败", e);
                    log.error("Test " + i + " failed");
                }

            }
            i++;
            Thread.sleep(1000);
        }
    }*/

    @Test
    public void autoTestDynamicDatasourceWithSingleKey() throws Exception {
//        DRTestDao autoTestDynamicDatasourceWithSingleKeyDao = new DRTestDao();
        log.info(String.format("切换前"));

        log.info(String.format("检查当前hostname"));
        assertEquals(currentHostname, queryHostName(keyName1, null, dao));

        checkQueryAndUpdate("setNameBeforeTheFirstSwitch", null, dao);

        log.info(String.format("开始第一次切换"));
        connectionStringSwitch.postByMHA(true);
        checkSwitchSucceed(null, dao);

        log.info(String.format("开始回切"));
        connectionStringSwitch.postByMHA(true);
        checkSwitchSucceed(null, dao);
    }

    @Test
    public void autoTestSwitchSingleKeyWithInvalidIp() throws Exception {
        String invalidIp = "10.2.74.1111";

        JsonArray jsonArray = new JsonArray();
        JsonObject subJson = new JsonObject();
        subJson.addProperty("keyname", keyName1);
        subJson.addProperty("server", invalidIp);
        subJson.addProperty("port", "55111");
        jsonArray.add(subJson);

        DRTestDao autoTestSwitchSingleKeyWithInvalidIpDao = new DRTestDao();
        //切换前
        log.info(String.format("before switch"));

        //检查当前hostname
        assertEquals(currentHostname, queryHostName(keyName1, null, autoTestSwitchSingleKeyWithInvalidIpDao));

        //开始用无效ip切换
        log.info(String.format("start switch with invalid serverIp"));
        connectionStringSwitch.postByQconfig(jsonArray, isPro);

        try {
            log.info("3 seconds wait...");
            Thread.sleep(3000);

            //检查切换是否生效
            log.info("start the first validation after invalid ip switch");
            assertNotEquals(currentHostname, queryHostName(keyName1, null, autoTestSwitchSingleKeyWithInvalidIpDao));

            //如果查询hostName没有抛异常则测试案例失败
            fail("创建数据源本该失败，但是没有，请检查是否没有收到QConfig通知");

        } catch (Exception e) {
            log.info("切换无效IP生效");

            //重新切换到正确的ip
            log.info("reset ip");
            connectionStringSwitch.postByMHA(isPro);

            try {
                //等待3秒重新创建数据源
                log.info("3 seconds wait...");
                Thread.sleep(3000);

                log.info("start the second time validation after the recovery switch");
                assertNotEquals(currentHostname, queryHostName(keyName1, null, autoTestSwitchSingleKeyWithInvalidIpDao));

                checkQueryAndUpdate("setNameTheRecoverySwitch", null, autoTestSwitchSingleKeyWithInvalidIpDao);

                log.info("恢复IP切换生效");
            } catch (Exception ex) {
                log.error("恢复IP后Qconfig没有在5秒内推送切换通知，请检查问题", ex);
                fail();
            }
        }
    }

    /*@Test
    public void autoTestDynamicDatasourceWithMultipleKeysReturnCostTime() throws Exception {
        DRTestDao autoTestDynamicDatasourceWithMultipleKeysDao = new DRTestDao(databaseSet2);
        int i = 1;
        System.setOut(new PrintStream(new File("autoTestDynamicDatasourceWithMultipleKeysReturnCostTime.txt")));
        while (i <= 10) {

            log.info("Test " + i + " started");
            System.out.println("Test " + i + " started");

            try {
                log.info(String.format("切换前检查当前数据库状态"));
                log.info(String.format("1、检查当前hostname"));
                assertEquals(currentHostname, queryHostName(keyName1, new DalHints().inShard(0), autoTestDynamicDatasourceWithMultipleKeysDao));
                assertEquals(currentHostname, queryHostName(keyName2, new DalHints().inShard(1), autoTestDynamicDatasourceWithMultipleKeysDao));
                log.info(String.format("2、检查当前数据库是否可写"));
                checkQueryAndUpdate("setNameBeforeSwitch0", new DalHints().inShard(0), autoTestDynamicDatasourceWithMultipleKeysDao);
                checkQueryAndUpdate("setNameBeforeSwitch1", new DalHints().inShard(1), autoTestDynamicDatasourceWithMultipleKeysDao);
                log.info("当前数据库状态正常，准备切换");
            } catch (Throwable e) {
                log.warn("当前数据库状态异常，准备初始化", e);
                initialize();
            }
            //start switch
            log.info(String.format("开始切换"));
            System.out.println(String.format("开始切换"));

            long startTime = System.currentTimeMillis();
            long endTime = 0;
            long costTime = 0;

            connectionStringSwitch.postByMHA(isPro);

            //检查切换是否生效
            try {
                log.info("开始切换生效检查");
                String hostname1;
                String hostname2;
                boolean toBeContinued = true;
                int times = 1;
                while (toBeContinued) {
                    try {
                        log.info(String.format("第 %d 次请求开始 ", times));
                        hostname1 = queryHostName(keyName1, new DalHints().inShard(0), autoTestDynamicDatasourceWithMultipleKeysDao);
                        hostname2 = queryHostName(keyName2, new DalHints().inShard(1), autoTestDynamicDatasourceWithMultipleKeysDao);
                        //如果斷言成立，即hostname已經改變，則說明切換生效
                        assertNotEquals(currentHostname, hostname1);
                        assertNotEquals(currentHostname, hostname2);
                        //切換一旦生效，則可以記錄生效時間
                        endTime = System.currentTimeMillis();
                        //計算切換所用時間
                        costTime = endTime - startTime;
                        //切換后修改當前hostname
                        currentHostname = hostname1;
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
                            log.warn(String.format("等待生效时间超过40秒，此次切换失败"));
                            toBeContinued = false;
                            fail();
                        }
                    }
                }
                //检查读写操作是否正常
                log.info("开始检查写操作是否正常");
                checkQueryAndUpdate("setNameAfterSwitch0", new DalHints().inShard(0), autoTestDynamicDatasourceWithMultipleKeysDao);
                checkQueryAndUpdate("setNameAfterSwitch1", new DalHints().inShard(1), autoTestDynamicDatasourceWithMultipleKeysDao);

                log.info("读写操作正常，切换生效");
                log.info(String.format("Test %d passed,cost %d ms", i, costTime));
                System.out.println(String.format("Test %d passed,cost %d ms", i, costTime));

            } catch (Exception e) {
                log.warn("切换检查失败，本次切換失敗", e);
                log.warn(String.format("Test %d failed", i));
                System.out.println(String.format(String.format("Test %d failed", i)));
                fail();
            }
            i++;
            Thread.sleep(1000);
        }
    }*/

    @Test
    public void autoTestDynamicDatasourceWithMultipleKeys() throws Exception {
//        DRTestDao autoTestDynamicDatasourceWithMultipleKeysDao = new DRTestDao(databaseSet);
        //before switch
        log.info(String.format("before switch"));
        String initHostname = currentHostname;
        assertEquals(initHostname, queryHostName(keyName1, new DalHints().inShard(0), shardingDao));
        assertEquals(initHostname, queryHostName(keyName2, new DalHints().inShard(1), shardingDao));

        checkQueryAndUpdate("setNameBeforeSwitch0", new DalHints().inShard(0), shardingDao);
        checkQueryAndUpdate("setNameBeforeSwitch1", new DalHints().inShard(1), shardingDao);

        //start switch
        log.info(String.format("start switch"));
        connectionStringSwitch.postByMHA(true);

        //after switch
        checkSwitchSucceed(new DalHints().inShard(0), shardingDao);
        //检查切换是否成功
        log.info("After switch");
        assertNotEquals(initHostname, queryHostName(keyName1, new DalHints().inShard(0), shardingDao));
        assertNotEquals(initHostname, queryHostName(keyName2, new DalHints().inShard(1), shardingDao));

        checkQueryAndUpdate("setNameAfterSwitch0", new DalHints().inShard(0), shardingDao);
        checkQueryAndUpdate("setNameAfterSwitch1", new DalHints().inShard(1), shardingDao);

        log.info("切换已生效");
    }

    @Test
    public void autoTestSwitchMultipleKeysWithOneFailedAndOnePassed() throws Exception {
        String invalidIp = "10.2.74.1111";
        String validIp;
        String recoveryIp;

        DRTestDao autoTestSwitchMultipleKeysWithOneFailedAndOnePassedDao = new DRTestDao(databaseSet);

        //before switch
        log.info(String.format("before switch"));

        assertEquals(currentHostname, queryHostName(keyName1, new DalHints().inShard(0), autoTestSwitchMultipleKeysWithOneFailedAndOnePassedDao));
        assertEquals(currentHostname, queryHostName(keyName2, new DalHints().inShard(1), autoTestSwitchMultipleKeysWithOneFailedAndOnePassedDao));


        if (currentHostname.equals("FAT1868")) {
            validIp = "10.2.74.122";
            recoveryIp = "10.2.74.111";
        } else {
            validIp = "10.2.74.111";
            recoveryIp = "10.2.74.122";
        }


        JsonArray jsonArray = new JsonArray();
        JsonObject subJson = new JsonObject();
        subJson.addProperty("keyname", keyName1);
        subJson.addProperty("server", validIp);
        subJson.addProperty("port", "55111");
        jsonArray.add(subJson);
        JsonObject subJson2 = new JsonObject();
        subJson2.addProperty("keyname", keyName2);
        subJson2.addProperty("server", invalidIp);
        subJson2.addProperty("port", "55111");
        jsonArray.add(subJson2);

        //start switch
        log.info(String.format("start switch"));
        connectionStringSwitch.postByQconfig(jsonArray, isPro);
        log.info(String.format("switch succeed"));

        //after switch

        //等待3秒获取通知重建数据源
        log.info("3 seconds wait...");
        Thread.sleep(3000);

        //检查切换是否成功
        log.info("After switch");

        //mysqldaltest01db_W切换成功
//            log.info(String.format("---------------current hostname of mysqldaltest01db_W is: %s ", queryHostName("mysqldaltest01db_W",new DalHints().inShard(0),autoTestSwitchMultipleKeysWithOneFailedAndOnePassedDao)));
        try {
            assertNotEquals(currentHostname, queryHostName("mysqldaltest01db_W", new DalHints().inShard(0), autoTestSwitchMultipleKeysWithOneFailedAndOnePassedDao));
            log.info(String.format("%s 切换生效", keyName1));
        } catch (Throwable e) {
            log.error(String.format("%s 切换并未生效", keyName1), e.getMessage());
        }

        //mysqldaltest02db_W切换到非法IP
        try {
            assertNotEquals(currentHostname, queryHostName("mysqldaltest02db_W", new DalHints().inShard(1), autoTestSwitchMultipleKeysWithOneFailedAndOnePassedDao));
//            log.info(String.format("---------------current hostname of mysqldaltest02db_W is: %s ", queryHostName("mysqldaltest02db_W",new DalHints().inShard(1),autoTestSwitchMultipleKeysWithOneFailedAndOnePassedDao)));
            fail("重新创建mysqldaltest02db_W时应该失败，但是并没有，请检查切换异常IP是否成功");
        } catch (Throwable e) {
            log.info(String.format("%s 切换生效", keyName2));
        }


        log.info("恢复连接串");

        JsonArray jsonArray2 = new JsonArray();
        JsonObject subJson3 = new JsonObject();
        subJson3.addProperty("keyname", keyName1);
        subJson3.addProperty("server", recoveryIp);
        subJson3.addProperty("port", "55111");
        jsonArray2.add(subJson3);
        JsonObject subJson4 = new JsonObject();
        subJson4.addProperty("keyname", keyName2);
        subJson4.addProperty("server", recoveryIp);
        subJson4.addProperty("port", "55111");
        jsonArray2.add(subJson4);

        connectionStringSwitch.postByQconfig(jsonArray2, isPro);

        try {
            Thread.sleep(3000);

            log.info("validate after recovery");
            assertEquals(currentHostname, queryHostName(keyName1, new DalHints().inShard(0), autoTestSwitchMultipleKeysWithOneFailedAndOnePassedDao));
            assertEquals(currentHostname, queryHostName(keyName2, new DalHints().inShard(1), autoTestSwitchMultipleKeysWithOneFailedAndOnePassedDao));

            log.info("恢复连接串成功");
        } catch (Exception ex) {
            log.error("恢复切换失败，请检查", ex);
            fail();
        }
    }
}
