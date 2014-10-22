package com.ctrip.platform.dal.dao.markdown;

import junit.framework.Assert;

import org.junit.Test;

public class DetectorCounterTest {

	@Test
	public void countCorrectTest() {
		DetectorCounter counter = new DetectorCounter(1000);
		for (int i = 0; i < 10; i++) {
			if(i % 2 == 0)
				counter.incrementHints();
			counter.incrementRequest();
		}
		
		Assert.assertEquals(5, counter.getHints());
		Assert.assertEquals(10, counter.getRequestTimes());
	}
	
	@Test
	public void countOverdueTest() throws InterruptedException{
		DetectorCounter counter = new DetectorCounter(1000);
		for (int i = 0; i < 10; i++) {
			if(i == 5){
				Thread.sleep(510);
			}
			if(i == 9){
				Thread.sleep(510);
			}
			if(i % 2 == 0)
				counter.incrementHints();
			counter.incrementRequest();
		}
		
		Assert.assertEquals(2, counter.getHints());
		Assert.assertEquals(5, counter.getRequestTimes());
	}
	
	@Test
	public void countAllOverdueTest() throws InterruptedException{
		DetectorCounter counter = new DetectorCounter(1000);
		for (int i = 0; i < 10; i++) {
			if(i % 2 == 0)
				counter.incrementHints();
			counter.incrementRequest();
		}
		
		Thread.sleep(1001);
		
		Assert.assertEquals(0, counter.getHints());
		Assert.assertEquals(0, counter.getRequestTimes());
	}
}
