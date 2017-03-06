package test.com.ctrip.platform.dal.dao.markdown;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.platform.dal.dao.markdown.DetectorCounter;

public class DetectorCounterTest {

	@Test
	public void countCorrectTest() {
		DetectorCounter counter = new DetectorCounter(1000);
		for (int i = 0; i < 10; i++) {
			if(i % 2 == 0){
				counter.incrementErrors();
			}
			counter.incrementRequest();
		}
		
		Assert.assertEquals(5, counter.getErrors());
		Assert.assertEquals(10, counter.getRequestTimes());
	}
	
	@Test
	public void countOverdueTest() throws InterruptedException{
		DetectorCounter counter = new DetectorCounter(1000);
		for (int i = 0; i < 10; i++) {
			if(i == 5){
				Thread.sleep(510);
			}
			if(i == 8){
				Thread.sleep(510);
			}
			if(i % 2 == 0)
				counter.incrementErrors();
			counter.incrementRequest();
		}
		
		Assert.assertEquals(2, counter.getErrors()); //([8],[6])
		Assert.assertEquals(5, counter.getRequestTimes());//([8,9],[5,6,7])
	}
	
	@Test
	public void countAllOverdueTest() throws InterruptedException{
		DetectorCounter counter = new DetectorCounter(1000);
		for (int i = 0; i < 10; i++) {
			if(i % 2 == 0)
				counter.incrementErrors();
			counter.incrementRequest();
		}
		
		Thread.sleep(1100);
		
		Assert.assertEquals(0, counter.getErrors());
		Assert.assertEquals(0, counter.getRequestTimes());
		
	}
}
