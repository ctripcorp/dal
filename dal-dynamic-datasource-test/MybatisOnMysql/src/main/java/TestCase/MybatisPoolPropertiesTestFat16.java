package TestCase;

import DRTestMybatisMySQL.DRTestMapperDao;
import DRTestMybatisMySQL2.DRTestMapperDao2;
import com.ctrip.framework.dal.datasourceswitch.ConnectionStringSwitch;
import com.ctrip.framework.dal.datasourceswitch.PoolPropertiesSwitch;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by lilj on 2018/3/5.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/application-context.xml")
public class MybatisPoolPropertiesTestFat16 {
    @Autowired
    private DRTestMapperDao drTestMapperDao;

    @Autowired
    private DRTestMapperDao2 drTestMapperDao2;

    private String keyName1 = "mysqldaltest01db_W";
    private String keyName2 = "mysqldaltest02db_W";
    private static Logger log = LoggerFactory.getLogger(MybatisPoolPropertiesTestFat16.class);
    private static ConnectionStringSwitch connectionStringSwitch = new ConnectionStringSwitch();
    private static PoolPropertiesSwitch poolPropertiesSwitch = new PoolPropertiesSwitch();
    private static Boolean isPro = false;

    @Before
    public void setUp() throws Exception {
        connectionStringSwitch.resetConnectionString(isPro);
        Thread.sleep(5000);
        poolPropertiesSwitch.resetPoolProperties();
    }

    @After
    public void tearDown() throws Exception {

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        connectionStringSwitch.resetConnectionString(isPro);
        Thread.sleep(5000);
        poolPropertiesSwitch.resetPoolProperties();
    }

    /*@Test
    public void testDynamicDatasourceWithSingleKey() throws Exception {
        int i = 0;
        while (1 == 1) {
            try {
                log.info(String.format("Test %d started", i));
                log.info(String.format("---------------current hostname is: %s ", drTestMapperDao.getHostNameMySQL()));
                log.info("clear data ");
                drTestMapperDao.truncateTable();
                log.info("insert data ");
                drTestMapperDao.addDRTestMybatisPojo();
                log.info("update data ");
                drTestMapperDao.updateDRTestMybatisPojo();
                log.info("query data ");
                drTestMapperDao.getDRTestMybatisPojo();
                log.info(String.format("Test %d passed", i));
            } catch (Exception e) {
                log.error(String.format("Test %d failed", i), e);
            }
            i++;
            Thread.sleep(1000);
        }

    }*/

    public String queryHostName(String keyName, DRTestMapperDao dao) throws Exception {
        String hostName = dao.getHostNameMySQL();
        log.info(String.format("---------------current hostname of %s is: %s ", keyName, hostName));
        return hostName;
    }


    public String checkQueryResultWithSingleKey(DRTestMapperDao dao, String errorMsg) {
        String hostname = null;
        log.info("check if query works well or not");
        try {
            log.info("select hostname");
            hostname = dao.getHostNameMySQL();
            log.info("query works, print result : " + hostname);
            return hostname;
        } catch (Exception e) {
            log.error(errorMsg, e);
            fail();
        }
        return hostname;
    }

    public void checkQueryExceptionWithSingleKey(DRTestMapperDao dao, String errorMsg) {
//        String hostname = null;
        log.info("check if query works well or not");
        try {
            log.info("select hostname");
            dao.getHostNameMySQL();
            log.error(errorMsg);
//            log.info("query works, print result : " + hostname);
//            return hostname;
//            fail();
        } catch (Exception e) {
            log.info("query not work,check passed", e);
        }
//        return hostname;
    }


