package com.ctrip.platform.dal.dao.markdown;

public class MarkupPhase {
	public static final int length = 10;
	
	private int[] schedules = null;
	private int index = 0;
	private int total = 0;
	
	public MarkupPhase(int schedule){
		this.schedules = new int[length];
		for (int i = 0; i < length; i++) {
			schedules[i] = i >= length - schedule ? 1 : 0;
		}
	}
	
	public boolean isQualified(){
		this.total ++;
		boolean pass = this.schedules[this.index] > 0;
		if(++ index >= length){
			this.index = 0;
		}
		return pass;
	}
	
	public int getTotal(){
		return this.total;
	}
}
