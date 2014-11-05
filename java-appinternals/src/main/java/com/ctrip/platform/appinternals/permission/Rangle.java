package com.ctrip.platform.appinternals.permission;

public class Rangle {
	private long minNum;
    private long maxNum;
    
    public Rangle(long min, long max){
    	this.minNum = min;
    	this.maxNum = max;
    }
    
    public long getMinNum(){
    	return this.minNum;
    }
    
    public long getMaxNum(){
    	return this.maxNum;
    }
}
