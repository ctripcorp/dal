package com.ctrip.platform.dal.dao.vi;

import com.ctrip.framework.vi.annotation.ComponentStatus;
import com.ctrip.framework.vi.annotation.FieldInfo;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.ctrip.platform.dal.dao.status.MarkdownStatus;

@ComponentStatus(id="arch-data-common-bean-markdownbean",name="arch-data-common-bean-markdownbean",description = "DAL Markdown Overview")
public class MarkdownConfigBean extends BaseConfigBean {
	@FieldInfo(name = "AppMarkDown", description = "AppMarkDown")
	private boolean appMarkDown = false;

	@FieldInfo(name = "EnableAutoMarkDown", description = "EnableAutoMarkDown")
	private boolean enableAutoMarkDown = false;

	@FieldInfo(name = "AutoMarkUpBatches", description = "AutoMarkUpBatches")
	private int autoMarkUpVolume = -1;

	@FieldInfo(name = "MarkDownKeys", description = "MarkDownKeys")
	private String markDownKeys = "";

	@FieldInfo(name = "AutoMarkDowns", description = "AutoMarkDowns")
	private String autoMarkDowns="";
	
	@FieldInfo(name = "AllInOneKeys", description = "AllInOneKeys")
	private String allInOneKeys = "";

	@FieldInfo(name = "AutoMarkUpSchedule", description = "AutoMarkUpSchedule")
	private String autoMarkUpSchedule = "1,3,5";

	@FieldInfo(name = "AutoMarkUpDelay", description = "AutoMarkUpDelay")
	private int autoMarkUpDelay = 120;

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
	}
}
