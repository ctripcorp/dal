package switchtest.mybatis;

import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import mybatis.mysql.DRTestDao;
import mybatis.mysql.DRTestPojo;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testUtil.ConnectionStringSwitch;
import testUtil.PoolPropertiesSwitch;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by lilj on 2018/2/27.
 */
public class PoolPropertiesSwitchTest {

    private static DRTestDao dao = null;
    private static PoolPropertiesSwitch poolPropertiesSwitch=null;
    private static ConnectionStringSwitch connectionStringSwitch=null;
    private static Logger log = LoggerFactory.getLogger(PoolPropertiesSwitchTest.class);
    private String keyName1 = "mysqldaltest01db_W";
    private String keyName2 = "mysqldaltest02db_W";
    private static Boolean isPro=true;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        /**
         * Initialize DalClientFactory.
         * The Dal.config can be specified from class-path or local file path.
         * One of follow three need to be enabled.
         **/
        DalClientFactory.shutdownFactory();
        DalClientFactory.initClientFactory(ClassLoader.getSystemClassLoader().getResource(".").getPath()+"DalConfigForSwitch/Dal.config");
        dao = new DRTestDao();
        poolPropertiesSwitch=new PoolPropertiesSwitch();
        connectionStringSwitch=new ConnectionStringSwitch();
    }

    @Before
    public void setUp() throws Exception {
        connectionStringSwitch.resetConnectionString(isPro);
        Thread.sleep(5000);
        poolPropertiesSwitch.resetPoolProperties();
    }

    @After
    public void tearDown() throws Exception {
//        poolPropertiesSwitch.resetPoolProperties();
    }

    @AfterClass
    public static void tearDownAfterClass() throws  Exception{
        connectionStringSwitch.resetConnectionString(isPro);
        Thread.sleep(5000);
        poolPropertiesSwitch.resetPoolProperties();
    }


    public String queryHostName(String keyName, DalHints hints, DRTestDao dao) throws Exception {
        String hostName = dao.selectHostname(hints);
        log.info(String.format("---------------current hostname of %s is: %s ", keyName, hostName));
        return hostName;
    }



    public void checkBatchInsertReturnListWithSingleKey(DRTestDao dao, int returnCode, String errorMsg, DalHints hints) throws Exception {
        List<DRTestPojo> list = new ArrayList<>();
        DRTestPojo pojo1 = new DRTestPojo();
        pojo1.setName("datasourceProperties1");
        list.add(pojo1);
        DRTestPojo pojo2 = new DRTestPojo();
        pojo2.setName("datasourceProperties2");
        list.add(pojo2);
        try {
            log.info("batchInsert data ");
            int[] ret = dao.batchInsert(hints, list);
            log.info("------------------ print result : " + ret[0] + "," + ret[1]);
            if (returnCode == -2)
                assertArrayEquals(new int[]{-2, -2}, ret);
            else
                assertArrayEquals(new int[]{1, 1}, ret);
        } catch (Error e) {
            log.error(errorMsg, e);
            fail();
        }
    }

    public void checkHostnameWithSingleKey(String hostnameBefore, String hostnameAfter, boolean isEqual, String errorMsg) {
        try {
            if (isEqual)
                assertEquals(hostnameBefore, hostnameAfter);
            else
                assertNotEquals(hostnameBefore, hostnameAfter);
        } catch (Throwable e) {
            log.error(errorMsg, e);
            fail();
        }
    }

    public void checkAfterDatasourceSwitch(DRTestDao dao, int returnCode, String errorMsg, DalHints hints) throws Exception {
        log.info("开始修改生效检查");
        long startTime = System.currentTimeMillis();
        boolean toBeContinued = true;
        int times = 1;
        while (toBeContinued) {
            try {
                log.info(String.format("第 %d 次请求开始", times));
                checkBatchInsertReturnListWithSingleKey(dao, returnCode, errorMsg, hints);
                toBeContinued = false;
                log.info(String.format("第 %d 次请求成功", times));
                log.info("本次检查通过");
            } catch (Throwable e) {
                //斷言失敗則說明切換還未生效，需要繼續等待
                log.warn(String.format("第 %d 次请求失败，还未收到通知", times));
                //如果等待時間已經超過40秒（30秒輪詢一次），說明此次切換失敗，結束等待；否則繼續等待
                if ((System.currentTimeMillis() - startTime) < 40000) {
                    toBeContinued = true;
                    times++;
                    Thread.sleep(1000);
                } else {
                    log.warn(String.format("等待切换时间超过40秒，本次切换失败"));
                    log.warn(String.format("本次检查失败"));
                    toBeContinued = false;
                    fail();
                }
            }
        }
    }


    public void checkAfterInvalidDatasourceSwitch(DRTestDao dao, int returnCode, String errorMsg, DalHints hints) {
        log.info("开始修改生效检查");
        long startTime = System.currentTimeMillis();
        boolean toBeContinued = true;
        int times = 1;
        while (toBeContinued) {
            try {
                log.info(String.format("第 %d 次请求开始", times));
                checkBatchInsertReturnListWithSingleKey(dao, returnCode, errorMsg, hints);
//                toBeContinued = true;
                log.info(String.format("第 %d 次请求并未异常,修改还未生效", times));
                //如果等待時間已經超過40秒（30秒輪詢一次），說明此次切換失敗，結束等待；否則繼續等待
                if ((System.currentTimeMillis() - startTime) < 40000) {
                    toBeContinued = true;
                    times++;

                    Thread.sleep(1000);
                } else {
                    log.warn(String.format("等待切换时间超过40秒，本次切换失败"));
                    log.warn(String.format("本次检查失败"));
                    toBeContinued = false;
                    fail();
                }
            } catch (Exception e) {
                //斷言失敗則說明切換還未生效，需要繼續等待
                log.warn(String.format("第 %d 次请求报异常，修改生效", times));
                toBeContinued = false;
                log.info("本次检查通过");
            }
        }
    }

    public void checkAfterMHASwitch(DRTestDao dao,String keyName, String hostname, DalHints hints) {
        //检查切换是否生效
        log.info("开始连接串切换生效检查");
        long startTime = System.currentTimeMillis();
        boolean toBeContinued = true;
        int times = 1;
        while (toBeContinued) {
            try {
                log.info(String.format("第 %d 次请求开始 ", times));
                //如果斷言成立，即hostname已經改變，則說明切換生效
                assertNotEquals(hostname, queryHostName(keyName, hints, dao));
                toBeContinued = false;
                log.info(String.format("第 %d 次请求成功 ", times));
                log.info(String.format("连接串切换生效"));
            } catch (Throwable e) {
                //斷言失敗則說明切換還未生效，需要繼續等待
                log.warn(String.format("第 %d 次请求失败，连接串切換還在進行中 ", times));
                //如果等待時間已經超過40秒（30秒輪詢一次），說明此次切換失敗，結束等待；否則繼續等待
                if ((System.currentTimeMillis() - startTime) < 40000) {
                    toBeContinued = true;
                    times++;
                } else {
                    log.warn(String.format("等待切换时间超过40秒，本次连接串切换失败 "));
                    toBeContinued = false;
                    fail();
                }
            }
        }
    }


    @Test
    public void modifyDatasourcePropertiesWithSingleKey() throws Exception {
        log.info(String.format("修改datasource之前，batchInsert返回值应该是-2"));
        checkBatchInsertReturnListWithSingleKey(dao, -2, "修改前，返回值异常", null);

        log.info(String.format("开始修改文件"));

        //修改rewriteBatchedStatements=false;
        Map<String, String> map = new HashMap<>();
        map.put("connectionProperties", "rewriteBatchedStatements=false;");
        poolPropertiesSwitch.modifyPoolProperties(map);

        log.info("修改文件后等待10秒钟");
        Thread.sleep(10000);

        log.info("由于enableDynamicPoolProperties未设置，修改datasource之后并未动态生效，batchInsert返回值仍然是-2");
        checkBatchInsertReturnListWithSingleKey(dao, -2, "enableDynamicPoolProperties未设置,返回值异常", null);

        log.info(String.format("修改enableDynamicPoolProperties为false"));
        map.put("enableDynamicPoolProperties", "false");
        poolPropertiesSwitch.modifyPoolProperties(map);

        log.info("修改文件后等待10秒钟");
        Thread.sleep(10000);

        log.info("由于enableDynamicPoolProperties设置为false，修改datasource之后并未动态生效，batchInsert返回值仍然是-2");
        checkBatchInsertReturnListWithSingleKey(dao, -2, "enableDynamicPoolProperties=false,返回值异常", null);

        log.info(String.format("修改enableDynamicPoolProperties为F"));
        map.put("enableDynamicPoolProperties", "F");
        poolPropertiesSwitch.modifyPoolProperties(map);

        log.info("修改文件后等待10秒钟");
        Thread.sleep(10000);

        log.info("由于enableDynamicPoolProperties设置为F，修改datasource之后并未动态生效，batchInsert返回值仍然是-2");
        checkBatchInsertReturnListWithSingleKey(dao, -2, "enableDynamicPoolProperties=F,返回值异常", null);

        log.info(String.format("修改enableDynamicPoolProperties为True"));
        map.put("enableDynamicPoolProperties", "True");
        poolPropertiesSwitch.modifyPoolProperties(map);

        log.info("由于enableDynamicPoolProperties设置为True，修改datasource之后动态生效，batchInsert返回值变成1");
        checkAfterDatasourceSwitch(dao, 1, "enableDynamicPoolProperties=True,返回值异常", null);

        log.info("测试通过！");
    }

    @Test
    public void modifyDatasourcePropertiesAndConnectionStringWithSingleKey() throws Exception {
        log.info(String.format("修改datasource之前，batchInsert返回值应该是-2"));
        checkBatchInsertReturnListWithSingleKey(dao, -2, "修改前，返回值异常", null);
        String currentHostname1 = queryHostName(keyName1, null, dao);

        log.info(String.format("开始修改文件"));

        //修改rewriteBatchedStatements=false;
        Map<String, String> map = new HashMap<>();
        map.put("connectionProperties", "rewriteBatchedStatements=false;");
        poolPropertiesSwitch.modifyPoolProperties(map);

        log.info("修改文件后等待35秒钟");
        Thread.sleep(35000);

        log.info("由于enableDynamicPoolProperties未设置，修改datasource之后并未动态生效，batchInsert返回值仍然是-2");
        checkBatchInsertReturnListWithSingleKey(dao, -2, "enableDynamicPoolProperties未设置,返回值异常", null);
        String currentHostname2 = queryHostName(keyName1, null, dao);
        checkHostnameWithSingleKey(currentHostname1, currentHostname2, true, "enableDynamicPoolProperties未设置，修改datasource之后并未动态生效,但是hostname变了");

        log.info(String.format("切换连接串"));
        connectionStringSwitch.postByMHA(isPro);

//      检查连接串切换是否生效
        checkAfterMHASwitch(dao, keyName1,currentHostname1, null);

        log.info("由于enableDynamicPoolProperties未设置，修改datasource之后并未动态生效，此时切换连接串也不该使得DataSource生效，batchInsert返回值仍然是-2");
        checkBatchInsertReturnListWithSingleKey(dao, -2, "enableDynamicPoolProperties未设置,切换连接串后使得DataSource生效", null);

        log.info(String.format("修改enableDynamicPoolProperties为True"));
        map.put("enableDynamicPoolProperties", "True");
        poolPropertiesSwitch.modifyPoolProperties(map);

        log.info("由于enableDynamicPoolProperties设置为True，修改datasource之后动态生效，batchInsert返回值变成1");
        checkAfterDatasourceSwitch(dao, 1, "enableDynamicPoolProperties=True,返回值异常", null);

        log.info("测试通过！");
    }

    @Test
    public void modifyDatasourcePropertiesAndConnectionStringWithMultipleKeys() throws Exception {
        DRTestDao dao1 = new DRTestDao("shardSwitchTestOnMysql");
        log.info(String.format("修改datasource之前，batchInsert返回值应该是-2"));
        checkBatchInsertReturnListWithSingleKey(dao1, -2, "修改前，shard0返回值异常", new DalHints().inShard(0));
        checkBatchInsertReturnListWithSingleKey(dao1, -2, "修改前，shard1返回值异常", new DalHints().inShard(1));
        String currentHostname1 = queryHostName(keyName1, new DalHints().inShard(0), dao1);
        String currentHostname2 = queryHostName(keyName2, new DalHints().inShard(1), dao1);
        assertEquals("两个key所在hostname不一致", currentHostname1, currentHostname2);

        log.info(String.format("开始修改文件"));

        //修改mysqldaltest01db_W.connectionProperties = rewriteBatchedStatements=false;
        Map<String, String> map = new HashMap<>();
        map.put("mysqldaltest01db_W.connectionProperties", "rewriteBatchedStatements=false;");
        poolPropertiesSwitch.modifyPoolProperties(map);
        log.info("修改文件后等待35秒钟");
        Thread.sleep(35000);

        log.info("由于enableDynamicPoolProperties未设置，修改datasource之后并未动态生效，batchInsert返回值仍然是-2");
        checkBatchInsertReturnListWithSingleKey(dao1, -2, "enableDynamicPoolProperties未设置，shard0返回值异常", new DalHints().inShard(0));
        checkBatchInsertReturnListWithSingleKey(dao1, -2, "enableDynamicPoolProperties未设置, shard1返回值异常", new DalHints().inShard(1));

        log.info(String.format("切换连接串"));
        connectionStringSwitch.postByMHA(isPro);
//      检查连接串切换是否生效
        checkAfterMHASwitch(dao1,keyName1, currentHostname1, new DalHints().inShard(0));
        checkAfterMHASwitch(dao1,keyName2, currentHostname2, new DalHints().inShard(1));

        log.info("由于enableDynamicPoolProperties未设置，修改datasource之后并未动态生效，此时切换连接串也不该使得DataSource生效，batchInsert返回值仍然是-2");
        checkBatchInsertReturnListWithSingleKey(dao1, -2, "enableDynamicPoolProperties未设置,切换连接串后使得DataSource生效", new DalHints().inShard(0));
        checkBatchInsertReturnListWithSingleKey(dao1, -2, "enableDynamicPoolProperties未设置,切换连接串后使得DataSource生效", new DalHints().inShard(1));

        log.info(String.format("修改enableDynamicPoolProperties为true"));
        map.put("enableDynamicPoolProperties", "true");
        poolPropertiesSwitch.modifyPoolProperties(map);

        log.info("由于enableDynamicPoolProperties设置为true，修改datasource之后动态生效，shard0的batchInsert返回值变成1，shard1还是-2");
        checkAfterDatasourceSwitch(dao1, 1, "enableDynamicPoolProperties=true,返回值异常", new DalHints().inShard(0));
        checkBatchInsertReturnListWithSingleKey(dao1, -2, "enableDynamicPoolProperties=true,返回值异常", new DalHints().inShard(1));

        log.info("测试通过！");
    }

    @Test
    public void modifyDatasourcePropertiesWithMultipleKeys() throws Exception {
        DRTestDao dao1 = new DRTestDao("shardSwitchTestOnMysql");
        log.info(String.format("修改datasource之前，batchInsert返回值应该是-2"));
        checkBatchInsertReturnListWithSingleKey(dao1, -2, "修改前，shard0返回值异常", new DalHints().inShard(0));
        checkBatchInsertReturnListWithSingleKey(dao1, -2, "修改前，shard1返回值异常", new DalHints().inShard(1));

        log.info(String.format("开始修改文件"));

        Map<String, String> map = new HashMap<>();
        map.put("mysqldaltest01db_W.connectionProperties", "rewriteBatchedStatements=false;");
        poolPropertiesSwitch.modifyPoolProperties(map);

        log.info("修改文件后等待35秒钟");
        Thread.sleep(35000);

        log.info("由于enableDynamicPoolProperties未设置，修改datasource之后并未动态生效，batchInsert返回值仍然是-2");
        checkBatchInsertReturnListWithSingleKey(dao1, -2, "enableDynamicPoolProperties未设置，shard0返回值异常", new DalHints().inShard(0));
        checkBatchInsertReturnListWithSingleKey(dao1, -2, "enableDynamicPoolProperties未设置, shard1返回值异常", new DalHints().inShard(1));

        log.info(String.format("修改enableDynamicPoolProperties为False"));
        map.put("enableDynamicPoolProperties", "False");
        poolPropertiesSwitch.modifyPoolProperties(map);

        log.info("修改文件后等待35秒钟");
        Thread.sleep(35000);

        log.info("由于enableDynamicPoolProperties设置为False，修改datasource之后并未动态生效，batchInsert返回值仍然是-2");
        checkBatchInsertReturnListWithSingleKey(dao1, -2, "enableDynamicPoolProperties设置为False，shard0返回值异常", new DalHints().inShard(0));
        checkBatchInsertReturnListWithSingleKey(dao1, -2, "enableDynamicPoolProperties设置为False，shard1返回值异常", new DalHints().inShard(1));

        log.info(String.format("修改enableDynamicPoolProperties为f"));
        map.put("enableDynamicPoolProperties", "f");
        poolPropertiesSwitch.modifyPoolProperties(map);

        log.info("修改文件后等待35秒钟");
        Thread.sleep(35000);

        log.info("由于enableDynamicPoolProperties设置为f，修改datasource之后并未动态生效，batchInsert返回值仍然是-2");
        checkBatchInsertReturnListWithSingleKey(dao1, -2, "enableDynamicPoolProperties设置为f，shard0返回值异常", new DalHints().inShard(0));
        checkBatchInsertReturnListWithSingleKey(dao1, -2, "enableDynamicPoolProperties设置为f，shard1返回值异常", new DalHints().inShard(1));

        log.info(String.format("修改enableDynamicPoolProperties为true"));
        map.put("enableDynamicPoolProperties", "true");
        poolPropertiesSwitch.modifyPoolProperties(map);

        log.info("由于enableDynamicPoolProperties设置为true，修改datasource之后动态生效，shard0的batchInsert返回值变成1，shard还是-2");
        checkAfterDatasourceSwitch(dao1, 1, "enableDynamicPoolProperties=true,返回值异常", new DalHints().inShard(0));
        checkBatchInsertReturnListWithSingleKey(dao1, -2, "enableDynamicPoolProperties=true,返回值异常", new DalHints().inShard(1));

        log.info("测试通过！");
    }


    @Test
    public void modifyDatasourcePropertiesWithSingleKeyAndFail() throws Exception {
        log.info(String.format("修改datasource之前，batchInsert正常"));
        checkBatchInsertReturnListWithSingleKey(dao, -2, "修改datasource之前，batchInser返回值异常", null);

        log.info(String.format("开始修改文件"));
        Map<String, String> map = new HashMap<>();
        map.put("maxAge", "哦");
        poolPropertiesSwitch.modifyPoolProperties(map);

        log.info("修改文件后等待35秒钟");
        Thread.sleep(35000);

        log.info("由于开关未开启，错误修改并未生效");
        checkBatchInsertReturnListWithSingleKey(dao, -2, "开关未开启，batchInser操作出现异常", null);

        log.info(String.format("修改enableDynamicPoolProperties为true"));
        map.put("enableDynamicPoolProperties", "true");
        poolPropertiesSwitch.modifyPoolProperties(map);

        checkAfterInvalidDatasourceSwitch(dao, -2, "enableDynamicPoolProperties为true,batchInsert操作应该出现异常且已经出现异常", null);

        log.info("恢复文件");
        map.put("maxAge", "28000000");
        poolPropertiesSwitch.modifyPoolProperties(map);

        log.info("恢复datasource之后，batchInsert恢复正常");
        checkAfterDatasourceSwitch(dao, -2, "恢复文件后，batchInsert应该恢复正常但是并没有", null);

        log.info("测试通过");
    }

    public Map<String, String> getOriginalDatasourceProperties() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("testWhileIdle", "false");//原值为false
        map.put("testOnBorrow", "true");//原值为true
        map.put("testOnReturn", "false");//原值为false
        map.put("validationQuery", "SELECT 1");//原值为SELECT 1
