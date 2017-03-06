package test.com.ctrip.platform.dal.dao.unittests;

import java.util.concurrent.CountDownLatch;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.status.DalStatusManager;

public class DalStatusManagerTest {
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalStatusManager.initialize(DalClientFactory.getDalConfigure());
	}
	
	@Test
	public void testInitialize() throws Exception{
		DalStatusManager.initialize(DalClientFactory.getDalConfigure());
		DalStatusManager.initialize(DalClientFactory.getDalConfigure());
		DalStatusManager.initialize(DalClientFactory.getDalConfigure());
	}
	
	@Test
	public void testShutdown() throws Exception{
		DalStatusManager.shutdown();
		DalStatusManager.shutdown();
		DalStatusManager.shutdown();
	}
	
	@Test
	public void testParaInitShutdown(){
		final CountDownLatch cdl = new CountDownLatch(10);
		for(int i=0; i < 10; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						for(int i=0; i < 100; i++) {
							DalStatusManager.initialize(DalClientFactory.getDalConfigure());
							DalStatusManager.shutdown();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Assert.fail();
					}
					cdl.countDown();
				}
			}).run();
		}
		try {
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