    public void checkAfterDatasourceSwitch(DRTestMapperDao dao, String errorMsg) throws Exception {
        log.info("开始修改生效检查");
        long startTime = System.currentTimeMillis();
        boolean toBeContinued = true;
        int times = 1;
        while (toBeContinued) {
            try {
                log.info(String.format("第 %d 次请求开始", times));
                dao.getHostNameMySQL();
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
                    log.warn(String.format(errorMsg + "本次检查失败"));
                    toBeContinued = false;
                    fail();
                }
            }
        }
    }


    public void checkAfterInvalidDatasourceSwitch(DRTestMapperDao dao, String errorMsg) {
        log.info("开始修改生效检查");
        long startTime = System.currentTimeMillis();
        boolean toBeContinued = true;
        int times = 1;
        while (toBeContinued) {
            try {
                log.info(String.format("第 %d 次请求开始", times));

                dao.getHostNameMySQL();

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
                log.warn(String.format("第 %d 次请求报异常，修改生效", times));
                toBeContinued = false;
                log.info(errorMsg + "本次检查通过");
            }
        }
    }

    public void checkAfterMHASwitch(DRTestMapperDao dao, String hostname) {
        //检查切换是否生效
        log.info("开始连接串切换生效检查");
        long startTime = System.currentTimeMillis();
        boolean toBeContinued = true;
        int times = 1;
        while (toBeContinued) {
            try {
                log.info(String.format("第 %d 次请求开始 ", times));
                //如果斷言成立，即hostname已經改變，則說明切換生效
                assertNotEquals(hostname, queryHostName(keyName1, dao));
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
    public void modifyDatasourcePropertiesWithSingleKeyAndFail() throws Exception {
        log.info(String.format("修改datasource之前，数据库操作正常"));
//        checkBatchInsertReturnListWithSingleKey(drTestMapperDao,-2,"修改datasource之前，batchInser返回值异常",null);
        String hostname = checkQueryResultWithSingleKey(drTestMapperDao, "修改datasource之前，数据库操作不正常");

        log.info(String.format("开始修改文件"));
        Map<String, String> map = new HashMap<>();
        map.put(keyName1 + ".maxAge", "哦");
        poolPropertiesSwitch.modifyPoolProperties(map);

        log.info("修改文件后等待35秒钟");
        Thread.sleep(35000);

        log.info("由于开关未开启，错误修改并未生效");
//        checkBatchInsertReturnListWithSingleKey(dao,-2,"开关未开启，batchInser操作出现异常",null);
        checkQueryResultWithSingleKey(drTestMapperDao, "开关未开启，数据库操作出现异常");

        log.info("切换连接串");
        connectionStringSwitch.postByMHA(isPro);

        checkAfterMHASwitch(drTestMapperDao, hostname);
//        checkAfterInvalidDatasourceSwitch(drTestMapperDao,"开关未开启，MHA切换不该导致属性修改生效");
        checkQueryResultWithSingleKey(drTestMapperDao, "开关未开启，MHA切换不该导致属性修改生效");

        log.info(String.format("修改enableDynamicPoolProperties为true"));
        map.put("enableDynamicPoolProperties", "true");
        poolPropertiesSwitch.modifyPoolProperties(map);

        checkAfterInvalidDatasourceSwitch(drTestMapperDao, "enableDynamicPoolProperties为true,数据库操作应该出现异常且已经出现异常");

        log.info("恢复文件");
        map.put(keyName1 + ".maxAge", "28000000");
        poolPropertiesSwitch.modifyPoolProperties(map);

        log.info("恢复datasource之后，数据库操作恢复正常");
        checkAfterDatasourceSwitch(drTestMapperDao, "恢复文件后，数据库操作应该恢复正常但是并没有");

        log.info("测试通过");
    }


    @Test
    public void modifyDatasourcePropertiesAndConnectionStringWithMultipleKeys() throws Exception {
//        DRTestDao dao1=new DRTestDao("shardTestOnMysql");
        log.info(String.format("修改datasource之前，对数据库做查询操作"));
        String currentHostname1 = drTestMapperDao.getHostNameMySQL();
        String currentHostname2 = drTestMapperDao2.getHostNameMySQL2();
        assertEquals("修改之前，两个库hostname不一致", currentHostname1, currentHostname2);

//        checkBatchInsertReturnListWithSingleKey(dao1,-2, "修改前，shard0返回值异常",new DalHints().inShard(0));
//        checkBatchInsertReturnListWithSingleKey(dao1,-2, "修改前，shard1返回值异常",new DalHints().inShard(1));
//        String currentHostname1 = queryHostName(keyName1, new DalHints().inShard(0), dao1);
//        String currentHostname1 = queryHostName(keyName2, new DalHints().inShard(1), dao1);
//        assertEquals("两个key所在hostname不一致",currentHostname1,currentHostname2);

        log.info(String.format("开始修改文件"));

        //修改mysqldaltest01db_W.connectionProperties = rewriteBatchedStatements=false;
        Map<String, String> map = new HashMap<>();
        map.put(keyName1 + ".maxAge", "哦");
        poolPropertiesSwitch.modifyPoolProperties(map);

        log.info("修改文件后等待35秒钟");
        Thread.sleep(35000);

        log.info("由于enableDynamicPoolProperties未设置，修改datasource之后并未动态生效，数据库操作仍然正常");
//        checkBatchInsertReturnListWithSingleKey(dao1,-2, "enableDynamicPoolProperties未设置，shard0返回值异常",new DalHints().inShard(0));
//        checkBatchInsertReturnListWithSingleKey(dao1,-2, "enableDynamicPoolProperties未设置, shard1返回值异常",new DalHints().inShard(1));
        checkQueryResultWithSingleKey(drTestMapperDao, "开关未开启，但是异常修改已生效");
        assertEquals(keyName2 + " 的hostname和初始值不一致", currentHostname2, drTestMapperDao2.getHostNameMySQL2());

        log.info(String.format("切换连接串"));
        connectionStringSwitch.postByMHA(isPro);
//      检查连接串切换是否生效
        checkAfterMHASwitch(drTestMapperDao, currentHostname1);
        Thread.sleep(2000);
        assertNotEquals(keyName2 + " hostname切换失败", currentHostname2, drTestMapperDao2.getHostNameMySQL2());

        log.info("由于enableDynamicPoolProperties未设置，修改datasource之后并未动态生效，此时切换连接串也不该使得DataSource生效，数据库操作仍然正常");
        checkQueryResultWithSingleKey(drTestMapperDao, "开关未开启，但是MHA切换使得异常修改生效");
        assertNotEquals(keyName2 + " 的hostname和初始值不一致", currentHostname2, drTestMapperDao2.getHostNameMySQL2());
//        checkBatchInsertReturnListWithSingleKey(dao1,-2, "enableDynamicPoolProperties未设置,切换连接串后使得DataSource生效",new DalHints().inShard(0));
//        checkBatchInsertReturnListWithSingleKey(dao1,-2, "enableDynamicPoolProperties未设置,切换连接串后使得DataSource生效",new DalHints().inShard(1));
        /*String currentHostname3 = queryHostName("mysqldaltest01db_W", null, dao);
        checkHostnameWithSingleKey(currentHostname1, currentHostname3, false, "enableDynamicPoolProperties未设置，修改datasource之后并未动态生效,但是切换连接串后hostname没变");*/

//        log.info("测试通过！");

       /* log.info(String.format("修改enableDynamicPoolProperties为False"));
        map.put("enableDynamicPoolProperties", "False");
        modifyDatasourceProperties(map);

        log.info("修改文件后等待10秒钟");
        Thread.sleep(10000);

        log.info("由于enableDynamicPoolProperties设置为False，修改datasource之后并未动态生效，batchInsert返回值仍然是-2");
//        checkAfterDatasourceSwitch(dao,-2, "enableDynamicPoolProperties=false,返回值异常",null);
        checkBatchInsertReturnListWithSingleKey(dao1,-2, "enableDynamicPoolProperties设置为False，shard0返回值异常",new DalHints().inShard(0));
        checkBatchInsertReturnListWithSingleKey(dao1,-2, "enableDynamicPoolProperties设置为False，shard1返回值异常",new DalHints().inShard(1));

        log.info(String.format("修改enableDynamicPoolProperties为f"));
        map.put("enableDynamicPoolProperties", "f");
        modifyDatasourceProperties(map);

        log.info("修改文件后等待10秒钟");
        Thread.sleep(10000);

        log.info("由于enableDynamicPoolProperties设置为f，修改datasource之后并未动态生效，batchInsert返回值仍然是-2");
//        checkAfterDatasourceSwitch(dao,-2, "enableDynamicPoolProperties=F,返回值异常",null);
        checkBatchInsertReturnListWithSingleKey(dao1,-2, "enableDynamicPoolProperties设置为f，shard0返回值异常",new DalHints().inShard(0));
        checkBatchInsertReturnListWithSingleKey(dao1,-2, "enableDynamicPoolProperties设置为f，shard1返回值异常",new DalHints().inShard(1));
*/
        log.info(String.format("修改enableDynamicPoolProperties为true"));
        map.put("enableDynamicPoolProperties", "true");
        poolPropertiesSwitch.modifyPoolProperties(map);

//        log.info("修改文件后等待30秒钟");
//        Thread.sleep(30000);

        log.info(String.format("由于enableDynamicPoolProperties设置为true，修改datasource之后动态生效, %s 查询报错，%s 操作正常", keyName1, keyName2));
//        checkAfterDatasourceSwitch(dao1,1, "enableDynamicPoolProperties=true,返回值异常",new DalHints().inShard(0));
//        checkBatchInsertReturnListWithSingleKey(dao1,-2, "enableDynamicPoolProperties=true,返回值异常",new DalHints().inShard(1));
        checkAfterInvalidDatasourceSwitch(drTestMapperDao, "开关已开启，" + keyName1 + "查询操作应该报错且已经报错");
        assertNotEquals(keyName2 + "的hostname异常", currentHostname2, drTestMapperDao2.getHostNameMySQL2());


        log.info("恢复数据源");
        map.put(keyName1 + ".maxAge", "28000000");
        poolPropertiesSwitch.modifyPoolProperties(map);

        log.info("恢复datasource之后，数据库操作恢复正常");
        checkAfterDatasourceSwitch(drTestMapperDao, "恢复文件后，数据库操作应该恢复正常但是并没有");
        assertNotEquals(keyName2 + "的hostname异常", currentHostname2, drTestMapperDao2.getHostNameMySQL2());

        log.info("测试通过！");

    }
}
