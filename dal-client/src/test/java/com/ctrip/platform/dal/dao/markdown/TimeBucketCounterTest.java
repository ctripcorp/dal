package com.ctrip.platform.dal.dao.markdown;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class TimeBucketCounterTest {
	@Test
	public void testIncrease() {
		try {
			// 0
			TimeBucketCounter test = new TimeBucketCounter(1000, System.currentTimeMillis());
			assertEquals(0, test.getCount());
			Thread.sleep(200);
			// 200 [3][0]
			test.increase();
			test.increase();
			test.increase();
			assertEquals(3, test.getCount());
			Thread.sleep(400);
			// 600 [0][3]
			assertEquals(3, test.getCount());
			test.increase();
			test.increase();
			test.increase();
			// 600 [3][3]
			assertEquals(6, test.getCount());
			Thread.sleep(410);
			// 1000 [0][3]
			assertEquals(3, test.getCount());
			Thread.sleep(501);
			// 1500 [0][0]
			assertEquals(0, test.getCount());
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}
}
