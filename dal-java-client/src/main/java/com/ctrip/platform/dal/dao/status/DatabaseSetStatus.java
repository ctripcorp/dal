package com.ctrip.platform.dal.dao.status;

import java.util.Calendar;
import java.util.Date;

public class DatabaseSetStatus extends BaseStatus implements DatabaseSetStatusMBean {
	private volatile String name;
	private volatile boolean markdown;
	private volatile Date markdownTime;
	
	public DatabaseSetStatus(String name) {
		this.name = name;
	}
	
	public boolean isMarkdown() {
		return markdown;
	}
	public void setMarkdown(boolean markdown) {
		this.markdown = markdown;
		markdownTime = Calendar.getInstance().getTime();
		changed();
	}
	
	public String getName() {
		return name;
	}
	public Date getMarkdownTime() {
		return markdownTime;
	}
}
