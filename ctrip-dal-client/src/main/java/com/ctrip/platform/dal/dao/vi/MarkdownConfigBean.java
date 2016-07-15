package com.ctrip.platform.dal.dao.vi;

import com.ctrip.framework.vi.annotation.ComponentStatus;
import com.ctrip.framework.vi.annotation.FieldInfo;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.ctrip.platform.dal.dao.status.MarkdownStatus;

@ComponentStatus(id="com.ctrip.dal.client.MarkdownConfigBean",name="com.ctrip.dal.client.MarkdownConfigBean",description = "DAL Markdown Overview")
public class MarkdownConfigBean extends BaseConfigBean {
	@FieldInfo(name = "AppMarkDown", description = "AppMarkDown")
	private boolean appMarkDown;

	@FieldInfo(name = "EnableAutoMarkDown", description = "EnableAutoMarkDown")
	private boolean enableAutoMarkDown;

	@FieldInfo(name = "AutoMarkUpBatches", description = "AutoMarkUpBatches")
	private int autoMarkUpVolume;

	@FieldInfo(name = "MarkDownKeys", description = "MarkDownKeys")
	private String markDownKeys;

	@FieldInfo(name = "AutoMarkDowns", description = "AutoMarkDowns")
	private String autoMarkDowns;
	
	@FieldInfo(name = "DatabaseSets", description = "DatabaseSets")
	private String databaseSets;
	
	@FieldInfo(name = "AllInOneKeys", description = "AllInOneKeys")
	private String allInOneKeys;

	@FieldInfo(name = "AutoMarkUpSchedule", description = "AutoMarkUpSchedule")
	private String autoMarkUpSchedule;

	@FieldInfo(name = "AutoMarkUpDelay", description = "AutoMarkUpDelay")
	private int autoMarkUpDelay;

	public boolean isAppMarkDown() {
		return this.appMarkDown;
	}

	public void setAppMarkDown(boolean markdown) {
		this.appMarkDown = markdown;
	}

	public String getMarkDownKeys() {
		return this.markDownKeys;
	}

	public void setMarkDownKeys(String dbMarkdown) {
		this.markDownKeys = dbMarkdown;
	}

	public String getAllInOneKeys() {
		return this.allInOneKeys;
	}

	public String getDatabaseSets() {
		return databaseSets;
	}

	public boolean isEnableAutoMarkDown() {
		return enableAutoMarkDown;
	}

	public void setEnableAutoMarkDown(boolean enableAutoMarkDown) {
		this.enableAutoMarkDown = enableAutoMarkDown;
	}

	public int getAutoMarkUpVolume() {
		return autoMarkUpVolume;
	}

	public void setAutoMarkUpVolume(int autoMarkupBatches) {
		this.autoMarkUpVolume = autoMarkupBatches;
	}

	public int getAutoMarkUpDelay() {
		return autoMarkUpDelay;
	}

	public void setAutoMarkUpDelay(int autoMarkUpDelay) {
		this.autoMarkUpDelay = autoMarkUpDelay;
	}

	public String getAutoMarkUpSchedule() {
		return autoMarkUpSchedule;
	}

	public void setAutoMarkUpSchedule(String autoMarkUpSchedule) {
		this.autoMarkUpSchedule = autoMarkUpSchedule;
	}

	@Override
	protected void register() {
		DalStatusManager.getMarkdownStatus().addNotificationListener(this, null, null);
	}

	@Override
	protected void refresh() {
		MarkdownStatus  ms = DalStatusManager.getMarkdownStatus();
		appMarkDown = ms.isAppMarkDown();
		enableAutoMarkDown = ms.isEnableAutoMarkDown();
		autoMarkUpVolume = ms.getAutoMarkUpVolume();
		markDownKeys = ms.getMarkDownKeys();
		autoMarkDowns = ms.getAutoMarkDowns();
		allInOneKeys = ms.getDataSourceNames();
		autoMarkUpSchedule = ms.getAutoMarkUpSchedule();
		autoMarkUpDelay = ms.getAutoMarkUpDelay();
		databaseSets = ms.getDatabaseSetNames();
	}
}