//        map.put("validationQueryTimeout", "5");//原来没有这个设置，默认值为5
        map.put("validationInterval", "30000");//原值为30000
        map.put("validatorClassName", "com.ctrip.platform.dal.dao.datasource.DataSourceValidator");//原值为com.ctrip.platform.dal.dao.datasource.DataSourceValidator
        map.put("timeBetweenEvictionRunsMillis", "5000");//原值为5000
        map.put("maxActive", "100");//原值为100
        map.put("minIdle", "1");//原值为1
        map.put("maxWait", "10000");//原值为10000
        map.put("maxAge", "28000000");//原值为28000000
        map.put("initialSize", "1");//原值为1
        map.put("removeAbandonedTimeout", "65");//原值为65
        map.put("removeAbandoned", "true");//原值为true
        map.put("logAbandoned", "false");//原值为false
        map.put("minEvictableIdleTimeMillis", "30000");//原值为30000
        //原值为sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8
        map.put("connectionProperties", "sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8;useSSL=false;socketTimeout=100000");
        //原来没有设置
//        map.put("intSQL", "set names utf8mb4");
        //设置了应该也无效，对connectionProperties没有影响
//        map.put("option", "rewriteBatchedStatements=true;allowMultiQueries=true");
        return map;
    }

    public Map<String, String> getModifiedDatasourceProperties() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("testWhileIdle", "true");//原值为false
        map.put("testOnBorrow", "false");//原值为true
        map.put("testOnReturn", "true");//原值为false
        map.put("validationQuery", "SELECT 100");//原值为SELECT 1
        map.put("validationQueryTimeout", "10");//原来没有这个设置，默认值为5
        map.put("validationInterval", "40000");//原值为30000
        map.put("validatorClassName", "com.ctrip.platform.dal.dao.datasource.DataSourceValidator");//原值为com.ctrip.platform.dal.dao.datasource.DataSourceValidator
        map.put("timeBetweenEvictionRunsMillis", "5500");//原值为5000
        map.put("maxActive", "150");//原值为100
        map.put("minIdle", "2");//原值为1
        map.put("maxWait", "11000");//原值为10000
        map.put("maxAge", "30000000");//原值为28000000
        map.put("initialSize", "2");//原值为1
        map.put("removeAbandonedTimeout", "70");//原值为65
        map.put("removeAbandoned", "false");//原值为true
        map.put("logAbandoned", "true");//原值为false
        map.put("minEvictableIdleTimeMillis", "40000");//原值为30000
        //原值为sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8
        map.put("connectionProperties", "rewriteBatchedStatements=false;allowMultiQueries=false");
        //原来没有设置
        map.put("intSQL", "set names utf8mb4");
        //设置了应该也无效，对connectionProperties没有影响
