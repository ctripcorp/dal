package com.ctrip.platform.dal.dao.status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceStatus extends BaseStatus implements DataSourceStatusMBean {
	private static Logger logger = LoggerFactory.getLogger(DataSourceStatus.class);
	
	private String name;
	private volatile boolean manualMarkdown = false;
	private volatile long manualMarkdownTime;
	private volatile boolean autoMarkdown = false;
	private volatile long autoMarkdownTime;
	
	public DataSourceStatus(String name) {
		this.name = name;
	}
	
	public boolean isManualMarkdown() {
		return manualMarkdown;
	}
	public void setManualMarkdown(boolean manualMarkdown) {
		this.manualMarkdown = manualMarkdown;
		if(manualMarkdown){
			this.manualMarkdownTime = System.currentTimeMillis();
			logger.info(String.format("Database %s has been marked down manually.", name));
		}else{
			logger.info(String.format("Database %s has been marked up manually.", name));
		}
		changed();
	}
	
	public boolean isAutoMarkdown() {
		return autoMarkdown;
	}
	public void setAutoMarkdown(boolean autoMarkdown) {
		this.autoMarkdown = autoMarkdown;
		if(autoMarkdown)
			this.autoMarkdownTime = System.currentTimeMillis();
		changed();
	}
	
	public String getName() {
		return name;
	}

	public long getManualMarkdownTime() {
		return manualMarkdownTime;
	}

	public long getAutoMarkdownTime() {
		return autoMarkdownTime;
	}
}
