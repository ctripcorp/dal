package noShardTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.helper.PoolPropertiesHelper;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DatabasePoolConfigParser;

public class QConfigTest2 {
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
				assertEquals(10000, pc.getMaxWait());
				assertEquals(28000000, pc.getMaxAge());
				assertEquals(1, pc.getInitialSize());
				assertEquals(70, pc.getRemoveAbandonedTimeout());
				assertTrue(pc.isRemoveAbandoned());
				assertTrue(pc.isLogAbandoned());
				assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
				assertEquals(
						"sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8;useSSL=false;socketTimeout=100000",
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
				assertEquals(10000, pc.getMaxWait());
				assertEquals(28000000, pc.getMaxAge());
				assertEquals(1, pc.getInitialSize());
				assertEquals(70, pc.getRemoveAbandonedTimeout());
				assertTrue(pc.isRemoveAbandoned());
				assertTrue(pc.isLogAbandoned());
				assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
				assertEquals(
						"sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8;useSSL=false;socketTimeout=100000",
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
				assertEquals(6,pc.getValidationQueryTimeout());
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
				assertEquals(10000, pc.getMaxWait());
				assertEquals(28000000, pc.getMaxAge());
				assertEquals(1, pc.getInitialSize());
				assertEquals(70, pc.getRemoveAbandonedTimeout());
				assertTrue(pc.isRemoveAbandoned());
				assertTrue(pc.isLogAbandoned());
				assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
				assertEquals(
						"sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8;useSSL=false;socketTimeout=100000",
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
				assertEquals(10000, pc.getMaxWait());
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
		public void testQonfigCase8() {
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
				assertEquals(10000, pc.getMaxWait());
				assertEquals(28000000, pc.getMaxAge());
				assertEquals(1, pc.getInitialSize());
				assertEquals(70, pc.getRemoveAbandonedTimeout());
				assertTrue(pc.isRemoveAbandoned());
				assertTrue(pc.isLogAbandoned());
				assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
				assertEquals(
						"sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8;useSSL=false;socketTimeout=100000",
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
