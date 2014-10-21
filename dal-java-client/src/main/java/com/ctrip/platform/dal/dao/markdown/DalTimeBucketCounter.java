package com.ctrip.platform.dal.dao.markdown;



/**
 * This class is not thread safe, caller should make sure it will only be referred by one thread per time.
 * @author jhhe
 */
public class DalTimeBucketCounter {
	private long duration;
	private long bucketInterval;
	private long bucketStart;
	private long[] counters = new long[2]; 
	
	public DalTimeBucketCounter(long duration) {
		this.duration = duration;
		bucketInterval = duration/2;
		bucketStart = System.currentTimeMillis();
	}
	
	public void increase() {
		checkBucket();
		counters[0]++;
	}
	
	public void increasedBy(long delta) {
		checkBucket();
		counters[0] += delta;
	}
	
	public long getCount() {
		return counters[0] + counters[1];
	}
	
	public long[] getDetail() {
		return counters;
	}
	
	private void checkBucket() {
		long now = System.currentTimeMillis();
		long timePassed = now - bucketStart;
		
		// Restart counting
		if(timePassed > duration) {
			counters[0] = 0;
			counters[1] = 0;
			bucketStart = now;
		}else //Moving bucket forward
		if(timePassed > bucketInterval) {
			counters[1] = counters[0];
			counters[0] = 0;
			bucketStart += bucketInterval;
		}// Otherwise, remain here
	}
	
	public static void main(String[] args) {
		int c;
		DalTimeBucketCounter counter = new DalTimeBucketCounter(1000 * 10);
		
		try {
			while(true) {
				Thread.sleep(700);
				counter.increase();
				long[] detail = counter.getDetail();
				System.out.println(counter.getCount() + ": [" + detail[0] + "][" + detail[1] + "]");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