//        map.put("option", "rewriteBatchedStatements=true;allowMultiQueries=true");
        return map;
    }


    //    PoolProperties pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("CorpPerformanceManagementDB_W"));
    @Test
    public void modifyAllDatasourceProperties() throws Exception {
        try {
            log.info(String.format("修改datasource之前，检查datasource各属性值"));
            TitanProvider provider = new TitanProvider();
            Set<String> dbNames = new HashSet<>();
            dbNames.add(keyName1);
            Map<String, String> settings = new HashMap<>();
            provider.initialize(settings);
            provider.setup(dbNames);
            try {
                Properties pc = provider.getDataSourceConfigure(keyName1).getProperties();

                for (Map.Entry<String, String> entry : getOriginalDatasourceProperties().entrySet()) {
                    assertEquals(entry.getValue().toString(), pc.getProperty(entry.getKey()).toString());
                }
                log.info("各属性值都是原始值，检查通过");
            } catch (Throwable e) {
                log.error("检查初始值失败", e);
                Assert.fail();
            }

            log.info(String.format("开始修改文件"));
            Map<String, String> map = getModifiedDatasourceProperties();
            map.put("enableDynamicPoolProperties", "true");
            poolPropertiesSwitch.modifyPoolProperties(map);

            log.info(String.format("修改文件后等待35秒"));
            Thread.sleep(35000);

            log.info("开始修改生效检查");
            long startTime = System.currentTimeMillis();
            boolean toBeContinued = true;
            int times = 1;
            while (toBeContinued) {
                try {
                    log.info(String.format("第 %d 次请求开始", times));
                    Properties pc = provider.getDataSourceConfigure(keyName1).getProperties();
                    for (Map.Entry<String, String> entry : getModifiedDatasourceProperties().entrySet())
                        assertEquals(entry.getValue().toString(), pc.getProperty(entry.getKey()).toString());
                    toBeContinued = false;
                    log.info(String.format("第 %d 次请求成功", times));
                    log.info("修改生效");
                } catch (Throwable e) {
                    //斷言失敗則說明切換還未生效，需要繼續等待
                    log.warn(String.format("第 %d 次请求失败，还未收到通知", times));
                    //如果等待時間已經超過40秒（30秒輪詢一次），說明此次切換失敗，結束等待；否則繼續等待
                    if ((System.currentTimeMillis() - startTime) < 40000) {
                        toBeContinued = true;
                        times++;
                        Thread.sleep(1000);
                    } else {
                        log.warn(String.format("等待切换时间超过40秒，本次修改失败"));
                        log.warn(String.format("本次检查失败"));
                        toBeContinued = false;
                        fail();
                    }
                }
            }
        } catch (Exception e) {
            log.error("测试过程中出现异常", e);
            fail();
        }
        log.info("测试通过");
    }
}
