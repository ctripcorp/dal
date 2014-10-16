package com.ctrip.platform.dal.dao.markdown;

import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;

public class Markup {
	private String name;
	private int loop = 0;
	private MarkupLooper indexer = null;

	public Markup(String name){
		this.name = name;
		this.init();
	}
	
	public synchronized boolean isPass() {
		int[] schedules = ConfigBeanFactory.getMarkdownConfigBean()
				.getAutoMarkUpSchedule();
		int autoMarkupCount = ConfigBeanFactory.getMarkdownConfigBean()
				.getAutoMarkupBatches() * 10;
		if (this.indexer.getTotal() == autoMarkupCount) {
			if (this.loop == schedules.length) {
				ConfigBeanFactory.getMarkdownConfigBean().markup(this.name);
				this.init();
				return true;
			}
			this.indexer = new MarkupLooper(schedules[this.loop]);
			this.loop ++;
		}
		boolean pass = this.indexer.isSchedule();
		return pass;
	}
	
	private void init(){
		int[] schedules = ConfigBeanFactory.getMarkdownConfigBean()
				.getAutoMarkUpSchedule();
		this.loop = 1;
		this.indexer = new MarkupLooper(schedules[0]);
	}

	public synchronized void rollback() {
		int[] schedules = ConfigBeanFactory.getMarkdownConfigBean()
				.getAutoMarkUpSchedule();
		if(this.loop > 1){
			this.loop = this.loop - 1;
		}
		this.indexer = new MarkupLooper(schedules[this.loop]);
	}
	

	public String toString() {
		return String.format("total:%s--loop:%s", this.indexer.getTotal(), this.loop);
	}
}
