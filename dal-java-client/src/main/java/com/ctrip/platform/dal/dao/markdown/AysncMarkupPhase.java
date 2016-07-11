package com.ctrip.platform.dal.dao.markdown;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.ctrip.platform.dal.dao.status.DalStatusManager;

public class AysncMarkupPhase {
	
	private static Random random = new Random();
	
	private String name;
	private AtomicInteger phaseIndex = new AtomicInteger();
	private AtomicInteger totalCount = new AtomicInteger();
	private AtomicInteger passedCount = new AtomicInteger();
	private AtomicInteger rollbackCount = new AtomicInteger();
	
	public AysncMarkupPhase(String name){
		this.name = name;
	}
	
	public boolean isPassed(){
		this.totalCount.incrementAndGet();
		boolean passed = true;
		int index = this.getPhaseIndex();
		int[] schedule = DalStatusManager.getMarkdownStatus().getMarkUpSchedule();
		if(index <= schedule.length - 1 && random.nextInt(100)<= schedule[this.phaseIndex.get()] * 10){
			passed = true;
			this.passedCount.incrementAndGet();
		}else{
			passed = false;
		}
		return passed;
	}
	
	public synchronized void resetConter(){
//		System.out.println("##########################################" + (this.passedCount.get() + 0.0f)/this.totalCount.get());
		this.totalCount.set(0);
		this.passedCount.set(0);
		this.rollbackCount.set(0);
	}
	
	public void setPhaseIndex(int index){
		this.phaseIndex.set(index);
	}
	
	public int getPhaseIndex(){
		return this.phaseIndex.get();
	}
	
	public int getTotalCount(){
		return this.totalCount.get();
	}
	
	public int getPassed(){
		return this.passedCount.get() - this.rollbackCount.get();
	}
	
	public int getRollbackCount(){
		return this.rollbackCount.get();
	}
	
	public String getName(){
		return this.name;
	}
	
	public void rollback(){
		this.rollbackCount.incrementAndGet();
	}
}
