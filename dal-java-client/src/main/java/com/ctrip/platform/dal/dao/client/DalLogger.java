package com.ctrip.platform.dal.dao.client;

import java.util.Map;

public interface DalLogger {
	/**
	 * To initialize logger's settings
	 * @param settings
	 */
	void initLogger(Map<String, String> settings);
	
	LogEntry createLogEntry();
	
	void start(LogEntry entry);
	
	void success(Object result, LogEntry entry);
	
	void fail(Throwable e, LogEntry entry);
}
