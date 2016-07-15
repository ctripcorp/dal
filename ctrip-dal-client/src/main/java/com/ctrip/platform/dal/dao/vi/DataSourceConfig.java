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
import com.ctrip.platform.dal.dao.status.DataSourceStatus;

@ComponentStatus(id="com.ctrip.dal.client.DataSourceConfig",name="com.ctrip.dal.client.DataSourceConfig",description = "DAL datasource config",list = true)
public class DataSourceConfig implements NotificationListener {
	@FieldInfo(name = "Name", description = "Name")
	private String name;
	
	@FieldInfo(name = "ManualMarkdown", description = "ManualMarkdown")
	private boolean manualMarkdown;
	
	@FieldInfo(name = "ManualMarkdownTime", description = "ManualMarkdownTime")
	private Date manualMarkdownTime;
	
	@FieldInfo(name = "AutoMarkdown", description = "AutoMarkdown")
	private boolean autoMarkdown;
	
	@FieldInfo(name = "AutoMarkdownTime", description = "AutoMarkdownTime")
	private Date autoMarkdownTime;
	
	public DataSourceConfig(String name) {
		this.name = name;
		register();
		refresh();
	}
	
    public static List<DataSourceConfig> list(){
    	List<DataSourceConfig> beans = new ArrayList<>();
		for(String name: DalClientFactory.getDalConfigure().getDataSourceNames()) {
			beans.add(new DataSourceConfig(name));
		}
		return beans;
    }

	private void register() {
		DalStatusManager.getDataSourceStatus(name).addNotificationListener(this, null, null);
	}

	private void refresh() {
		DataSourceStatus dss = DalStatusManager.getDataSourceStatus(name);
		manualMarkdown = dss.isManualMarkdown();
		autoMarkdown = dss.isAutoMarkdown();
		manualMarkdownTime = new Date(dss.getManualMarkdownTime());
		autoMarkdownTime = null;//new Date(dss.getAutoMarkdownTime());
	}
	
	@Override
	public void handleNotification(Notification notification, Object handback) {
		refresh();
	}
}
