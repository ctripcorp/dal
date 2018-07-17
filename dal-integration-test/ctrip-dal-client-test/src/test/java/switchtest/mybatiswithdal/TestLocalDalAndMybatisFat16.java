package switchtest.mybatiswithdal;

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

import static org.junit.Assert.fail;

/**
 * Created by lilj on 2018/3/6.
 */
public class TestLocalDalAndMybatisFat16 {
    private static Logger log = LoggerFactory.getLogger(TestLocalDalAndMybatisFat16.class);
    private static ConnectionStringSwitch connectionStringSwitch = new ConnectionStringSwitch();
    private static PoolPropertiesSwitch poolPropertiesSwitch = new PoolPropertiesSwitch();
    private static Boolean isPro = true;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        poolPropertiesSwitch.resetPoolProperties();
    }

    @Before
    public void setUp() throws Exception {
        DalClientFactory.shutdownFactory();
        connectionStringSwitch.resetConnectionString(isPro);
        Thread.sleep(5000);
    }

    @After
    public void tearDown() throws Exception {
        DalClientFactory.shutdownFactory();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
//        connectionStringSwitch.resetConnectionString();
//        Thread.sleep(5000);
    }

    @Test
    public void testLocalDALAndMybatisWithSameKey() throws Exception {
        try {
            DalClientFactory.initClientFactory(this.getClass().getClassLoader().getResource(".").getPath() + "DalConfig/Dal.config");
            DalClientFactory.warmUpConnections();

            ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context-singlekey.xml");
            DRTestDao drTestDao = applicationContext.getBean(DRTestDao.class);
            DRTestMapperDao drTestMapperDao = applicationContext.getBean(DRTestMapperDao.class);
            fail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLocalDALAndMybatisWithDifferentKey() throws Exception {
        DalClientFactory.initClientFactory(this.getClass().getClassLoader().getResource(".").getPath() + "DalConfig/Dal.config");
        DalClientFactory.warmUpConnections();

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context-differentKey.xml");
        DRTestDao drTestDao = applicationContext.getBean(DRTestDao.class);
        DRTestMapperDao drTestMapperDao = applicationContext.getBean(DRTestMapperDao.class);

        for (int i = 0; i < 10; i++) {
            try {
                log.info(String.format("切换前hostname in mybatis：%s", drTestMapperDao.getHostNameMySQL()));
                log.info(String.format("切换前hostname in dal：%s", drTestDao.selectHostname(null)));
                Assert.assertEquals("llj_test", drTestDao.selectDatabase(null));

                log.info("clear data in mybatis ");
                drTestMapperDao.truncateTable();
                log.info("insert data in dal ");
                drTestMapperDao.addDRTestMybatisPojo();
                log.info("clear data in dal ", drTestDao.test_def_update(new DalHints()));
                DRTestPojo daoPojo2 = new DRTestPojo();
                daoPojo2.setName("testDal");
                log.info("insert data ", drTestDao.insert(null, daoPojo2));

                log.info(String.format("开始切换"));
                //调用切换接口
                connectionStringSwitch.postByMHA(isPro);
                //等待5秒
                Thread.sleep(5000);

                log.info(String.format("切换后hostname in mybatis：%s", drTestMapperDao.getHostNameMySQL()));
                log.info(String.format("切换后hostname in dal：%s", drTestDao.selectHostname(null)));
                Assert.assertEquals("llj_test", drTestDao.selectDatabase(null));

                drTestMapperDao.truncateTable();
                drTestMapperDao.addDRTestMybatisPojo();
                drTestDao.test_def_update(new DalHints());
                drTestDao.insert(null, daoPojo2);

                log.info("test success!!!");
            } catch (Exception e) {
                e.printStackTrace();
                log.warn("test failed!!!", e.getMessage());
                fail();
            }
            Thread.sleep(1000);
        }
    }
}
