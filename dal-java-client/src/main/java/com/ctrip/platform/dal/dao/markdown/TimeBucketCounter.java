package com.ctrip.platform.dal.dao.markdown;

public class TimeBucketCounter {
	private long duration;
	private long bucketInterval;
	private long bucketStart;
	private long count;
	private long[] counters = new long[2];
	
	public TimeBucketCounter(long duration) {
		this.duration = duration;
		bucketInterval = duration/2;
		bucketStart = -1;
	}
	
	public void increase() {
		checkBucket();
		counters[0]++;
		this.count = counters[0] + counters[1];
	}
	
	public long getCount() {
		checkBucket();
		return this.count;
	}
	
	private void checkBucket() {
		long now = System.currentTimeMillis();
		if(bucketStart < 0){
			bucketStart = now;
			return;
		}
		long timePassed = now - bucketStart;
		
		//[0][1][2][@3] Restart counting
		if(timePassed > duration + bucketInterval) {
			counters[0] = 0;
			counters[1] = 0;
			bucketStart = now;
		}else //[0][1][@2][3] Moving bucket forward
		if(timePassed > duration) {
			counters[1] = counters[0];
			counters[0] = 0;
			bucketStart += bucketInterval;
		}else //[0][@1][2][3] Moving bucket forward
		if(timePassed > bucketInterval) {
			counters[1] = counters[0];
			counters[0] = 0;
		}// Otherwise, remain here
	}
}
