package com.ctrip.platform.dal.dao.markdown;

public class DetectorCounter {
	private TimeBucketCounter request;
	private TimeBucketCounter errors;
	private long duration;
	private long start;
	
	public DetectorCounter(long duration){
		this.duration = duration;
		this.reset(duration);
	}
	
	public void incrementRequest(){
		this.request.increase();
	}
	
	public void incrementErrors(){
		this.errors.increase();
	}
	
	public long getRequestTimes(){
		return this.request.getCount();
	}
	
	public long getErrors(){
		return this.errors.getCount();
	}
	
	public long getDuration(){
		return this.duration;
	}
	
	public long getStart(){
		return this.start;
	}
	
	public void reset(long duration){
		this.duration = duration;
		this.reset();
	}
	
	public void reset(){
		this.start = System.currentTimeMillis();
		this.request = new TimeBucketCounter(duration, start);
		this.errors = new TimeBucketCounter(duration, start);
	}
}
