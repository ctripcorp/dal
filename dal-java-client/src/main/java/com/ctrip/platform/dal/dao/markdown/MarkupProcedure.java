package com.ctrip.platform.dal.dao.markdown;

import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;

public class MarkupProcedure {
	private String name;
	private int nextPhaseIndex = 0;
	private MarkupPhase phase = null;

	public MarkupProcedure(String name){
		this.name = name;
		this.init();
	}
	
	public synchronized boolean isPass() {
		int[] scheduleTemplate = ConfigBeanFactory.getMarkdownConfigBean()
				.getAutoMarkUpSchedule();
		
		int autoMarkupCount = ConfigBeanFactory.getMarkdownConfigBean()
				.getAutoMarkupBatches() * MarkupPhase.length;
		
		if (this.phase.getTotal() == autoMarkupCount) {
			if (this.nextPhaseIndex == scheduleTemplate.length) {
				ConfigBeanFactory.getMarkdownConfigBean().markup(this.name);
				this.init();
				return true;
			}
			this.phase = new MarkupPhase(scheduleTemplate[this.nextPhaseIndex]);
			this.nextPhaseIndex ++;
		}
		return phase.isQualified();
	}
	
	private void init(){
		int[] schedules = ConfigBeanFactory.getMarkdownConfigBean()
				.getAutoMarkUpSchedule();
		this.nextPhaseIndex = 1;
		this.phase = new MarkupPhase(schedules[0]);
	}

	public synchronized void rollback() {
		int[] schedules = ConfigBeanFactory.getMarkdownConfigBean()
				.getAutoMarkUpSchedule();
		if(this.nextPhaseIndex > 1){
			this.nextPhaseIndex = this.nextPhaseIndex - 1;
		}
		this.phase = new MarkupPhase(schedules[this.nextPhaseIndex]);
	}
	

	public String toString() {
		return String.format("total:%s--loop:%s", this.phase.getTotal(), this.nextPhaseIndex);
	}
}
