package test.com.ctrip.platform.dal.dao.unittests;

import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;

public class DalClientFactoryTest {

	@Test
	public void testInitClientFactory() {
		try {
			//Check laze load
			DalClientFactory.getDalConfigure();
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			e.printStackTrace();
			fail();	
		}
	}

	@Test
	public void testInitClientFactoryString() {
//		fail("Not yet implemented");
	}

	@Test
	public void testWarmUpConnections() {
		try {
			DalClientFactory.warmUpConnections();
		} catch (Throwable e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testGetClient() {
		Set<String> names = new HashSet<>();
//		names.add("MultiThreadingTest");
		names.add("dao_test");
		names.add("HA_Test_0");
		names.add("HA_Test");
		names.add("HA_Test_1");
		names.add("dao_test_sqlsvr");
		names.add("dao_test_sqlsvr1");
		names.add("dao_test_mysql");
		names.add("dao_test_mysql1");
		names.add("dao_test_mod");
		names.add("dao_test_mod_mysql");
		names.add("dao_test_simple");
		names.add("DAL_TEST");
		
		for(String name: names)
			Assert.assertNotNull(DalClientFactory.getClient(name));
	}

	@Test
	public void testGetDalConfigure() {
		Assert.assertNotNull(DalClientFactory.getDalConfigure());
	}

	@Test
	public void testGetDalLogger() {
		Assert.assertNotNull(DalClientFactory.getDalLogger());
	}

	@Test
	public void testGetTaskFactory() {
		Assert.assertNotNull(DalClientFactory.getTaskFactory());
	}

	@Test
	public void testShutdownFactory() {
		try {
			DalClientFactory.getDalConfigure();
			DalClientFactory.shutdownFactory();
			DalClientFactory.shutdownFactory();
			DalClientFactory.shutdownFactory();
		} catch (Throwable e) {
			Assert.fail();
		}
	}
}
