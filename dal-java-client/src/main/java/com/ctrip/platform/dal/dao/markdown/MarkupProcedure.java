package com.ctrip.platform.dal.dao.markdown;

import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;
import com.ctrip.platform.dal.dao.configbeans.MarkdownConfigBean;

public class MarkupProcedure {
	private DalLogger logger;
	private String name;
	private int nextPhaseIndex = 0;
	private int qualifies = 0;
	private MarkupPhase phase = null;

	public MarkupProcedure(String name, DalLogger logger){
		this.name = name;
		this.logger = logger;
		this.init();
	}
	
	public synchronized boolean isPass() {
		MarkdownConfigBean mcb = ConfigBeanFactory.getMarkdownConfigBean();
		int autoMarkupCount = mcb.getAutoMarkUpVolume() * MarkupPhase.length;
		
		if(mcb.getAutoMarkUpVolume() <= 0){
			return this.autoMarkup();
		}
		
		if (this.phase.getTotal() >= autoMarkupCount) {
			if (this.nextPhaseIndex == mcb.getMarkUpSchedule().length) {
				return this.autoMarkup();
			}
			this.phase = new MarkupPhase(mcb.getMarkUpSchedule()[this.nextPhaseIndex]);
			this.nextPhaseIndex ++;
		}
		boolean pass = phase.isQualified();
		this.qualifies = pass ? this.qualifies + 1 : this.qualifies;
		return pass;
	}
	
	private boolean autoMarkup(){
		ConfigBeanFactory.getMarkdownConfigBean().markup(this.name);
		MarkupInfo marticsInfo = new MarkupInfo(this.name, Version.getVersion(), this.qualifies);
		logger.info(String.format("Database %s has been marked up automatically", this.name));
		logger.markup(marticsInfo);
		this.init();
		return true;
	}
	
	private void init(){
		int[] schedules = ConfigBeanFactory.getMarkdownConfigBean()
				.getMarkUpSchedule();
		this.nextPhaseIndex = 1;
		this.phase = new MarkupPhase(schedules[0]);
	}

	public synchronized void rollback() {
		if(this.qualifies > 1){
			this.qualifies --;
		}
		int[] schedules = ConfigBeanFactory.getMarkdownConfigBean()
				.getMarkUpSchedule();
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
