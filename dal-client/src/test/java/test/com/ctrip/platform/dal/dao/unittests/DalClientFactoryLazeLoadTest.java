package test.com.ctrip.platform.dal.dao.unittests;

import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;

public class DalClientFactoryLazeLoadTest {
	@Test
	public void testLazeLoad(){
		Assert.assertNotNull(DalClientFactory.getClient("dao_test"));
	}

	@Test
	public void testIitShutdown(){
		final CountDownLatch cdl = new CountDownLatch(10);
		for(int i=0; i < 10; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						for(int i=0; i < 100; i++) {
							DalClientFactory.initClientFactory();
							DalClientFactory.initClientFactory();
							DalClientFactory.shutdownFactory();
							DalClientFactory.shutdownFactory();
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
