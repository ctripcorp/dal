package com.ctrip.platform.dal.dao.status;

import java.util.concurrent.atomic.AtomicInteger;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

public class BaseStatus extends NotificationBroadcasterSupport {
	private static final String DEFAULT_TYPE = "value changed";
	private AtomicInteger seq = new AtomicInteger();
	
	protected void changed() {
		changed(DEFAULT_TYPE);
	}
	
	protected void changed(String type) {
		Notification n = new Notification(type, this, seq.incrementAndGet(), System.currentTimeMillis());
		sendNotification(n);
	}	
}
