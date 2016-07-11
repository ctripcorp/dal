package com.ctrip.platform.dal.dao.status;

public interface DataSourceStatusMBean {

	boolean isManualMarkdown();

	void setManualMarkdown(boolean manualMarkdown);

	boolean isAutoMarkdown();

	String getName();

	long getManualMarkdownTime();

	long getAutoMarkdownTime();

}