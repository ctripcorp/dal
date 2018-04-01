package DRTestOnMysql;

import com.ctrip.framework.dal.datasourceswitch.ConnectionStringSwitch;
import com.ctrip.framework.dal.datasourceswitch.PoolPropertiesSwitch;
import com.ctrip.framework.dal.datasourceswitch.netstat.NetStat;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.datasource.DataSourceTerminator;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * Created by lilj on 2018/3/4.
 */
public class ConnectionTestFat16 {
    private static DRTestDao dao = null;
    private static ConnectionStringSwitch connectionStringSwitch = null;
    private static PoolPropertiesSwitch poolPropertiesSwitch = null;
    private static NetStat netStat = null;
    private static Logger log = LoggerFactory.getLogger(ConnectionTestFat16.class);
    private static String hostname;
    private static boolean isPro=false;

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
        connectionStringSwitch = new ConnectionStringSwitch();
        poolPropertiesSwitch = new PoolPropertiesSwitch();
        netStat = new NetStat();
//         pojo = new DRTestPojo();
//        pojo.setName("test");
//        dao.dropTable();
//        dao.createTable();
        poolPropertiesSwitch.resetPoolProperties();
    }

    @Before
    public void setUp() throws Exception {
        connectionStringSwitch.resetConnectionString(isPro);
        Thread.sleep(5000);
//        poolPropertiesSwitch.resetPoolProperties();
        hostname = dao.selectHostname(null);
        log.info(String.format("current hostname is : %s", hostname));
    }

    @After
    public void tearDown() throws Exception {
//        connectionStringSwitch.resetConnectionString();
//        poolPropertiesSwitch.resetPoolProperties();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        connectionStringSwitch.resetConnectionString(isPro);
        Thread.sleep(5000);
    }

    @Test
    public void testLeakConnectionClosedByServer() throws Exception {
        DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureLocator.getInstance();
        DataSourceConfigure dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure("mysqldaltest01db_W");
        final SingleDataSource singleDataSource = new SingleDataSource("mysqldaltest01db_W", dataSourceConfigure);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    connectionStringSwitch.postByMHA(true);
                    Thread.sleep(5000);
                    hostname = dao.selectHostname(null);
                    log.info(String.format("hostname after switch: %s", hostname));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Connection connection = null;
        try {
            log.info("30s query start.");
            connection = singleDataSource.getDataSource().getConnection();
            connection.createStatement().executeQuery("select name from testTable where sleep(30) = 0 limit 1");
            log.info("30s query should be interrupted but not");
            fail();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertNotEquals(-1, e.getCause().toString().indexOf("EOF"));
            log.info("query interrupted");
        }
// 验证连接已断开
        Thread.sleep(10000);
        netStat.netstatCMD(hostname, true);
//验证由于连接泄露，所以并未关闭
        assertFalse(connection.isClosed());
//验证65秒后pool cleaner关闭了泄露的连接
        Thread.sleep(70000);
        assertTrue(connection.isClosed());
    }

    @Test
    public void testNormalConnectionClosedByServer() throws Exception {
        DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureLocator.getInstance();
        DataSourceConfigure dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure("mysqldaltest01db_W");
        final SingleDataSource singleDataSource = new SingleDataSource("mysqldaltest01db_W", dataSourceConfigure);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    connectionStringSwitch.postByMHA(isPro);
                    Thread.sleep(5000);
                    hostname = dao.selectHostname(null);
                    log.info(String.format("hostname after switch: %s", hostname));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Connection connection = null;
        try {
            log.info("30s query start.");
            connection = singleDataSource.getDataSource().getConnection();
            connection.createStatement().executeQuery("select name from testTable where sleep(30) = 0 limit 1");
            log.info("30s query should be interrupted but not");
            fail();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertNotEquals(-1, e.getCause().toString().indexOf("EOF"));
            log.info("query interrupted");
        } finally {
            connection.close();
        }
// 验证连接已断开
        Thread.sleep(10000);
        netStat.netstatCMD(hostname, true);
//验证由于连接未泄露，所以已关闭
        assertTrue(connection.isClosed());
    }

    @Test
    public void testLongQueryByDALAndConnectionClosedByServer() throws Exception {
        dao.test_def_update(null);
        DRTestPojo drTestPojo = new DRTestPojo();
        drTestPojo.setName("testLongQuery");
        dao.insert(null, drTestPojo);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    connectionStringSwitch.postByMHA(isPro);
                    Thread.sleep(5000);
                    hostname = dao.selectHostname(null);
                    log.info(String.format("hostname after switch: %s", hostname));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        try {
            log.info("30s query start.");
            dao.testLongQuery(30, null);
            log.info("30s query should be interrupted but not");
            fail();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertNotEquals(-1, e.getCause().toString().indexOf("EOF"));
            log.info("query interrupted,case passed");
        }

        // 验证连接已断开
        Thread.sleep(10000);
        netStat.netstatCMD(hostname, true);
    }

    @Test
    public void testLongQueryAndConnectionClosedByClient() throws Exception {
        DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureLocator.getInstance();
        DataSourceConfigure dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure("mysqldaltest01db_W");
        final SingleDataSource singleDataSource = new SingleDataSource("mysqldaltest01db_W", dataSourceConfigure);

        final AtomicBoolean query_30s_success = new AtomicBoolean();
        final AtomicBoolean query_30s_leak_success = new AtomicBoolean();
        final AtomicBoolean query_90s_success = new AtomicBoolean();
        final AtomicBoolean query_90s_leak_success = new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(5);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = null;
                try {
                    log.info("30s query with normal connection start.");
                    connection = singleDataSource.getDataSource().getConnection();
                    connection.createStatement().execute("select name from testTable where sleep(30) = 0 limit 1");
                    log.info(String.format("30s query with normal connection end."));
                } catch (Exception e) {
                    e.printStackTrace();
                    log.warn(String.format("30s query with normal connection test failed because of interruption."));
                    query_30s_success.set(false);
                } finally {
                    try {
                        connection.close();
                        assertTrue("30s正常连接未被关闭", connection.isClosed());
                        log.info("30s normal connection closed, case passed.");
                        query_30s_success.set(true);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        query_30s_success.set(false);
                    } finally {
                        latch.countDown();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = null;
                try {
                    log.info("30s query with leak connection start.");
                    connection = singleDataSource.getDataSource().getConnection();
                    connection.createStatement().execute("select name from testTable where sleep(30) = 0 limit 1");
                    log.info("30s query with leak connection end.");
                } catch (Exception e) {
                    e.printStackTrace();
                    log.warn("30s query with leak connection test failed because of interruption.");
                    query_30s_leak_success.set(false);
                } finally {
                    try {
                        assertFalse("泄露的连接被关闭", connection.isClosed());
                    } catch (Throwable e) {
                        e.printStackTrace();
                        query_30s_leak_success.set(false);
                    } finally {
                        try {
                            Thread.sleep(70000);
                            assertTrue("泄露的连接未被强制关闭", connection.isClosed());
                            log.info("30s leak connection force closed, case passed.");
                            query_30s_leak_success.set(true);
                        } catch (Throwable e) {
                            e.printStackTrace();
                            query_30s_leak_success.set(false);
                        }
                    }
                    latch.countDown();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = null;
                try {
                    log.info("90s query with normal connection start...");
                    connection = singleDataSource.getDataSource().getConnection();
                    connection.createStatement().execute("select name from testTable where sleep(90) = 0 limit 1");
                    log.info("90s query with normal connection end.");
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("90s query with normal connection test failed because of interruption");
                    query_90s_success.set(false);
                } finally {
                    try {
                        connection.close();
                        assertTrue("90s正常连接未被关闭", connection.isClosed());
                        log.info("90s normal connection closed,case passed.");
                        query_90s_success.set(true);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        query_90s_success.set(false);
                    }
                    latch.countDown();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = null;
                try {
                    log.info("90s query with leak connection start.");
                    connection = singleDataSource.getDataSource().getConnection();
                    connection.createStatement().execute("select name from testTable where sleep(90) = 0 limit 1");
                    log.info("90s query with leak connection passed.");
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("90s query with leak connection test failed because of interruption");
                    query_90s_leak_success.set(false);
                } finally {
                    try {
                        assertTrue("90s泄露连接未被强制关闭", connection.isClosed());
                        log.info("90s leak connection force closed, case passed");
                        query_90s_leak_success.set(true);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        query_90s_leak_success.set(false);
                    }
                    latch.countDown();
                }
            }
        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("close datasource after 10s");
                    Thread.sleep(10000);

                    log.info("close datasource start.");
                    DataSourceTerminator.getInstance().close(singleDataSource);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    log.info("close datasource done.");
                    latch.countDown();
                }
            }
        }).start();

        latch.await();

        assertTrue("30s query test with normal connection  failed", query_30s_success.get());
        assertTrue("90s query test with normal connection  failed", query_90s_success.get());
        assertTrue("30s query test with leak connection failed", query_30s_leak_success.get());
        assertTrue("90s query test with leak connection failed", query_90s_leak_success.get());

        netStat.netstatCMD(hostname, false);
        log.info("done");
    }
}
