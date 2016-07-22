package com.ctrip.platform.dal.dao.status;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;

public class MarkdownStatus extends BaseStatus implements MarkdownStatusMBean {
	private static final int DEFAULT_AUTO_MARKUP_DELAY = 30;

	private volatile boolean appMarkdown = false;

	private volatile boolean enableAutoMarkdown = false;

	private volatile int autoMarkupDelay = DEFAULT_AUTO_MARKUP_DELAY;

	public boolean isAppMarkdown() {
		return this.appMarkdown;
	}

	public void setAppMarkdown(boolean markdown) {
		this.appMarkdown = markdown;
		changed();
	}

	public boolean isEnableAutoMarkdown() {
		return enableAutoMarkdown;
	}

	public void setEnableAutoMarkdown(boolean enableAutoMarkDown) {
		this.enableAutoMarkdown = enableAutoMarkDown;
		MarkdownManager.resetAutoMarkdowns();
		changed();
	}

	public int getAutoMarkupDelay() {
		return autoMarkupDelay;
	}

	public void setAutoMarkupDelay(int autoMarkUpDelay) {
		this.autoMarkupDelay = autoMarkUpDelay;
		changed();
	}

	public String getMarkdownKeys() {
		Set<String> names = new HashSet<>();
		for(String dbName: DalClientFactory.getDalConfigure().getDataSourceNames()){
			if(MarkdownManager.isMarkdown(dbName))
				names.add(dbName);
		}

		return StringUtils.join(names, ",");
	}

	public String getAutoMarkdownKeys() {
		Set<String> names = new HashSet<>();
		for(String dbName: DalClientFactory.getDalConfigure().getDataSourceNames()){
			DataSourceStatus dss = DalStatusManager.getDataSourceStatus(dbName);
			if(dss.isAutoMarkdown())
				names.add(dbName);
		}
		return StringUtils.join(names, ",");
	}
}