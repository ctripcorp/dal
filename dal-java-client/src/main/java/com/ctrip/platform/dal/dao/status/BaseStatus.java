package com.ctrip.platform.dal.dao.status;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

public class BaseStatus extends NotificationBroadcasterSupport {
	private static final String DEFAULT_TYPE = "value changed";
	private int seq = 0;
	
	protected void changed() {
		changed(DEFAULT_TYPE);
	}
	
	protected void changed(String type) {
		Notification n = new Notification(type, this, seq++, System.currentTimeMillis());
		sendNotification(n);
	}	
}
