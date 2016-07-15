package com.ctrip.platform.dal.dao.status;

public interface MarkdownStatusMBean {

	boolean isAppMarkDown();

	void setAppMarkDown(boolean markdown);

	boolean isEnableAutoMarkDown();

	void setEnableAutoMarkDown(boolean enableAutoMarkDown);

	int getAutoMarkUpVolume();

	void setAutoMarkUpVolume(int autoMarkupBatches);

	String getAutoMarkUpSchedule();

	void setAutoMarkUpSchedule(String autoMarkUpSchedule) throws Exception;

	int getAutoMarkUpDelay();

	void setAutoMarkUpDelay(int autoMarkUpDelay);

	String getDatabaseSetNames();

	String getDataSourceNames();

	String getMarkDownKeys();

	String getAutoMarkDowns();
	
}