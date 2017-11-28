package noShardTest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import static org.junit.Assert.*;
import org.junit.Test;


import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.CtripTaskFactory;
import com.ctrip.platform.dal.dao.configure.DatabasePoolConfigParser;

public class QConfigTest1 {
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



			PoolProperties pc = DatabasePoolConfigParser.getInstance()
					.getDatabasePoolConifg("DalServiceDB")
					.getPoolProperties();
			assertFalse(pc.isTestWhileIdle());
			assertTrue(pc.isTestOnBorrow());
			assertFalse(pc.isTestOnReturn());
			assertEquals("SELECT 1", pc.getValidationQuery());
			assertEquals(30000, pc.getValidationInterval());
			assertEquals(5000, pc.getTimeBetweenEvictionRunsMillis());
			assertEquals(100, pc.getMaxActive());
			assertEquals(0, pc.getMinIdle());
			assertEquals(10000, pc.getMaxWait());
			assertEquals(28000000, pc.getMaxAge());
			assertEquals(1, pc.getInitialSize());
			assertEquals(65, pc.getRemoveAbandonedTimeout());
			assertTrue(pc.isRemoveAbandoned());
			assertFalse(pc.isLogAbandoned());
			assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
			assertEquals(
					"sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8",
					pc.getConnectionProperties());
			// assertEquals("set names utf8mb4",pc.getInitSQL());
			assertEquals(
					"com.ctrip.platform.dal.dao.datasource.DataSourceValidator",
					pc.getValidatorClassName());
            assertEquals(5, pc.getValidationQueryTimeout());

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

			PoolProperties pc = DatabasePoolConfigParser.getInstance()
					.getDatabasePoolConifg("uiautomationtestdb_W")
					.getPoolProperties();
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

			pc = DatabasePoolConfigParser.getInstance()
					.getDatabasePoolConifg("DalServiceDB")
					.getPoolProperties();
			assertFalse(pc.isTestWhileIdle());
			assertTrue(pc.isTestOnBorrow());
			assertFalse(pc.isTestOnReturn());
			assertEquals("SELECT 1", pc.getValidationQuery());
			assertEquals(30000, pc.getValidationInterval());
			assertEquals(5000, pc.getTimeBetweenEvictionRunsMillis());
			assertEquals(100, pc.getMaxActive());
			assertEquals(0, pc.getMinIdle());
			assertEquals(10000, pc.getMaxWait());
			assertEquals(28000000, pc.getMaxAge());
			assertEquals(1, pc.getInitialSize());
			assertEquals(65, pc.getRemoveAbandonedTimeout());
			assertTrue(pc.isRemoveAbandoned());
			assertFalse(pc.isLogAbandoned());
			assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
			assertEquals(
					"sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8",
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
	public void testQonfigCase3() {
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

			PoolProperties pc = DatabasePoolConfigParser.getInstance()
					.getDatabasePoolConifg("CruiseBookingDB")
					.getPoolProperties();
			assertFalse(pc.isTestWhileIdle());
			assertTrue(pc.isTestOnBorrow());
			assertFalse(pc.isTestOnReturn());
			assertEquals("SELECT 1", pc.getValidationQuery());
			assertEquals(30000, pc.getValidationInterval());
			assertEquals(10, pc.getValidationQueryTimeout());
			assertEquals(5000, pc.getTimeBetweenEvictionRunsMillis());
			assertEquals(100, pc.getMaxActive());
			assertEquals(10, pc.getMinIdle());
			assertEquals(10000, pc.getMaxWait());
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

			pc = DatabasePoolConfigParser.getInstance()
					.getDatabasePoolConifg("DalServiceDB")
					.getPoolProperties();
			assertFalse(pc.isTestWhileIdle());
			assertTrue(pc.isTestOnBorrow());
			assertFalse(pc.isTestOnReturn());
			assertEquals("SELECT 1", pc.getValidationQuery());
			assertEquals(30000, pc.getValidationInterval());
			assertEquals(5000, pc.getTimeBetweenEvictionRunsMillis());
			assertEquals(100, pc.getMaxActive());
			assertEquals(0, pc.getMinIdle());
			assertEquals(10000, pc.getMaxWait());
			assertEquals(28000000, pc.getMaxAge());
			assertEquals(1, pc.getInitialSize());
			assertEquals(65, pc.getRemoveAbandonedTimeout());
			assertTrue(pc.isRemoveAbandoned());
			assertFalse(pc.isLogAbandoned());
			assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
			assertEquals(
					"sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8",
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
	public void testQconfigCase4() {
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

			PoolProperties pc = DatabasePoolConfigParser.getInstance()
					.getDatabasePoolConifg("ctripoaDB_W")
					.getPoolProperties();
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

			pc = DatabasePoolConfigParser.getInstance()
					.getDatabasePoolConifg("DalServiceDB")
					.getPoolProperties();
			assertFalse(pc.isTestWhileIdle());
			assertTrue(pc.isTestOnBorrow());
			assertFalse(pc.isTestOnReturn());
			assertEquals("SELECT 1", pc.getValidationQuery());
			assertEquals(30000, pc.getValidationInterval());
			assertEquals(5000, pc.getTimeBetweenEvictionRunsMillis());
			assertEquals(100, pc.getMaxActive());
			assertEquals(0, pc.getMinIdle());
			assertEquals(10000, pc.getMaxWait());
			assertEquals(28000000, pc.getMaxAge());
			assertEquals(1, pc.getInitialSize());
			assertEquals(65, pc.getRemoveAbandonedTimeout());
			assertTrue(pc.isRemoveAbandoned());
			assertFalse(pc.isLogAbandoned());
			assertEquals(30000, pc.getMinEvictableIdleTimeMillis());
			assertEquals(
					"sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8",
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
