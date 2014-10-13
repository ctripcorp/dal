package com.ctrip.platform.dal.dao.markdown;

import java.util.concurrent.atomic.AtomicInteger;

import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;


public class Markup {
	private String name;
	private MarkupLevel level;
	private AtomicInteger retryTimes = new AtomicInteger(0);
	private AtomicInteger markupTimes = new AtomicInteger(0);
	
	public Markup(String name){
		this.name = name;
		this.level = MarkupLevel.LV1;
	}
	
	public boolean isMarkup(String key){
		 int markupTimes = this.markupTimes.get();
		 return this.level == MarkupLevel.LV2 && 
				 markupTimes >= ConfigBeanFactory.getMarkdownConfigBean().getAutoMarkupCount();
	}
	
	public boolean isPassed(String key){
		int markuplv1 = ConfigBeanFactory.getMarkdownConfigBean().getMarkuplv1();
		int markuplv2 = ConfigBeanFactory.getMarkdownConfigBean().getMarkuplv2();
		
		int retryTimes = this.retryTimes.incrementAndGet();
		
		if(this.level == MarkupLevel.LV1){
			if(retryTimes == markuplv1){
				return this.moveToLevel2(markuplv1);
			}
		}
		if(this.level == MarkupLevel.LV2){
			if(retryTimes == markuplv2){
				return this.resetLevel2(markuplv2);
			}
		}
		return false;
	}
	
	public void rollback(){
		int markuplv1 = ConfigBeanFactory.getMarkdownConfigBean().getMarkuplv1();
		int markuplv2 = ConfigBeanFactory.getMarkdownConfigBean().getMarkuplv2();
		
		 if(this.level == MarkupLevel.LV1){
			 this.reduce(1, markuplv1);
		 }
		 if(this.level == MarkupLevel.LV2){
			 this.reduce(1, markuplv2);
		 }
	}
	
	public synchronized boolean moveToLevel2(int m){
		this.retryTimes.set(0);
		this.markupTimes.addAndGet(m);
		if(this.markupTimes.get() >= ConfigBeanFactory.getMarkdownConfigBean().getAutoMarkupCount()){
			this.level = MarkupLevel.LV2;
			this.markupTimes.set(0);
		}
		return true;
	}
	
	public synchronized boolean resetLevel2(int m){
		this.retryTimes.set(0);
		this.markupTimes.addAndGet(m);
		return true;
	}
	
	public synchronized void reduce(int retry, int mks){
		if(this.retryTimes.get() >= retry)
			this.retryTimes.addAndGet(0 - retry);
		if(this.markupTimes.get() >= mks)
			this.markupTimes.addAndGet(0 - mks);
	}
	
	public String getName(){
		return this.name;
	}
	
	public String toString(){
		return String.format("Key:%s--Retry:%s--Markups:%s--level:%s", 
				this.name, this.retryTimes.get(), this.markupTimes.get(), this.level.toString());
	}
}
