package switchtest.mybatiswithdal;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import mybatis.mysql.DRTestDao;
import mybatis.mysql.DRTestMapperDao;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import testUtil.ConnectionStringSwitch;
import testUtil.PoolPropertiesSwitch;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by lilj on 2018/3/6.
 */
public class TestPoolProperties {
    private static Logger log = LoggerFactory.getLogger(TestPoolProperties.class);
    private static ConnectionStringSwitch connectionStringSwitch = new ConnectionStringSwitch();
    private static PoolPropertiesSwitch poolPropertiesSwitch = new PoolPropertiesSwitch();
    private static Boolean isPro = true;

    @Before
    public void setUp() throws Exception {
        DalClientFactory.shutdownFactory();
        DalClientFactory.initClientFactory(ClassLoader.getSystemClassLoader().getResource(".").getPath()+"DalConfigForSwitch/Dal.config");
        DalClientFactory.warmUpConnections();
        connectionStringSwitch.resetConnectionString(isPro);
        Thread.sleep(5000);
        poolPropertiesSwitch.resetPoolProperties();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        connectionStringSwitch.resetConnectionString(isPro);
        Thread.sleep(5000);
        poolPropertiesSwitch.resetPoolProperties();
    }


    @Test
    public void testDatasourceByDALAndMybatisWithSameKey() throws Exception {
//        DalClientFactory.initClientFactory();
//        DalClientFactory.warmUpConnections();

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context-singlekey.xml");
        DRTestDao drTestDao = applicationContext.getBean(DRTestDao.class);
        DRTestMapperDao drTestMapperDao = applicationContext.getBean(DRTestMapperDao.class);

        DalDataSourceFactory factory = (DalDataSourceFactory) applicationContext.getBean("dalDataSourceFactory");
        for (int i = 0; i < 5; i++) {
            factory.createDataSource("mysqldaltest01db_W");
        }

        log.info("query hostname before modify");
        String hostnameByMybatis = drTestMapperDao.getHostNameMySQL();
        log.info(String.format("hostname in mybatis：%s", hostnameByMybatis));
        String hostnameByDAL = drTestDao.selectHostname(null);
        log.info(String.format("hostname in dal：%s", hostnameByDAL));


        log.info("modify when switch is off");
        Map<String, String> map = new HashMap<>();
        map.put("maxAge", "哦");
        poolPropertiesSwitch.modifyPoolProperties(map);

        Thread.sleep(35000);

        log.info("check modify when switch is off");
        try {
            String currentHostnameByMybatis = drTestMapperDao.getHostNameMySQL();
            log.info(String.format("hostname in mybatis：%s", currentHostnameByMybatis));
            assertEquals(hostnameByMybatis, currentHostnameByMybatis);
            String currentHostnameByDAL = drTestDao.selectHostname(null);
            log.info(String.format("hostname in dal：%s", currentHostnameByDAL));
            assertEquals(hostnameByDAL, currentHostnameByDAL);
        } catch (Throwable e) {
            log.error("enableDynamicPoolProperties is off but exception appeared after invalid modify", e.getMessage());
            fail();
        }


        log.info("MHA switch");
        connectionStringSwitch.postByMHA(isPro);

        Thread.sleep(35000);

        log.info("check MHA switch");
        try {
            String currentHostnameByMybatis = drTestMapperDao.getDatabaseMySQL();
            log.info(String.format("hostname in mybatis：%s", currentHostnameByMybatis));
            assertNotEquals(hostnameByMybatis, currentHostnameByMybatis);
            String currentHostnameByDAL = drTestDao.selectHostname(null);
            log.info(String.format("hostname in dal：%s", currentHostnameByDAL));
            assertNotEquals(hostnameByDAL, currentHostnameByDAL);
        } catch (Throwable e) {
            log.error("enableDynamicPoolProperties is off but exception appeared after mha switch", e.getMessage());
            fail();
        }


        log.info("set enableDynamicPoolProperties on");
        map.put("enableDynamicPoolProperties", "true");
        poolPropertiesSwitch.modifyPoolProperties(map);
//        drTestDao.uploadProperties(map);

        log.info("check when switch is on");
        long startTime = System.currentTimeMillis();
        boolean toBeContinued = true;
        while (toBeContinued) {
            try {
                log.info(String.format("hostname in mybatis：%s", drTestMapperDao.getHostNameMySQL()));
                if (System.currentTimeMillis() - startTime < 40000) {
                    log.info("query by mybatis should exception but not, please wait...");
                    toBeContinued = true;
                } else {
                    toBeContinued = false;
                    fail("notify timeout");
                }
            } catch (Exception e) {
                log.info("modify is effective for mybatis datasource");
                toBeContinued = false;
            }
            Thread.sleep(1000);
        }

        try {
            log.info(String.format("hostname in dal：%s", drTestDao.selectHostname(null)));
            fail("modify is not effective for dal datasource");
        } catch (Exception e) {
            log.info("modify is effective for dal datasource");
        }

        log.info("success!");
    }

