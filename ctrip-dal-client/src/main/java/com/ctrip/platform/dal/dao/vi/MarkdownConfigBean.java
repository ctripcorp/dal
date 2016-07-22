package com.ctrip.platform.dal.dao.vi;

import com.ctrip.framework.vi.annotation.ComponentStatus;
import com.ctrip.framework.vi.annotation.FieldInfo;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.ctrip.platform.dal.dao.status.MarkdownStatus;

@ComponentStatus(id="com.ctrip.dal.client.MarkdownConfigBean",name="com.ctrip.dal.client.MarkdownConfigBean",description = "DAL Markdown Overview")
public class MarkdownConfigBean extends BaseConfigBean {
	@FieldInfo(name = "AppMarkdown", description = "AppMarkdown")
	private boolean appMarkDown;

	@FieldInfo(name = "EnableAutoMarkdown", description = "EnableAutoMarkdown")
	private boolean enableAutoMarkdown;

	@FieldInfo(name = "AutoMarkUpDelay", description = "AutoMarkUpDelay")
	private int autoMarkUpDelay;

	@FieldInfo(name = "MarkdownKeys", description = "MarkdownKeys")
	private String markdownKeys;

	@FieldInfo(name = "AutoMarkdowns", description = "AutoMarkdowns")
	private String autoMarkdowns;
	
	@Override
	protected void register() {
		DalStatusManager.getMarkdownStatus().addNotificationListener(this, null, null);
	}

	@Override
	protected void refresh() {
		MarkdownStatus  ms = DalStatusManager.getMarkdownStatus();
		appMarkDown = ms.isAppMarkdown();
		enableAutoMarkdown = ms.isEnableAutoMarkdown();
		autoMarkUpDelay = ms.getAutoMarkupDelay();
		markdownKeys = ms.getMarkdownKeys();
		autoMarkdowns = ms.getAutoMarkdownKeys();
	}
}
