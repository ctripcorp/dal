package com.ctrip.platform.dal.dao.status;

import java.util.Date;

public interface DatabaseSetStatusMBean {

	boolean isMarkdown();

	void setMarkdown(boolean markdown);

	String getName();

	Date getMarkdownTime();

}