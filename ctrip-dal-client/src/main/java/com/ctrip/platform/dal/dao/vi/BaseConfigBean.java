package com.ctrip.platform.dal.dao.vi;

import javax.management.Notification;
import javax.management.NotificationListener;

public abstract class BaseConfigBean implements NotificationListener {
	public BaseConfigBean() {
		register();
		refresh();
	}
	
	@Override
	public void handleNotification(Notification notification, Object handback) {
		refresh();
	}
	
	protected abstract void register();
	protected abstract void refresh();
}