    @Test
    public void testDatasourceByDALAndMybatisWithDifferentKey() throws Exception {
//        DalClientFactory.initClientFactory();
//        DalClientFactory.warmUpConnections();

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context-differentKey.xml");
        DRTestDao drTestDao = applicationContext.getBean(DRTestDao.class);
        DRTestMapperDao drTestMapperDao = applicationContext.getBean(DRTestMapperDao.class);

        DalDataSourceFactory factory = (DalDataSourceFactory) applicationContext.getBean("dalDataSourceFactory");
        for (int i = 0; i < 5; i++) {
            factory.createDataSource("mysqldaltest02db_W");
        }


        log.info("query hostname before modify");
        String hostnameByMybatis = drTestMapperDao.getHostNameMySQL();
        log.info(String.format("hostname in mybatis：%s", hostnameByMybatis));
        String hostnameByDAL = drTestDao.selectHostname(null);
        log.info(String.format("hostname in dal：%s", hostnameByDAL));

        log.info("modify when switch is off");
        Map<String, String> map = new HashMap<>();
        map.put("maxAge", "哦");
        poolPropertiesSwitch.modifyPoolProperties(map);

        Thread.sleep(35000);

        log.info("check modify when switch is off");
        try {
            String currentHostnameByMybatis = drTestMapperDao.getHostNameMySQL();
            log.info(String.format("hostname in mybatis：%s", currentHostnameByMybatis));
            assertEquals(hostnameByMybatis, currentHostnameByMybatis);
            String currentHostnameByDAL = drTestDao.selectHostname(null);
            log.info(String.format("hostname in dal：%s", currentHostnameByDAL));
            assertEquals(hostnameByDAL, currentHostnameByDAL);
        } catch (Throwable e) {
            log.error("enableDynamicPoolProperties is off but exception appeared after invalid modify", e.getMessage());
            fail();
        }


        log.info("MHA switch");
        connectionStringSwitch.postByMHA(isPro);

        Thread.sleep(35000);

        log.info("check MHA switch");
        try {
            String currentHostnameByMybatis = drTestMapperDao.getDatabaseMySQL();
            log.info(String.format("hostname in mybatis：%s", currentHostnameByMybatis));
            assertNotEquals(hostnameByMybatis, currentHostnameByMybatis);
            String currentHostnameByDAL = drTestDao.selectHostname(null);
            log.info(String.format("hostname in dal：%s", currentHostnameByDAL));
            assertNotEquals(hostnameByDAL, currentHostnameByDAL);
        } catch (Throwable e) {
            log.error("enableDynamicPoolProperties is off but exception appeared after mha switch", e.getMessage());
            fail();
        }


        log.info("set enableDynamicPoolProperties on");
        map.put("enableDynamicPoolProperties", "true");
        poolPropertiesSwitch.modifyPoolProperties(map);
//        drTestDao.uploadProperties(map);

        log.info("check when switch is on");
        long startTime = System.currentTimeMillis();
        boolean toBeContinued = true;
        while (toBeContinued) {
            try {
                log.info(String.format("hostname in mybatis：%s", drTestMapperDao.getHostNameMySQL()));
                if (System.currentTimeMillis() - startTime < 40000) {
                    log.info("query by mybatis should exception but not, please wait...");
                    toBeContinued = true;
                } else {
                    toBeContinued = false;
                    fail("notify timeout");
                }
            } catch (Exception e) {
                log.info("modify is effective for mybatis datasource");
                toBeContinued = false;
            }
            Thread.sleep(1000);
        }

        try {
            log.info(String.format("hostname in dal：%s", drTestDao.selectHostname(null)));
            fail("modify is not effective for dal datasource");
        } catch (Exception e) {
            log.info("modify is effective for dal datasource");
        }

        log.info("success!");
    }
}
