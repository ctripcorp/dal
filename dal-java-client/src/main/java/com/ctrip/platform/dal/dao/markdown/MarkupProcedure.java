package com.ctrip.platform.dal.dao.markdown;

import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;
import com.ctrip.platform.dal.logging.markdup.MarkupInfo;
import com.ctrip.platform.dal.sql.logging.Metrics;

public class MarkupProcedure {
	private String name;
	private int nextPhaseIndex = 0;
	private int qualifies = 0;
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
		
		if(ConfigBeanFactory.getMarkdownConfigBean()
				.getAutoMarkupBatches() <= 0){
			return this.autoMarkup();
		}
		
		if (this.phase.getTotal() >= autoMarkupCount) {
			if (this.nextPhaseIndex == scheduleTemplate.length) {
				return this.autoMarkup();
			}
			this.phase = new MarkupPhase(scheduleTemplate[this.nextPhaseIndex]);
			this.nextPhaseIndex ++;
		}
		boolean pass = phase.isQualified();
		this.qualifies = pass ? this.qualifies + 1 : this.qualifies;
		return pass;
	}
	
	private boolean autoMarkup(){
		ConfigBeanFactory.getMarkdownConfigBean().markup(this.name);
		MarkupInfo marticsInfo = new MarkupInfo(this.name);
		Metrics.report(marticsInfo, this.qualifies);
		this.init();
		return true;
	}
	
	private void init(){
		int[] schedules = ConfigBeanFactory.getMarkdownConfigBean()
				.getAutoMarkUpSchedule();
		this.nextPhaseIndex = 1;
		this.phase = new MarkupPhase(schedules[0]);
	}

	public synchronized void rollback() {
		if(this.qualifies > 1){
			this.qualifies --;
		}
		int[] schedules = ConfigBeanFactory.getMarkdownConfigBean()
				.getAutoMarkUpSchedule();
		if(this.nextPhaseIndex >= 1){
			this.phase = new MarkupPhase(schedules[this.nextPhaseIndex - 1]);
		}
		if(this.nextPhaseIndex > 1){
			this.nextPhaseIndex = this.nextPhaseIndex - 1;
		}
	}
	
	public int getNextPhaseIndex(){
		return this.nextPhaseIndex;
	}
	
	public MarkupPhase getCurrentPhase(){
		return this.phase;
	}
	
	public String toString() {
		return String.format("total:%s--loop:%s", this.phase.getTotal(), this.nextPhaseIndex);
	}
}
