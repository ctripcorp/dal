package com.ctrip.platform.dal.dao.vi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.management.Notification;
import javax.management.NotificationListener;

import com.ctrip.framework.vi.annotation.ComponentStatus;
import com.ctrip.framework.vi.annotation.FieldInfo;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.ctrip.platform.dal.dao.status.DatabaseSetStatus;

@ComponentStatus(id="com.ctrip.dal.client.DatabaseSetConfig",name="com.ctrip.dal.client.DatabaseSetConfig",description = "DAL Database Set config",list = true)
public class DatabaseSetConfig implements NotificationListener {
	@FieldInfo(name = "Name", description = "Name")
	private String name;
	
	@FieldInfo(name = "Markdown", description = "Markdown")
	private boolean markdown;
	
	@FieldInfo(name = "MarkdownTime", description = "MarkdownTime")
	private Date markdownTime;
	
	public DatabaseSetConfig(String name) {
		this.name = name;
		register();
		refresh();
	}
	
    public static List<DatabaseSetConfig> list(){
    	List<DatabaseSetConfig> beans = new ArrayList<>();
		for(String name: DalClientFactory.getDalConfigure().getDatabaseSetNames()) {
			beans.add(new DatabaseSetConfig(name));
		}
		return beans;
    }

	private void register() {
		DalStatusManager.getDatabaseSetStatus(name).addNotificationListener(this, null, null);
	}
	
	private void refresh() {
		DatabaseSetStatus dss = DalStatusManager.getDatabaseSetStatus(name);
		markdown = dss.isMarkdown();
		markdownTime = dss.getMarkdownTime();
	}

	@Override
	public void handleNotification(Notification notification, Object handback) {
		refresh();
	}
}
