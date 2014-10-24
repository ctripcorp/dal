package com.ctrip.platform.dal.dao.markdown;

public class TimeBucketCounter {
	private long duration;
	private long bucketInterval;
	private long bucketStart;
	private long[] counters = new long[2];
	
	public TimeBucketCounter(long duration, long start) {
		this.duration = duration;
		bucketInterval = duration/2;
		bucketStart = start;
	}
	
	public void increase() {
		checkBucket();
		counters[0]++;
	}
	
	public long getCount() {
		checkBucket();
		return counters[0] + counters[1];
	}
	
	private void checkBucket() {
		long now = System.currentTimeMillis();
		long timePassed = now - bucketStart;	
		//[0][*1][2][@3] Restart counting
		if(timePassed > duration) {
			counters[0] = 0;
			counters[1] = 0;
			bucketStart = now;
			return;
		}
		
		//[0][*1][@2][3] Moving bucket forward
		if(timePassed > bucketInterval) {
			counters[1] = counters[0];
			counters[0] = 0;
			bucketStart += bucketInterval;
			return;
		}
		// Otherwise, remain here
	}
}
