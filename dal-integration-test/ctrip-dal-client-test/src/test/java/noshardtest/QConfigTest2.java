package noshardtest;

import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.helper.PoolPropertiesHelper;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testUtil.PoolPropertiesSwitch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class QConfigTest2 {
    private static PoolPropertiesSwitch poolPropertiesSwitch=new PoolPropertiesSwitch();
	private static Logger log = LoggerFactory.getLogger(QConfigTest2.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		log.info(String.format("初始化属性配置"));

		Map<String, String> map = new HashMap<>();
		map.put("enableDynamicPoolProperties", "true");
		map.put("commonOrderShard1DB_S1_S.minIdle", "30");
		map.put("commonOrderShard1DB_S1_S.connectionProperties", "rewriteBatchedStatements=false");
		map.put("YouSearchDB.testWhileIdle", "false");
		map.put("YouSearchDB.testOnBorrow", "false");
		map.put("YouSearchDB.validationQuery", "SELECT 1");
		map.put("YouSearchDB.validationInterval", "30000");
		map.put("YouSearchDB.timeBetweenEvictionRunsMillis", "30000");
		map.put("YouSearchDB.maxActive", "100");
		map.put("YouSearchDB.minIdle", "30");
		map.put("YouSearchDB.maxWait", "10000");
		map.put("YouSearchDB.maxAge", "30000");
		map.put("YouSearchDB.initialSize", "1");
		map.put("YouSearchDB.removeAbandonedTimeout", "50");
		map.put("YouSearchDB.removeAbandoned", "true");
		map.put("YouSearchDB.logAbandoned", "false");
		map.put("YouSearchDB.minEvictableIdleTimeMillis", "30000");
		map.put("YouSearchDB.connectionProperties", "rewriteBatchedStatements=false;allowMultiQueries=true");
		map.put("YouSearchDB.initSQL", "set names utf8mb4");
		map.put("abtestdb.option", "rewriteBatchedStatements=false");
		map.put("commonOrderShard2DB_S2_S.option", "rewriteBatchedStatements=false");
		map.put("uiautomationtestdb_W.connectionProperties", "rewriteBatchedStatements=false");
		map.put("CommonOrderDB_S6_R.connectionProperties", "rewriteBatchedStatements=false");
		map.put("logAbandoned", "true");
		map.put("removeAbandonedTimeout", "70");

		poolPropertiesSwitch.modifyPoolProperties(map);
		log.info(String.format("初始化配置后等待35秒生效"));
		Thread.sleep(35000);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	// case5,配置env=fat环境，QConfig APP级别配置removeAbandonedTimeout=100,
	@Test
		public void testQConfigCase5() {
			TitanProvider provider = new TitanProvider();
			Set<String> dbNames = new HashSet<>();
			dbNames.add("CorpPerformanceManagementDB_W");
			Map<String, String> settings = new HashMap<>();
			try {
				provider.initialize(settings);
				provider.setup(dbNames);

				PoolProperties pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("CorpPerformanceManagementDB_W"));

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
				assertEquals(70, pc.getRemoveAbandonedTimeout());
				assertTrue(pc.isRemoveAbandoned());
				assertTrue(pc.isLogAbandoned());
				assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
				assertEquals(
						"sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8;useSSL=false;socketTimeout=100000;connectTimeout=1050;loginTimeout=2",
						pc.getConnectionProperties());
				// assertEquals("set names utf8mb4",pc.getInitSQL());
				assertEquals(
						"com.ctrip.platform.dal.dao.datasource.DataSourceValidator",
						pc.getValidatorClassName());
//                assertEquals(6, pc.getValidationQueryTimeout());

				DataSourceConfigure result = null;

				result = provider
						.getDataSourceConfigure("CorpPerformanceManagementDB_W");
				Assert.assertNotNull(result);

			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}

		// case6,配置env=fat环境，QConfig
		// APP级别配置removeAbandonedTimeout=100,datasource.xml配置CruiseInterfaceDB
		@Test
		public void testQConfigCase6() {
			TitanProvider provider = new TitanProvider();
			Set<String> dbNames = new HashSet<>();
			dbNames.add("CorpPerformanceManagementDB_W");
			dbNames.add("CruiseInterfaceDB");
			Map<String, String> settings = new HashMap<>();
			/*settings.put(TitanProvider.SERVICE_ADDRESS,
					"https://ws.titan.uat.qa.nt.ctripcorp.com/titanservice/query");
			settings.put(TitanProvider.USE_LOCAL_CONFIG, "false");*/
			try {
				provider.initialize(settings);
				provider.setup(dbNames);

				PoolProperties pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("CorpPerformanceManagementDB_W"));

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
				assertEquals(70, pc.getRemoveAbandonedTimeout());
				assertTrue(pc.isRemoveAbandoned());
				assertTrue(pc.isLogAbandoned());
				assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
				assertEquals(
						"sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8;useSSL=false;socketTimeout=100000;connectTimeout=1050;loginTimeout=2",
						pc.getConnectionProperties());
				// assertEquals("set names utf8mb4",pc.getInitSQL());
				assertEquals(
						"com.ctrip.platform.dal.dao.datasource.DataSourceValidator",
						pc.getValidatorClassName());

				pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("CruiseInterfaceDB"));

				assertFalse(pc.isTestWhileIdle());
				assertFalse(pc.isTestOnBorrow());
				assertFalse(pc.isTestOnReturn());
				assertEquals("SELECT 1", pc.getValidationQuery());
				assertEquals(30000, pc.getValidationInterval());
				assertEquals(500,pc.getValidationQueryTimeout());
				assertEquals(5000, pc.getTimeBetweenEvictionRunsMillis());
				assertEquals(100, pc.getMaxActive());
				assertEquals(10, pc.getMinIdle());
				assertEquals(10000, pc.getMaxWait());
				assertEquals(28000000, pc.getMaxAge());
				assertEquals(1, pc.getInitialSize());
				assertEquals(75, pc.getRemoveAbandonedTimeout());
				assertTrue(pc.isRemoveAbandoned());
				assertTrue(pc.isLogAbandoned());
				assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
				assertEquals("rewriteBatchedStatements=true",
						pc.getConnectionProperties());
				// assertEquals("set names utf8mb4",pc.getInitSQL());
				assertEquals(
						"com.ctrip.platform.dal.dao.datasource.DataSourceValidator",
						pc.getValidatorClassName());

				DataSourceConfigure result = null;

				result = provider
						.getDataSourceConfigure("CorpPerformanceManagementDB_W");
				Assert.assertNotNull(result);

				result = provider.getDataSourceConfigure("CruiseInterfaceDB");
				Assert.assertNotNull(result);

			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}

		// case7,配置env=fat环境，QConfig
		// APP级别配置removeAbandonedTimeout=100,datasource级别配置commonOrderShard1DB_S1_S
		@Test
		public void testQConfigCase7() {
			TitanProvider provider = new TitanProvider();
			Set<String> dbNames = new HashSet<>();
			dbNames.add("CorpPerformanceManagementDB_W");
			dbNames.add("commonOrderShard1DB_S1_S");
			Map<String, String> settings = new HashMap<>();
			/*settings.put(TitanProvider.SERVICE_ADDRESS,
					"https://ws.titan.uat.qa.nt.ctripcorp.com/titanservice/query");
			settings.put(TitanProvider.USE_LOCAL_CONFIG, "false");*/
			try {
				provider.initialize(settings);
				provider.setup(dbNames);

				PoolProperties pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("CorpPerformanceManagementDB_W"));

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
				assertEquals(70, pc.getRemoveAbandonedTimeout());
				assertTrue(pc.isRemoveAbandoned());
				assertTrue(pc.isLogAbandoned());
				assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
				assertEquals(
						"sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8;useSSL=false;socketTimeout=100000;connectTimeout=1050;loginTimeout=2",
						pc.getConnectionProperties());
				// assertEquals("set names utf8mb4",pc.getInitSQL());
				assertEquals(
						"com.ctrip.platform.dal.dao.datasource.DataSourceValidator",
						pc.getValidatorClassName());

				pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("commonOrderShard1DB_S1_S"));

				assertFalse(pc.isTestWhileIdle());
				assertTrue(pc.isTestOnBorrow());
				assertFalse(pc.isTestOnReturn());
				assertEquals("SELECT 1", pc.getValidationQuery());
				assertEquals(30000, pc.getValidationInterval());
				assertEquals(5000, pc.getTimeBetweenEvictionRunsMillis());
				assertEquals(100, pc.getMaxActive());
				assertEquals(30, pc.getMinIdle());
				assertEquals(6000, pc.getMaxWait());
				assertEquals(28000000, pc.getMaxAge());
				assertEquals(1, pc.getInitialSize());
				assertEquals(70, pc.getRemoveAbandonedTimeout());
				assertTrue(pc.isRemoveAbandoned());
				assertTrue(pc.isLogAbandoned());
				assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
				assertEquals("rewriteBatchedStatements=false",
						pc.getConnectionProperties());
				// assertEquals("set names utf8mb4",pc.getInitSQL());
				assertEquals(
						"com.ctrip.platform.dal.dao.datasource.DataSourceValidator",
						pc.getValidatorClassName());

				DataSourceConfigure result = null;

				result = provider
						.getDataSourceConfigure("CorpPerformanceManagementDB_W");
				Assert.assertNotNull(result);

				result = provider.getDataSourceConfigure("commonOrderShard1DB_S1_S");
				Assert.assertNotNull(result);

			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}

		// case8,配置env=fat环境，QConfig
		// APP级别配置removeAbandonedTimeout=100,datasource级别和datasource.xml配置YouSearchDB
		@Test
		public void testQConfigCase8() {
			TitanProvider provider = new TitanProvider();
			Set<String> dbNames = new HashSet<>();
			dbNames.add("CorpPerformanceManagementDB_W");
			dbNames.add("YouSearchDB");
			Map<String, String> settings = new HashMap<>();
//			settings.put(TitanProvider.SERVICE_ADDRESS,
//					"https://ws.titan.uat.qa.nt.ctripcorp.com/titanservice/query");
//			settings.put(TitanProvider.USE_LOCAL_CONFIG, "false");
			try {
				provider.initialize(settings);
				provider.setup(dbNames);

				PoolProperties pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("CorpPerformanceManagementDB_W"));

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
				assertEquals(70, pc.getRemoveAbandonedTimeout());
				assertTrue(pc.isRemoveAbandoned());
				assertTrue(pc.isLogAbandoned());
				assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
				assertEquals(
						"sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8;useSSL=false;socketTimeout=100000;connectTimeout=1050;loginTimeout=2",
						pc.getConnectionProperties());
				// assertEquals("set names utf8mb4",pc.getInitSQL());
				assertEquals(
						"com.ctrip.platform.dal.dao.datasource.DataSourceValidator",
						pc.getValidatorClassName());

				pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("YouSearchDB"));

				assertTrue(pc.isTestWhileIdle());
				assertTrue(pc.isTestOnBorrow());
				assertTrue(pc.isTestOnReturn());
				assertEquals("SELECT 100", pc.getValidationQuery());
				assertEquals(31000, pc.getValidationInterval());
				assertEquals(800, pc.getTimeBetweenEvictionRunsMillis());
				assertEquals(110, pc.getMaxActive());
				assertEquals(70, pc.getMinIdle());
				assertEquals(11000, pc.getMaxWait());
				assertEquals(31000, pc.getMaxAge());
				assertEquals(5, pc.getInitialSize());
				assertEquals(70, pc.getRemoveAbandonedTimeout());
				assertFalse(pc.isRemoveAbandoned());
				assertFalse(pc.isLogAbandoned());
				assertEquals(31000, pc.getMinEvictableIdleTimeMillis());
				assertEquals("rewriteBatchedStatements=true",
						pc.getConnectionProperties());
				assertEquals("set names utf8mb4", pc.getInitSQL());
				assertEquals(
						"com.ctrip.platform.dal.dao.datasource.DataSourceValidator",
						pc.getValidatorClassName());

				DataSourceConfigure result = null;

				result = provider
						.getDataSourceConfigure("CorpPerformanceManagementDB_W");
				Assert.assertNotNull(result);

				result = provider.getDataSourceConfigure("YouSearchDB");
				Assert.assertNotNull(result);

			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}


	@Test
	public void testOptionAndConnectionProperties() {
		TitanProvider provider = new TitanProvider();
		Set<String> dbNames = new HashSet<>();
		dbNames.add("abtestdb");
		dbNames.add("commonOrderShard2DB_S2_S");
		dbNames.add("uiautomationtestdb_W");
		dbNames.add("CommonOrderDB_S6_R");
		Map<String, String> settings = new HashMap<>();
		try {
			provider.initialize(settings);
			provider.setup(dbNames);

			PoolProperties pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("abtestdb"));

			assertEquals(
					"rewriteBatchedStatements=true",
					pc.getConnectionProperties());


			pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("commonOrderShard2DB_S2_S"));

			assertEquals("rewriteBatchedStatements=true",
					pc.getConnectionProperties());

			pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("uiautomationtestdb_W"));

			assertEquals("rewriteBatchedStatements=true",
					pc.getConnectionProperties());

			pc = PoolPropertiesHelper.getInstance().convert(provider.getDataSourceConfigure("CommonOrderDB_S6_R"));

			assertEquals("rewriteBatchedStatements=true",
					pc.getConnectionProperties());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
