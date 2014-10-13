package com.ctrip.platform.dal.dao.markdown;

public class Indexer {
	private static final int length = 10;
	private int[] schedules = null;
	private int index = 0;
	public Indexer(int schedule){
		this.schedules = new int[length];
		for (int i = 0; i < length; i++) {
			schedules[i] = i >= length - schedule ? 1 : 0;
		}
	}
	
	public boolean isSchedule(){
		boolean pass = this.schedules[this.index] > 0;
		if(++ index >= length){
			this.index = 0;
		}
		return pass;
	}
}
