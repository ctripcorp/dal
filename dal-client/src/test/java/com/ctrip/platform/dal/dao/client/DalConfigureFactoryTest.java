package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.dao.configure.ClusterDatabaseSet;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DalConfigureFactory;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import org.junit.Test;

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

}
