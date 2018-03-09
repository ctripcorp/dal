package com.ctrip.platform.dal.dao.status;

import java.util.Date;

public interface DataSourceStatusMBean {

	boolean isManualMarkdown();

	void setManualMarkdown(boolean manualMarkdown);

	boolean isAutoMarkdown();

	String getName();

	Date getManualMarkdownTime();

	Date getAutoMarkdownTime();

}