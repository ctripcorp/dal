package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.task.DalRequestExecutor;
import com.ctrip.platform.dal.dao.task.DalRequestExecutorUtils;
import com.ctrip.platform.dal.dao.task.DalThreadPoolExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutorService;

import static org.junit.Assert.*;

public class DalConfigureFactoryTest {

	@Test
	public void testLoad() {
		// Test load from dal.xml
		DalConfigure configure = null;
		try {
			configure = DalConfigureFactory.load();
			assertNotNull(configure);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		DatabaseSet databaseSet = configure.getDatabaseSet("clusterName1");
		assertTrue(databaseSet instanceof ClusterDatabaseSet);
		assertEquals("clusterName1".toLowerCase(), ((ClusterDatabaseSet) databaseSet).getCluster().getClusterName());
		try {
			configure.getDatabaseSet("clusterName1".toLowerCase());
			fail();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			configure.getDatabaseSet("clusterName2");
			fail();
		} catch (Exception e) {
			e.printStackTrace();
		}
		databaseSet = configure.getDatabaseSet("DbSetName");
		assertTrue(databaseSet instanceof ClusterDatabaseSet);
		assertEquals("clusterName2".toLowerCase(), ((ClusterDatabaseSet) databaseSet).getCluster().getClusterName());
		try {
			configure.getDatabaseSet("DbSetName".toLowerCase());
			fail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testThreadPoolConfig() throws Exception {
		DalRequestExecutor.shutdown();
		DalConfigure configure = DalConfigureFactory.load(Thread.currentThread().getContextClassLoader().
				getResource("dal-thread-pool-test.xml"));
		DalRequestExecutor.init(configure);
		ExecutorService executor = DalRequestExecutorUtils.getExecutor();
		Assert.assertTrue(executor instanceof DalThreadPoolExecutor);
		DalThreadPoolExecutorConfig executorConfig = DalRequestExecutorUtils.getExecutorConfig((DalThreadPoolExecutor) executor);
		Assert.assertEquals(5, executorConfig.getMaxThreadsPerShard("dao_test_mod_mysql"));
		Assert.assertEquals(0, executorConfig.getMaxThreadsPerShard("clusterName1"));
		Assert.assertEquals(0, executorConfig.getMaxThreadsPerShard("clusterName2"));
		Assert.assertEquals(3, executorConfig.getMaxThreadsPerShard("DbSetName"));
		DalRequestExecutor.shutdown();
	}

}
