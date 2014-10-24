package com.ctrip.platform.dal.dao.markdown;

public class TimeBucketCounter {
	private long duration;
	private long bucketInterval;
	private long bucketStart;
	private long[] counters = new long[2];
	
	public TimeBucketCounter(long duration) {
		this.duration = duration;
		bucketInterval = duration/2;
		bucketStart = -1;
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
		if(bucketStart < 0){
			bucketStart = now;
			return;
		}
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
	
	public static void main(String[] args) {
		final TimeBucketCounter c = new TimeBucketCounter(1000 * 10);
		try{
			new Thread(){
				public void run() {
					int timer = 0;
					while(true) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println(String.format("%d -- %d [%d][%d]", timer++, c.getCount(), c.counters[0], c.counters[1]));
					}
				}
			}.start();
			
			int count = 0;
			boolean start = true;
			while(true) {
				Thread.sleep(1000);
				if(start)
					c.increase();
				if(count++%12 == 0)
					start=!start;
			}
		}catch(Throwable e)
		{
			
		}
	}
}
