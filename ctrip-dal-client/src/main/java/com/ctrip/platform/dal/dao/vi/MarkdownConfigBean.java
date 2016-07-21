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

	@FieldInfo(name = "AutoMarkUpDelay", description = "AutoMarkUpDelay")
	private int autoMarkUpDelay;

	@FieldInfo(name = "MarkDownKeys", description = "MarkDownKeys")
	private String markDownKeys;

	@FieldInfo(name = "AutoMarkDowns", description = "AutoMarkDowns")
	private String autoMarkDowns;
	
	@FieldInfo(name = "DatabaseSetsName", description = "DatabaseSetsNames")
	private String databaseSets;
	
	@FieldInfo(name = "AllInOneKeys", description = "AllInOneKeys")
	private String allInOneKeys;

	@Override
	protected void register() {
		DalStatusManager.getMarkdownStatus().addNotificationListener(this, null, null);
	}

	@Override
	protected void refresh() {
		MarkdownStatus  ms = DalStatusManager.getMarkdownStatus();
		appMarkDown = ms.isAppMarkDown();
		enableAutoMarkDown = ms.isEnableAutoMarkDown();
		autoMarkUpDelay = ms.getAutoMarkUpDelay();
		markDownKeys = ms.getMarkDownKeys();
		autoMarkDowns = ms.getAutoMarkDowns();
		allInOneKeys = ms.getDataSourceNames();
		databaseSets = ms.getDatabaseSetNames();
	}
}
