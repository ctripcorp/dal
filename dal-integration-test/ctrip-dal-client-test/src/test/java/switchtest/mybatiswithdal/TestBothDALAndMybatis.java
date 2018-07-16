package switchtest.mybatiswithdal;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;


import mybatis.mysql.DRTestDao;
import mybatis.mysql.DRTestMapperDao;
import mybatis.mysql.DRTestPojo;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import testUtil.ConnectionStringSwitch;
import testUtil.PoolPropertiesSwitch;
import testUtil.netstat.NetStat;

import static org.junit.Assert.*;

/**
 * Created by lilj on 2018/3/6.
 */
public class TestBothDALAndMybatis {
    private static Logger log = LoggerFactory.getLogger(TestBothDALAndMybatis.class);
    private static ConnectionStringSwitch connectionStringSwitch = new ConnectionStringSwitch();
    private static PoolPropertiesSwitch poolPropertiesSwitch=new PoolPropertiesSwitch();
    private static NetStat netStat = new NetStat();
    private static Boolean isPro=true;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.shutdownFactory();
        DalClientFactory.initClientFactory(ClassLoader.getSystemClassLoader().getResource(".").getPath()+"DalConfigForSwitch/Dal.config");
        poolPropertiesSwitch.resetPoolProperties();
    }

    @Before
    public void setUp() throws Exception {
        connectionStringSwitch.resetConnectionString(isPro);
        Thread.sleep(5000);
//        poolPropertiesSwitch.resetPoolProperties();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception{
        connectionStringSwitch.resetConnectionString(isPro);
        Thread.sleep(5000);
//        poolPropertiesSwitch.resetPoolProperties();
    }

    @Test
    public void confirmNoNewConnectionsToOldMasterWithSameKey() throws Exception {
        DalClientFactory.initClientFactory();
        DalClientFactory.warmUpConnections();

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context-singlekey.xml");
        DRTestDao drTestDao = applicationContext.getBean(DRTestDao.class);
        DRTestMapperDao drTestMapperDao = applicationContext.getBean(DRTestMapperDao.class);

        DalDataSourceFactory factory = (DalDataSourceFactory) applicationContext.getBean("dalDataSourceFactory");
        for (int i = 0; i < 5; i++) {
            factory.createDataSource("mysqldaltest01db_W");
        }

        String currentHostname=drTestDao.selectHostname(null);
        log.info(String.format("****************query hostname before switch: %s", currentHostname));
        drTestMapperDao.addDRTestMybatisPojo();

        netStat.netstatCMD(currentHostname,true);
        Thread.sleep(2000);
        log.info("switch starts");
        connectionStringSwitch.postByMHA(isPro);
        log.info(String.format("****************sleep 5s after switch..."));
        Thread.sleep(5000);

        String hostname=drTestMapperDao.getHostNameMySQL();
        assertNotEquals(currentHostname,hostname);
        drTestDao.test_def_update(null);

        currentHostname=hostname;

        netStat.netstatCMD(currentHostname,true);
        log.info(String.format("Done"));
    }

    @Test
    public void confirmNoNewConnectionsToOldMasterWithDifferentKey() throws Exception {
        DalClientFactory.initClientFactory();
        DalClientFactory.warmUpConnections();

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context-differentKey.xml");
        DRTestDao drTestDao = applicationContext.getBean(DRTestDao.class);
        DRTestMapperDao drTestMapperDao = applicationContext.getBean(DRTestMapperDao.class);

        DalDataSourceFactory factory = (DalDataSourceFactory) applicationContext.getBean("dalDataSourceFactory");
        for (int i = 0; i < 5; i++) {
            factory.createDataSource("mysqldaltest02db_W");
        }

        String originalHostnameInDAL=drTestDao.selectHostname(null);
        log.info(String.format("****************query hostname in dal before switch: %s", originalHostnameInDAL));
        String originalHostnameInMyBatis=drTestMapperDao.getHostNameMySQL();
        log.info(String.format("****************query hostname in mybatis before switch: %s", originalHostnameInMyBatis));
        assertEquals(originalHostnameInDAL,originalHostnameInMyBatis);

        drTestMapperDao.addDRTestMybatisPojo();
        drTestDao.test_def_update(null);

        netStat.netstatCMD(originalHostnameInDAL,true);
        Thread.sleep(2000);
        log.info("switch starts");
        connectionStringSwitch.postByMHA(isPro);
        log.info(String.format("****************sleep 5s after switch..."));
        Thread.sleep(5000);

        String currentHostnameInDAL=drTestDao.selectHostname(null);
        log.info(String.format("****************query hostname in dal before switch: %s", currentHostnameInDAL));
        String currentHostnameInMyBatis=drTestMapperDao.getHostNameMySQL();
        log.info(String.format("****************query hostname in mybatis before switch: %s", currentHostnameInMyBatis));
        assertEquals(currentHostnameInDAL,currentHostnameInMyBatis);
        assertNotEquals(originalHostnameInDAL,currentHostnameInDAL);
        assertNotEquals(originalHostnameInMyBatis,currentHostnameInMyBatis);

        drTestMapperDao.addDRTestMybatisPojo();
        drTestDao.test_def_update(null);


        netStat.netstatCMD(currentHostnameInMyBatis,true);
        log.info(String.format("Done"));
    }

   /* @Test
    public void test() throws Exception {
        DalClientFactory.initClientFactory();
        DalClientFactory.warmUpConnections();

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context-differentKey.xml");
        DRTestDao drTestDao = applicationContext.getBean(DRTestDao.class);
        DRTestMapperDao drTestMapperDao = applicationContext.getBean(DRTestMapperDao.class);

        DalDataSourceFactory factory = (DalDataSourceFactory) applicationContext.getBean("dalDataSourceFactory");
        for (int i = 0; i < 5; i++) {
            factory.createDataSource("mysqldaltest02db_W");
        }

        while (1 == 1) {
            try {
                String currentHostnameByMybatis = drTestMapperDao.getHostNameMySQL();
                log.info(String.format("hostname in mybatis：%s", currentHostnameByMybatis));
                String currentHostnameByDAL = drTestDao.selectHostname(null);
                log.info(String.format("hostname in dal：%s", currentHostnameByDAL));
                log.info("success");
            } catch (Throwable e) {
                log.error("fail");
            }
            Thread.sleep(1000);
        }
    }*/



    @Test
    public void testDALAndMybatisWithSameKey() throws Exception {
        DalClientFactory.initClientFactory();
        DalClientFactory.warmUpConnections();

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context-singlekey.xml");
        DRTestDao drTestDao = applicationContext.getBean(DRTestDao.class);
        DRTestMapperDao drTestMapperDao = applicationContext.getBean(DRTestMapperDao.class);

        DalDataSourceFactory factory = (DalDataSourceFactory) applicationContext.getBean("dalDataSourceFactory");
        for (int i = 0; i < 5; i++) {
            factory.createDataSource("mysqldaltest01db_W");
        }


        for (int i = 0; i < 10; i++) {
            try {
                String hostnameMybatis = drTestMapperDao.getHostNameMySQL();
                log.info(String.format("切换前hostname in mybatis：%s", hostnameMybatis));
                log.info(String.format("切换前database in mybatis：%s", drTestMapperDao.getDatabaseMySQL()));
                String hostnameDal = drTestDao.selectHostname(null);
                log.info(String.format("切换前hostname in dal：%s", hostnameDal));
                log.info(String.format("切换前database in dal：%s", drTestDao.selectDatabase(null)));
                log.info("clear data in mybatis ");
                drTestMapperDao.truncateTable();
                log.info("insert data in dal ");
                drTestMapperDao.addDRTestMybatisPojo();
                log.info("clear data in dal ", drTestDao.test_def_update(new DalHints()));
                DRTestPojo daoPojo = new DRTestPojo();
                daoPojo.setName("testDal");
                log.info("insert data ", drTestDao.insert(null, daoPojo));

                log.info(String.format("开始切换"));
                //调用切换接口
                connectionStringSwitch.postByMHA(isPro);
                //等待5秒
                Thread.sleep(10000);

                String currentHostnameMybatis = drTestMapperDao.getHostNameMySQL();
                log.info(String.format("切换后hostname in mybatis：%s", drTestMapperDao.getHostNameMySQL()));
                log.info(String.format("切换后database in mybatis：%s", drTestMapperDao.getDatabaseMySQL()));
                Assert.assertNotEquals(hostnameMybatis, currentHostnameMybatis);

                String currentHostnameDal = drTestDao.selectHostname(null);
                log.info(String.format("切换后hostname in dal：%s", currentHostnameDal));
                log.info(String.format("切换后database in dal：%s", drTestDao.selectDatabase(null)));
                Assert.assertNotEquals(hostnameDal, currentHostnameDal);


                drTestMapperDao.truncateTable();
                drTestMapperDao.addDRTestMybatisPojo();
                drTestDao.test_def_update(new DalHints());
                drTestDao.insert(null, daoPojo);

                log.info("success!!!");
            } catch (Exception e) {
                log.warn("failed", e.getMessage());
                e.printStackTrace();
                fail();
            }
            Thread.sleep(1000);
        }
    }



    @Test
    public void testDALAndMybatisWithDifferentKey() throws Exception {
        DalClientFactory.initClientFactory();
        DalClientFactory.warmUpConnections();

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context-differentKey.xml");
        DRTestDao drTestDao = applicationContext.getBean(DRTestDao.class);
        DRTestMapperDao drTestMapperDao = applicationContext.getBean(DRTestMapperDao.class);

        DalDataSourceFactory factory = (DalDataSourceFactory) applicationContext.getBean("dalDataSourceFactory");
        for (int i = 0; i < 5; i++) {
            factory.createDataSource("mysqldaltest02db_W");
        }


        for (int i = 0; i < 10; i++) {
            try {
                String hostnameMybatis = drTestMapperDao.getHostNameMySQL();
                log.info(String.format("切换前hostname in mybatis：%s", hostnameMybatis));
                log.info(String.format("切换前database in mybatis：%s", drTestMapperDao.getDatabaseMySQL()));

                String hostnameDal = drTestMapperDao.getHostNameMySQL();
                log.info(String.format("切换前hostname in dal：%s", hostnameDal));
                log.info(String.format("切换前database in dal：%s", drTestDao.selectDatabase(null)));

                log.info("clear data in mybatis ");
                drTestMapperDao.truncateTable();
                log.info("insert data in dal ");
                drTestMapperDao.addDRTestMybatisPojo();
                log.info("clear data in dal ", drTestDao.test_def_update(new DalHints()));
                DRTestPojo daoPojo = new DRTestPojo();
                daoPojo.setName("testDal");
                log.info("insert data ", drTestDao.insert(null, daoPojo));

                log.info(String.format("开始切换"));
                //调用切换接口
                connectionStringSwitch.postByMHA(isPro);
                //等待5秒
                Thread.sleep(5000);

                String currentHostnameMybatis = drTestMapperDao.getDatabaseMySQL();
                log.info(String.format("切换后hostname in mybatis：%s", currentHostnameMybatis));
                log.info(String.format("切换后database in mybatis：%s", drTestMapperDao.getDatabaseMySQL()));
                Assert.assertNotEquals(hostnameMybatis, currentHostnameMybatis);

                String currentHostnameDal = drTestDao.selectHostname(null);
                log.info(String.format("切换后hostname in dal：%s", currentHostnameDal));
                log.info(String.format("切换后database in dal：%s", drTestDao.selectDatabase(null)));
                Assert.assertNotEquals(hostnameDal, currentHostnameDal);

                drTestMapperDao.truncateTable();
                drTestMapperDao.addDRTestMybatisPojo();
                drTestDao.test_def_update(new DalHints());
                drTestDao.insert(null, daoPojo);

                log.info("success!!!");
            } catch (Exception e) {
                log.warn("failed", e.getMessage());
                fail();
            }
            Thread.sleep(1000);
        }
    }
}
