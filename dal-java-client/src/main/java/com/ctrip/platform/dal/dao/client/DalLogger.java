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
	
	void info(String desc);
	
	void warn(String desc);
	
	void error(String desc, Throwable e);
	
	/**
	 * Fail on getting connections fro the given logic DB
	 * @param logicDb
	 * @param e
	 */
	void getConnectionFailed(String logicDb, Throwable e);
	
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
	 * @param result
	 * @param entry
	 */
	void success(Object result, LogEntry entry);
	
	/**
	 * The DB operation is fail
	 * @param e
	 * @param entry
	 */
	void fail(Throwable e, LogEntry entry);
	
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
	
	/**
	 * The system is going to be shutdown
	 */
	void shutdown();
}
