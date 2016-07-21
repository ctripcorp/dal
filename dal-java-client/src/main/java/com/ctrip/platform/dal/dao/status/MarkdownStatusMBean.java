package com.ctrip.platform.dal.dao.status;

public interface MarkdownStatusMBean {

	boolean isAppMarkDown();

	void setAppMarkDown(boolean markdown);

	boolean isEnableAutoMarkDown();

	void setEnableAutoMarkDown(boolean enableAutoMarkDown);

	int getAutoMarkUpDelay();

	void setAutoMarkUpDelay(int autoMarkUpDelay);

	String getDatabaseSetNames();

	String getDataSourceNames();

	String getMarkDownKeys();

	String getAutoMarkDowns();
	
}