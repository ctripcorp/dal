package switchtest.mybatis;


import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocatorManager;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testUtil.ConnectionStringSwitch;
import testUtil.IpDomainSwitch;

/**
 * Created by lilj on 2018/3/4.
 */
public class IpDomainSwitchTest {
    private static ConnectionStringSwitch connectionStringSwitch = new ConnectionStringSwitch();
    private static Logger log = LoggerFactory.getLogger(IpDomainSwitchTest.class);
    private static IpDomainSwitch ipDomainSwitch = new IpDomainSwitch();
    private static Boolean isPro=true;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        if (ipDomainSwitch.getStatus().equalsIgnoreCase("failover")) {
            ipDomainSwitch.setNormal();
//            Thread.sleep(35000);
        }
    }

    @AfterClass
    public static void tearDownBeforeClass() throws Exception {
        if (ipDomainSwitch.getStatus().equalsIgnoreCase("failover")) {
            ipDomainSwitch.setNormal();
//            Thread.sleep(35000);
        }
    }

    @Before
    public void setUp() throws Exception {
        if (ipDomainSwitch.getStatus().equalsIgnoreCase("failover")) {
            ipDomainSwitch.setNormal();
            Thread.sleep(35000);
        }
    }

    @After
    public void tearDown() throws Exception {
        DalClientFactory.shutdownFactory();
    }

    @Test
    public void testSetUpInDomainMode() {
        String url;
        try {
            log.info(String.format("set failover before dal setup"));
            ipDomainSwitch.setFailover();
            log.info(String.format("set failover done"));
//            Thread.sleep(35000);

//            DalClientFactory.shutdownFactory();
            DalClientFactory.initClientFactory(ClassLoader.getSystemClassLoader().getResource(".").getPath()+"DalConfigForSwitch/Dal.config");
            DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureLocatorManager.getInstance();
            DataSourceConfigure dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure("mysqldaltest01db_W");
            url = dataSourceConfigure.getConnectionUrl();
            log.info(String.format("the initial url in domain mode is: %s", url));
            Assert.assertNotEquals(-1, url.indexOf("mysqldaltest01.mysql.db.fat.qa.nt.ctripcorp.com:55111"));

            log.info(String.format("set normal start"));
            ipDomainSwitch.setNormal();
            log.info(String.format("35s after switch"));
            Thread.sleep(35000);

            dataSourceConfigureLocator = DataSourceConfigureLocatorManager.getInstance();
            dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure("mysqldaltest01db_W");
            url = dataSourceConfigure.getConnectionUrl();

            log.info(String.format("the url after set normal is: %s", url));
            Assert.assertNotEquals(-1, url.indexOf("10.2.74"));
            log.info("done");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIpDomainSwitch() {
        String url;
        String ip;
        try {
            //initial url
            DalClientFactory.initClientFactory(ClassLoader.getSystemClassLoader().getResource(".").getPath()+"DalConfigForSwitch/Dal.config");
            DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureLocatorManager.getInstance();
            DataSourceConfigure dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure("mysqldaltest01db_W");
            url = dataSourceConfigure.getConnectionUrl();
            log.info(String.format("the url before set failover is: %s", url));
            Assert.assertNotEquals(-1, url.indexOf("10.2.74"));
//          get initial ip
            if (url.indexOf("10.2.74.111") != -1)
                ip = "10.2.74.111";
            else
                ip = "10.2.74.122";

            //set failover
            log.info(String.format("set failover start"));
            ipDomainSwitch.setFailover();
            log.info(String.format("35s after switch"));
            Thread.sleep(35000);

            //check failover url
            dataSourceConfigureLocator = DataSourceConfigureLocatorManager.getInstance();
            dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure("mysqldaltest01db_W");
            url = dataSourceConfigure.getConnectionUrl();
            log.info(String.format("the url after set failover is: %s", url));
            Assert.assertNotEquals(-1, url.indexOf("mysqldaltest01.mysql.db.fat.qa.nt.ctripcorp.com"));

            //connection string switch in failover mode
            log.info(String.format("connection string switch in domain mode start"));
            connectionStringSwitch.postByMHA(isPro);
            log.info(String.format("connection string switch in domain mode done"));
            Thread.sleep(5000);
            log.info(String.format("check connection string switch in domain mode"));

            //check connection string switch in domain mode, qconfig will not notify dal to refresh connection string
            dataSourceConfigureLocator = DataSourceConfigureLocatorManager.getInstance();
            dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure("mysqldaltest01db_W");
            url = dataSourceConfigure.getConnectionUrl();
            log.info(String.format("the url after connection string switch in domain mode is: %s", url));
            Assert.assertNotEquals(-1, url.indexOf("mysqldaltest01.mysql.db.fat.qa.nt.ctripcorp.com"));
            log.info(String.format("check connection string switch in domain mode passed"));

            //set normal
            log.info(String.format("set normal start"));
            ipDomainSwitch.setNormal();
            log.info(String.format("35s after switch"));
            Thread.sleep(35000);

            //check normal url
            dataSourceConfigureLocator = DataSourceConfigureLocatorManager.getInstance();
            dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure("mysqldaltest01db_W");
            url = dataSourceConfigure.getConnectionUrl();

            log.info(String.format("the url after set normal is: %s", url));
            Assert.assertNotEquals(-1,url.indexOf("10.2.74"));

            //            get ip from locator but not qconfig
            log.info(String.format("the current ip is: %s",ip));
            Assert.assertNotEquals(-1, url.indexOf(ip));

            //connection string switch in ip mode, qconfig will notify dal to refresh connection string
            log.info(String.format("connection string switch in ip mode start"));
            connectionStringSwitch.postByMHA(isPro);
            log.info(String.format("connection string switch in ip mode done"));
            Thread.sleep(5000);
            log.info(String.format("check connection string in ip mode switch"));

            //check connection string switch in ip mode
            dataSourceConfigureLocator = DataSourceConfigureLocatorManager.getInstance();
            dataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure("mysqldaltest01db_W");
            url = dataSourceConfigure.getConnectionUrl();

            log.info(String.format("the url after connection string switch in ip mode is: %s", url));
            Assert.assertNotEquals(-1, url.indexOf("10.2.74"));
            log.info(String.format("check connection string switch in ip mode passed"));

            if (ip.equalsIgnoreCase("10.2.74.111"))
                Assert.assertNotEquals(-1, url.indexOf("10.2.74.111"));
            else
                Assert.assertNotEquals(-1, url.indexOf("10.2.74.122"));

            log.info("done");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
