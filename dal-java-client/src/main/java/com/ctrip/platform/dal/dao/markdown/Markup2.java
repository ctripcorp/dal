package com.ctrip.platform.dal.dao.markdown;

import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;

public class Markup2 {
	private String name;
	private int passCount = 0;
	private int failCount = 0;
	private int totalCount = 0;
	private int loop = 0;
	private Indexer indexer = null;

	public Markup2(String name){
		this.name = name;
	}
	
	public synchronized boolean isPass() {
		int[] schedules = ConfigBeanFactory.getMarkdownConfigBean()
				.getAutoMarkUpSchedule();
		if (this.moveToNext()) {
			this.indexer = new Indexer(schedules[this.loop++]);
			if (this.loop == schedules.length) {
				ConfigBeanFactory.getMarkdownConfigBean().markup(this.name);
				this.reset();
			}
		}
		boolean pass = this.indexer.isSchedule();
		passCount = pass ? passCount + 1 : passCount;
		this.totalCount++;
		return pass;
	}

	private boolean moveToNext() {
		if (this.totalCount == 0)
			return true;
		
		if(this.totalCount
				% ConfigBeanFactory.getMarkdownConfigBean()
				.getAutoMarkupCount() == 0){
			if(this.passCount
					* ConfigBeanFactory.getMarkdownConfigBean()
					.getAutoMarkupFailureThreshold() >= this.failCount){
				return true;
			}else{
				this.reset();
				return true;
			}
		}
		return false;
	}
	
	private void reset(){
		this.failCount = 0;
		this.passCount= 0;
		this.totalCount = 0;
		this.loop = 0;
		this.indexer = null;
	}

	public synchronized void rollback() {
		this.failCount++;
	}

	public String toString() {
		return String.format("total:%s--pass:%s--fail:%s--loop:%s",
				this.totalCount, this.passCount, this.failCount, this.loop);
	}
}
