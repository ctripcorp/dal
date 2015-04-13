package com.ctrip.platform.dal.dao.client;

import java.util.Map;

import com.ctrip.platform.dal.dao.markdown.MarkDownInfo;
import com.ctrip.platform.dal.dao.markdown.MarkupInfo;

public interface DalLogger {
	/**
	 * To initialize logger's settings
	 * @param settings
	 */
	void initLogger(Map<String, String> settings);
	
	void info(String msg);
	
	void warn(String msg);
	
	void error(String msg, Throwable e);
	
	/**
	 * Fail on getting connections fro the given logic DB
	 * @param dbName
	 * @param e
	 */
	void getConnectionFailed(String dbName, Throwable e);
	
	/**
	 * To create a context for current DB operation 
	 * @return
	 */
	LogEntry createLogEntry();
	
	/**
	 * Start the DB operation
	 * @param entry
	 */
	void start(LogEntry entry);
	
	/**
	 * The DB operation is completed successfully
	 */
	void success(LogEntry entry, int count);
	
	/**
	 * The DB operation is fail
	 */
	void fail(LogEntry entry, Throwable e);
	
	/**
	 * The DB is marked down because of error count threshold is reached
	 * @param markdown
	 */
	void markdown(MarkDownInfo markdown);
	
	/**
	 * The DB is marked down because of success count threshold is reached
	 * @param markup
	 */
	void markup(MarkupInfo markup);
	
	String getAppID();
	
	/**
	 * The system is going to be shutdown
	 */
	void shutdown();
}
