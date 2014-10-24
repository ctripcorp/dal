package com.ctrip.platform.dal.dao.markdown;

public class DetectorCounter {
	private TimeBucketCounter request;
	private TimeBucketCounter hints;
	private long duration;
	
	public DetectorCounter(long duration){
		this.duration = duration;
		this.request = new TimeBucketCounter(duration);
		this.hints = new TimeBucketCounter(duration);
	}
	
	public void incrementRequest(){
		this.request.increase();
	}
	
	public void incrementHints(){
		this.hints.increase();
	}
	
	public long getRequestTimes(){
		return this.request.getCount();
	}
	
	public long getHints(){
		return this.hints.getCount();
	}
	
	public long getDuration(){
		return this.duration;
	}
}
