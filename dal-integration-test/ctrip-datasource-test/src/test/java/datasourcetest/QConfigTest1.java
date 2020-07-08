package datasourcetest;

import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.helper.PoolPropertiesHelper;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.PoolPropertiesSwitch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;


public class QConfigTest1 {
    private static PoolPropertiesSwitch poolPropertiesSwitch=new PoolPropertiesSwitch();
    private static Logger log = LoggerFactory.getLogger(QConfigTest1.class);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info(String.format("初始化属性配置"));

        Map<String, String> map = new HashMap<>();
        map.put("enableDynamicPoolProperties", "true");
        map.put("CruiseBookingDB.minIdle","10");
        map.put("CruiseBookingDB.connectionProperties", "sendTimeAsDateTime=false");
        map.put("ctripoaDB_W.minIdle", "30");
        map.put("ctripoaDB_W.connectionProperties", "sendStringParametersAsUnicode=false");
        map.put("CruiseBookingDB.validationTimeoutMillis", "600");

        poolPropertiesSwitch.modifyPoolProperties(map);
        log.info(String.format("初始化配置后等待35秒生效"));
        Thread.sleep(35000);
    }
    // case1,fat环境，QConfig APP级别不配置
    @Test
    public void testQConfigCase1() {
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("DalServiceDB");

        Map<String, String> settings = new HashMap<>();
        // settings.put(TitanProvider.SERVICE_ADDRESS,
        // "https://ws.titan.uat.qa.nt.ctripcorp.com/titanservice/query");
        // settings.put(TitanProvider.USE_LOCAL_CONFIG, "false");
        try {
            provider.initialize(settings);
            provider.setup(dbNames);


            PoolProperties pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("DalServiceDB"));

            assertFalse(pc.isTestWhileIdle());
            assertTrue(pc.isTestOnBorrow());
            assertFalse(pc.isTestOnReturn());
            assertEquals("SELECT 1", pc.getValidationQuery());
            assertEquals(30000, pc.getValidationInterval());
            assertEquals(5000, pc.getTimeBetweenEvictionRunsMillis());
            assertEquals(100, pc.getMaxActive());
            assertEquals(1, pc.getMinIdle());
            assertEquals(6000, pc.getMaxWait());
            assertEquals(28000000, pc.getMaxAge());
            assertEquals(1, pc.getInitialSize());
            assertEquals(65, pc.getRemoveAbandonedTimeout());
            assertTrue(pc.isRemoveAbandoned());
            assertFalse(pc.isLogAbandoned());
            assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
            assertEquals(
                    "sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8;useSSL=false;socketTimeout=100000;connectTimeout=1050;loginTimeout=2",
                    pc.getConnectionProperties());
            // assertEquals("set names utf8mb4",pc.getInitSQL());
            assertEquals(
                    "com.ctrip.platform.dal.dao.datasource.DataSourceValidator",
                    pc.getValidatorClassName());
            assertEquals(250, pc.getValidationQueryTimeout());

            DataSourceConfigure result = null;
            result = provider.getDataSourceConfigure("DalServiceDB");
            Assert.assertNotNull(result);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    // case2,fat环境，QConfig
    // APP级别不配置removeAbandonedTimeout=100,datasource.xml配置uiautomationtestdb_W
    @Test
    public void testQConfigCase2() {
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("uiautomationtestdb_W");
        dbNames.add("DalServiceDB");
        Map<String, String> settings = new HashMap<>();
        // settings.put(TitanProvider.SERVICE_ADDRESS,
        // "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query");
        // settings.put(TitanProvider.USE_LOCAL_CONFIG, "false");
        try {
            provider.initialize(settings);
            provider.setup(dbNames);

            PoolProperties pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("uiautomationtestdb_W"));

            assertFalse(pc.isTestWhileIdle());
            assertFalse(pc.isTestOnBorrow());
            assertFalse(pc.isTestOnReturn());
            assertEquals("SELECT 1", pc.getValidationQuery());
            assertEquals(30000, pc.getValidationInterval());
            assertEquals(5000, pc.getTimeBetweenEvictionRunsMillis());
            assertEquals(100, pc.getMaxActive());
            assertEquals(10, pc.getMinIdle());
            assertEquals(10000, pc.getMaxWait());
            assertEquals(28000000, pc.getMaxAge());
            assertEquals(1, pc.getInitialSize());
            assertEquals(60, pc.getRemoveAbandonedTimeout());
            assertTrue(pc.isRemoveAbandoned());
            assertTrue(pc.isLogAbandoned());
            assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
            assertEquals("rewriteBatchedStatements=true",
                    pc.getConnectionProperties());
            // assertEquals("set names utf8mb4",pc.getInitSQL());
            assertEquals(
                    "com.ctrip.platform.dal.dao.datasource.DataSourceValidator",
                    pc.getValidatorClassName());

            pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("DalServiceDB"));

            assertFalse(pc.isTestWhileIdle());
            assertTrue(pc.isTestOnBorrow());
            assertFalse(pc.isTestOnReturn());
            assertEquals("SELECT 1", pc.getValidationQuery());
            assertEquals(30000, pc.getValidationInterval());
            assertEquals(5000, pc.getTimeBetweenEvictionRunsMillis());
            assertEquals(100, pc.getMaxActive());
            assertEquals(1, pc.getMinIdle());
            assertEquals(6000, pc.getMaxWait());
            assertEquals(28000000, pc.getMaxAge());
            assertEquals(1, pc.getInitialSize());
            assertEquals(65, pc.getRemoveAbandonedTimeout());
            assertTrue(pc.isRemoveAbandoned());
            assertFalse(pc.isLogAbandoned());
            assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
            assertEquals(
                    "sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8;useSSL=false;socketTimeout=100000;connectTimeout=1050;loginTimeout=2",
                    pc.getConnectionProperties());
            // assertEquals("set names utf8mb4",pc.getInitSQL());
            assertEquals(
                    "com.ctrip.platform.dal.dao.datasource.DataSourceValidator",
                    pc.getValidatorClassName());

            DataSourceConfigure result = null;

            result = provider
                    .getDataSourceConfigure("uiautomationtestdb_W");
            Assert.assertNotNull(result);

            result = provider
                    .getDataSourceConfigure("DalServiceDB");
            Assert.assertNotNull(result);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    // case3,fat环境，QConfig
    // APP级别不配置removeAbandonedTimeout=100,配置中心datasource级别配置CruiseBookingDB
    @Test
    public void testQConfigCase3() {
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("CruiseBookingDB");
        dbNames.add("DalServiceDB");
        Map<String, String> settings = new HashMap<>();
        // settings.put(TitanProvider.SERVICE_ADDRESS,
        // "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query");
        // settings.put(TitanProvider.USE_LOCAL_CONFIG, "false");
        try {
            provider.initialize(settings);
            provider.setup(dbNames);

            PoolProperties pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("CruiseBookingDB"));

            assertFalse(pc.isTestWhileIdle());
            assertTrue(pc.isTestOnBorrow());
            assertFalse(pc.isTestOnReturn());
            assertEquals("SELECT 1", pc.getValidationQuery());
            assertEquals(30000, pc.getValidationInterval());
            assertEquals(600, pc.getValidationQueryTimeout());
            assertEquals(5000, pc.getTimeBetweenEvictionRunsMillis());
            assertEquals(100, pc.getMaxActive());
            assertEquals(10, pc.getMinIdle());
            assertEquals(6000, pc.getMaxWait());
            assertEquals(28000000, pc.getMaxAge());
            assertEquals(1, pc.getInitialSize());
            assertEquals(65, pc.getRemoveAbandonedTimeout());
            assertTrue(pc.isRemoveAbandoned());
            assertFalse(pc.isLogAbandoned());
            assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
            assertEquals("sendTimeAsDateTime=false",
                    pc.getConnectionProperties());
            // assertEquals("set names utf8mb4",pc.getInitSQL());
            assertEquals(
                    "com.ctrip.platform.dal.dao.datasource.DataSourceValidator",
                    pc.getValidatorClassName());

            pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("DalServiceDB"));

            assertFalse(pc.isTestWhileIdle());
            assertTrue(pc.isTestOnBorrow());
            assertFalse(pc.isTestOnReturn());
            assertEquals("SELECT 1", pc.getValidationQuery());
            assertEquals(30000, pc.getValidationInterval());
            assertEquals(5000, pc.getTimeBetweenEvictionRunsMillis());
            assertEquals(100, pc.getMaxActive());
            assertEquals(1, pc.getMinIdle());
            assertEquals(6000, pc.getMaxWait());
            assertEquals(28000000, pc.getMaxAge());
            assertEquals(1, pc.getInitialSize());
            assertEquals(65, pc.getRemoveAbandonedTimeout());
            assertTrue(pc.isRemoveAbandoned());
            assertFalse(pc.isLogAbandoned());
            assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
            assertEquals(
                    "sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8;useSSL=false;socketTimeout=100000;connectTimeout=1050;loginTimeout=2",
                    pc.getConnectionProperties());
            // assertEquals("set names utf8mb4",pc.getInitSQL());
            assertEquals(
                    "com.ctrip.platform.dal.dao.datasource.DataSourceValidator",
                    pc.getValidatorClassName());

            DataSourceConfigure result = null;

            result = provider
                    .getDataSourceConfigure("CruiseBookingDB");
            Assert.assertNotNull(result);

            result = provider
                    .getDataSourceConfigure("DalServiceDB");
            Assert.assertNotNull(result);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    // case4,fat环境，QConfig APP级别不配置removeAbandonedTimeout=100,
    // 配置中心datasource级别配置DalService3DB_W,datasource.xml配置ctripoaDB_W
    @Test
    public void testQConfigCase4() {
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("ctripoaDB_W");
        dbNames.add("DalServiceDB");
        Map<String, String> settings = new HashMap<>();
        // settings.put(TitanProvider.SERVICE_ADDRESS,
        // "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query");
        // settings.put(TitanProvider.USE_LOCAL_CONFIG, "false");
        try {
            provider.initialize(settings);
            provider.setup(dbNames);

            PoolProperties pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("ctripoaDB_W"));

            assertFalse(pc.isTestWhileIdle());
            assertFalse(pc.isTestOnBorrow());
            assertFalse(pc.isTestOnReturn());
            assertEquals("SELECT 1", pc.getValidationQuery());
            assertEquals(30000, pc.getValidationInterval());
            assertEquals(5000, pc.getTimeBetweenEvictionRunsMillis());
            assertEquals(100, pc.getMaxActive());
            assertEquals(10, pc.getMinIdle());
            assertEquals(10000, pc.getMaxWait());
            assertEquals(28000000, pc.getMaxAge());
            assertEquals(1, pc.getInitialSize());
            assertEquals(60, pc.getRemoveAbandonedTimeout());
            assertTrue(pc.isRemoveAbandoned());
            assertTrue(pc.isLogAbandoned());
            assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
            assertEquals("sendStringParametersAsUnicode=false",
                    pc.getConnectionProperties());
            // assertEquals("set names utf8mb4",pc.getInitSQL());
            assertEquals(
                    "com.ctrip.platform.dal.dao.datasource.DataSourceValidator",
                    pc.getValidatorClassName());

            pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("DalServiceDB"));

            assertFalse(pc.isTestWhileIdle());
            assertTrue(pc.isTestOnBorrow());
            assertFalse(pc.isTestOnReturn());
            assertEquals("SELECT 1", pc.getValidationQuery());
            assertEquals(30000, pc.getValidationInterval());
            assertEquals(5000, pc.getTimeBetweenEvictionRunsMillis());
            assertEquals(100, pc.getMaxActive());
            assertEquals(1, pc.getMinIdle());
            assertEquals(6000, pc.getMaxWait());
            assertEquals(28000000, pc.getMaxAge());
            assertEquals(1, pc.getInitialSize());
            assertEquals(65, pc.getRemoveAbandonedTimeout());
            assertTrue(pc.isRemoveAbandoned());
            assertFalse(pc.isLogAbandoned());
            assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
            assertEquals(
                    "sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8;useSSL=false;socketTimeout=100000;connectTimeout=1050;loginTimeout=2",
                    pc.getConnectionProperties());
            // assertEquals("set names utf8mb4",pc.getInitSQL());
            assertEquals(
                    "com.ctrip.platform.dal.dao.datasource.DataSourceValidator",
                    pc.getValidatorClassName());

            DataSourceConfigure result = null;

            result = provider
                    .getDataSourceConfigure("ctripoaDB_W");
            Assert.assertNotNull(result);

            result = provider
                    .getDataSourceConfigure("DalServiceDB");
            Assert.assertNotNull(result);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
