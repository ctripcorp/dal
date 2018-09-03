package switchtest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mybatis.mysql.datasource1.DRTestMapperDao;
import mybatis.mysql.datasource2.DRTestMapperDao2;
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

import static org.junit.Assert.*;

/**
 * Created by lilj on 2018/3/5.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/application-context.xml")
public class MybatisDRTestMultipleKeysTest {
    @Autowired
    private DRTestMapperDao drTestMapperDao;

//    @Autowired
//    private DRTestSQLServerMapperDao drTestSQLServerMapperDao;

    @Autowired
    private DRTestMapperDao2 drTestMapperDao2;

    private String keyName1 = "mysqldaltest01db_W";
    private String keyName2 = "mysqldaltest02db_W";
    private static ConnectionStringSwitch connectionStringSwitch = new ConnectionStringSwitch();
    private static PoolPropertiesSwitch poolPropertiesSwitch = new PoolPropertiesSwitch();
    private static Logger log = LoggerFactory.getLogger(MybatisDRTestMultipleKeysTest.class);
    private static Boolean isPro = true;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
//        poolPropertiesSwitch.resetPoolProperties();
    }

    @Before
    public void setUp() throws Exception {
        connectionStringSwitch.resetConnectionString(isPro);
//        Thread.sleep(5000);

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        connectionStringSwitch.resetConnectionString(isPro);
//        Thread.sleep(5000);
//        poolPropertiesSwitch.resetPoolProperties();
    }

    @Test
    public void testAutoDynamicDatasourceWithSingleKey() throws Exception {
        log.info("before switch");
        String hostname = drTestMapperDao.getHostNameMySQL();
        log.info(String.format("---------------current hostname is: %s ", hostname));
        try {
            log.info("clear data ");
            drTestMapperDao.truncateTable();
            log.info("insert data ");
            drTestMapperDao.addDRTestMybatisPojo();
        } catch (Exception e) {
            log.error("write exception before switch", e.getMessage());
            fail();
        }

        connectionStringSwitch.postByMHA(isPro);

        long startTime = System.currentTimeMillis();
        boolean toBeContinued = true;
        while (toBeContinued) {
            try {
                String currentHostname = drTestMapperDao.getHostNameMySQL();
                log.info(String.format("--------------- hostname after switch is: %s ", currentHostname));
                assertNotEquals(hostname, currentHostname);
                toBeContinued = false;
                log.info("switch is effective");
            } catch (Throwable e) {
                log.error("switch is not effective, please wait...");
                long endTime = System.currentTimeMillis();
                if (endTime - startTime > 40000) {
                    toBeContinued = false;
                    log.error("notify timeout");
                    fail();
                } else {
                    toBeContinued = true;
                }
            }
            Thread.sleep(1000);
        }

        try {
            log.info("clear data ");
            drTestMapperDao.truncateTable();
            log.info("insert data ");
            drTestMapperDao.addDRTestMybatisPojo();
        } catch (Exception e) {
            log.error("write exception, maybe the db is read-only", e.getMessage());
            fail();
        }

        log.info("switch success!");
    }

    @Test
    public void testAutoDynamicDatasourceWithMultipleKeys() throws Exception {
        log.info("before switch");
        String hostname1 = drTestMapperDao.getHostNameMySQL();
        log.info(String.format("---------------current hostname of %s is: %s ", keyName1, hostname1));
        String hostname2 = drTestMapperDao2.getHostNameMySQL2();
        log.info(String.format("---------------current hostname of %s is: %s ", keyName2, hostname2));
        try {
            log.info("clear data ");
            drTestMapperDao.truncateTable();
            drTestMapperDao2.truncateTable2();
            log.info("insert data ");
            drTestMapperDao.addDRTestMybatisPojo();
            drTestMapperDao2.addDRTestMybatisPojo2();
        } catch (Exception e) {
            log.error("write exception before switch", e.getMessage());
            fail();
        }

        connectionStringSwitch.postByMHA(isPro);

        long startTime = System.currentTimeMillis();
        boolean toBeContinued = true;
        while (toBeContinued) {
            try {
                String currentHostname1 = drTestMapperDao.getHostNameMySQL();
                log.info(String.format("--------------- hostname of %s after switch is: %s ", keyName1, currentHostname1));
                assertNotEquals(hostname1, currentHostname1);
                String currentHostname2 = drTestMapperDao2.getHostNameMySQL2();
                log.info(String.format("--------------- hostname of %s after switch is: %s ", keyName2, currentHostname2));
                assertNotEquals(hostname2, currentHostname2);
                toBeContinued = false;
                log.info("switch is effective");
            } catch (Throwable e) {
                log.error("switch is not effective, please wait...");
                long endTime = System.currentTimeMillis();
                if (endTime - startTime > 40000) {
                    toBeContinued = false;
                    log.error("notify timeout");
                    fail();
                } else {
                    toBeContinued = true;
                }
            }
            Thread.sleep(1000);
        }

        try {
            log.info("clear data ");
            drTestMapperDao.truncateTable();
            drTestMapperDao2.truncateTable2();
            log.info("insert data ");
            drTestMapperDao.addDRTestMybatisPojo();
            drTestMapperDao2.addDRTestMybatisPojo2();
        } catch (Exception e) {
            log.error("write exception before switch", e.getMessage());
            fail();
        }

        log.info("switch success!");
    }

    @Test
    public void autoTestSwitchMultipleKeysWithOneFailedAndOnePassed() throws Exception {
        String invalidIp = "10.2.74.1111";
        String validIp;
        String recoveryIp;

        connectionStringSwitch.postByMHA(isPro);
        Thread.sleep(3000);

        //before switch
        log.info(String.format("before switch"));

        String currentHostname1 = drTestMapperDao.getHostNameMySQL();
        log.info(String.format("---------------current hostname of %s is: %s ", keyName1, currentHostname1));
        String currentHostname2 = drTestMapperDao2.getHostNameMySQL2();
        log.info(String.format("---------------current hostname of %s is: %s ", keyName2, currentHostname2));
//        assertEquals(currentHostname, queryHostName(keyName1, new DalHints().inShard(0), autoTestSwitchMultipleKeysWithOneFailedAndOnePassedDao));
//        assertEquals(currentHostname, queryHostName(keyName2, new DalHints().inShard(1), autoTestSwitchMultipleKeysWithOneFailedAndOnePassedDao));


        if (currentHostname1.equals("FAT1868")) {
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

        //等待5秒获取通知重建数据源
        log.info("3 seconds wait...");
        Thread.sleep(3000);

        //检查切换是否成功
        log.info("After switch");

        //mysqldaltest01db_W切换成功
        try {
            String hostname1 = drTestMapperDao.getHostNameMySQL();
            log.info(String.format("---------------current hostname of mysqldaltest01db_W is: %s ", hostname1));
            assertNotEquals(currentHostname1, hostname1);
            log.error(String.format("mysqldaltest01db_W的hostname已经更改，切换生效"));
        } catch (Throwable e) {
            log.error(String.format("mysqldaltest01db_W的hostname并未更改，切换没有生效"));
            fail();
        }

        try {
            //mysqldaltest02db_W切换到非法IP
            String hostname2 = drTestMapperDao2.getHostNameMySQL2();
            assertNotEquals(currentHostname2, hostname2);
//            log.info(String.format("---------------current hostname of mysqldaltest02db_W is: %s ", queryHostName("mysqldaltest02db_W",new DalHints().inShard(1),autoTestSwitchMultipleKeysWithOneFailedAndOnePassedDao)));
            fail("重新创建mysqldaltest02db_W时应该失败，但是并没有，请检查切换异常IP是否成功");
        } catch (Throwable e) {
            log.info(String.format("重新创建mysqldaltest02db_W时失败,切换生效"));
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

            String hostname1 = drTestMapperDao.getHostNameMySQL();
            log.info(String.format("---------------current hostname of %s is: %s ", keyName1, hostname1));
            assertEquals(currentHostname1, hostname1);

            String hostname2 = drTestMapperDao2.getHostNameMySQL2();
            log.info(String.format("---------------current hostname of %s is: %s ", keyName2, hostname2));
            assertEquals(currentHostname2, hostname2);
//                assertEquals(currentHostname, queryHostName(keyName1, new DalHints().inShard(0), autoTestSwitchMultipleKeysWithOneFailedAndOnePassedDao));
//                assertEquals(currentHostname, queryHostName(keyName2, new DalHints().inShard(1), autoTestSwitchMultipleKeysWithOneFailedAndOnePassedDao));

            log.info("恢复连接串成功");
        } catch (Exception ex) {
            log.error("恢复切换失败，请检查", ex);
            fail();
        }
    }
   /* @Test
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

    }

    @Test
    public void testDynamicDatasourceWithMultipleKeys() throws Exception {
        int i = 0;
        while (1 == 1) {
            try {
                log.info(String.format("Test %d started", i));
                log.info(String.format("---------------current hostname of mysqldaltest01db_W is: %s ", drTestMapperDao.getHostNameMySQL()));
                log.info(String.format("---------------current hostname of mysqldaltest02db_W is: %s ", drTestMapperDao2.getHostNameMySQL2()));
                log.info("clear data of db1");
                drTestMapperDao.truncateTable();
                log.info("clear data of db2");
                drTestMapperDao2.truncateTable2();
                log.info("insert data into db1 ");
                drTestMapperDao.addDRTestMybatisPojo();
                log.info("insert data into db2 ");
                drTestMapperDao2.addDRTestMybatisPojo2();
                log.info("update data of db1");
                drTestMapperDao.updateDRTestMybatisPojo();
                log.info("update data db2");
                drTestMapperDao2.updateDRTestMybatisPojo2();
                log.info("query data of db1");
                drTestMapperDao.getDRTestMybatisPojo();
                log.info("query data of db2");
                drTestMapperDao2.getDRTestMybatisPojo2();
                log.info(String.format("Test %d passed", i));
            } catch (Exception e) {
                log.error(String.format("Test %d failed", i), e);
            }
            i++;
            Thread.sleep(1000);
        }

    }*/

    /*@Test
    public void testSQLServerDynamicDatasourceWithSingleKey() throws Exception {
        int i=0;
        while(1==1){
            try{
                log.info(String.format("Test %d started",i));
                log.info(String.format("---------------current hostname is: %s ",drTestSQLServerMapperDao.getHostNameSQLServer()));
                log.info("clear data ");
                drTestSQLServerMapperDao.truncateTableSQLServer();
                log.info("insert data ");
                drTestSQLServerMapperDao.addDRTestMybatisSQLServerPojo();
                log.info("update data ");
                drTestSQLServerMapperDao.updateDRTestMybatisSQLServerPojo();
                log.info("query data ");
                drTestSQLServerMapperDao.getDRTestMybatisSQLServerPojo();
                log.info(String.format("Test %d passed",i));
            }
            catch (Exception e){
                log.error(String.format("Test %d failed",i),e);
            }
            i++;
            Thread.sleep(1000);
        }
    }

    @Test
    public void testDynamicDatasourceOnMySqlAndSQLServer() throws Exception {
        int i=0;
        while(1==1){
            try{
                log.info(String.format("Test %d started",i));
                log.info(String.format("---------------current hostname of MySql is: %s ",drTestMapperDao.getHostNameMySQL()));
                log.info(String.format("---------------current hostname of SQLServer is: %s ",drTestSQLServerMapperDao.getHostNameSQLServer()));
                log.info("clear data of db1");
                drTestMapperDao.truncateTable();
                log.info("clear data of db2");
                drTestSQLServerMapperDao.truncateTableSQLServer();
                log.info("insert data into db1 ");
                drTestMapperDao.addDRTestMybatisPojo();
                log.info("insert data into db2 ");
                drTestSQLServerMapperDao.addDRTestMybatisSQLServerPojo();
                log.info("update data of db1");
                drTestMapperDao.updateDRTestMybatisPojo();
                log.info("update data db2");
                drTestSQLServerMapperDao.updateDRTestMybatisSQLServerPojo();
                log.info("query data of db1");
                drTestMapperDao.getDRTestMybatisPojo();
                log.info("query data of db2");
                drTestSQLServerMapperDao.getDRTestMybatisSQLServerPojo();
                log.info(String.format("Test %d passed",i));
            }
            catch (Exception e){
                log.error(String.format("Test %d failed",i),e);
            }
            i++;
            Thread.sleep(1000);
        }

    }*/
}
