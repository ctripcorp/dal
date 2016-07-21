package com.ctrip.platform.dal.dao.status;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;

public class MarkdownStatus extends BaseStatus implements MarkdownStatusMBean {

	private volatile boolean appMarkDown = false;

	private volatile boolean enableAutoMarkDown = false;

	private volatile int autoMarkUpDelay = 120;

	public boolean isAppMarkDown() {
		return this.appMarkDown;
	}

	public void setAppMarkDown(boolean markdown) {
		this.appMarkDown = markdown;
		changed();
	}

	public boolean isMarkdown(String dbname) {
		return MarkdownManager.isMarkdown(dbname);
	}

	public boolean isEnableAutoMarkDown() {
		return enableAutoMarkDown;
	}

	public void setEnableAutoMarkDown(boolean enableAutoMarkDown) {
		this.enableAutoMarkDown = enableAutoMarkDown;
		MarkdownManager.resetAutoMarkdowns();
		changed();
	}

	public int getAutoMarkUpDelay() {
		return autoMarkUpDelay;
	}

	public void setAutoMarkUpDelay(int autoMarkUpDelay) {
		this.autoMarkUpDelay = autoMarkUpDelay;
		changed();
	}

	public String getMarkDownKeys() {
		Set<String> names = new HashSet<>();
		for(String dbName: DalClientFactory.getDalConfigure().getDataSourceNames()){
			if(isMarkdown(dbName))
				names.add(dbName);
		}

		return StringUtils.join(names, ",");
	}

	public String getDataSourceNames() {
		return StringUtils.join(DalClientFactory.getDalConfigure().getDataSourceNames(), ",");
	}

	public String getDatabaseSetNames() {
		return StringUtils.join(DalClientFactory.getDalConfigure().getDatabaseSetNames(), ",");
	}
	
	public String getAutoMarkDowns() {
		Set<String> names = new HashSet<>();
		for(String dbName: DalClientFactory.getDalConfigure().getDataSourceNames()){
			DataSourceStatus dss = DalStatusManager.getDataSourceStatus(dbName);
			if(dss.isAutoMarkdown())
				names.add(dbName);
		}
		return StringUtils.join(names, ",");
	}
}