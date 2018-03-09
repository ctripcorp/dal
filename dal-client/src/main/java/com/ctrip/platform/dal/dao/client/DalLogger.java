package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.dao.configure.DalComponent;
import com.ctrip.platform.dal.dao.markdown.MarkDownInfo;
import com.ctrip.platform.dal.dao.markdown.MarkupInfo;
import com.ctrip.platform.dal.dao.task.DalRequest;

/**
 * For non cross shard execution
 * start
 *      singleTaskCreated
 *      startTask
 *          start(LogEntry)
 *              startStatement
 *              endStatement
 *          success/fail
 *      endTask
 * end
 * 
 * For non cross shard execution
 * start
 *      crossShardTaskCreated
 *      startCrossShardTasks
 *          startTask
 *              start(LogEntry)
 *                  startStatement
 *                  endStatement
 *              success/fail
 *          endTask
 *      endCrossShards
 * end
 * 
 * @author jhhe
 *
 */
public interface DalLogger extends DalComponent {
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
	 * Start request processing. This will happen at DAO level.
	 * User can chose to pass a customized log context to better track the process
	 * 
	 * @param request
	 * @return log context. It can be null
	 */
	<T> LogContext start(DalRequest<T> request);
	
    /**
     * End request processing
     * @param request
     * 
     */
    void end(LogContext logContext, Throwable e);
    
    /**
     * Start cross shard tasks execution
     * @return Customized log contect
     */
    void startCrossShardTasks(LogContext logContext, boolean isSequentialExecution);
    
    /**
     * Start cross shard tasks execution
     * @param e any exception happened
     */
    void endCrossShards(LogContext logContext, Throwable e);
    
    /**
     * Start task execution
     * @param request
     */
    void startTask(LogContext logContext, String shard);
    
    /**
     * End task execution
     * @param request
     */
    void endTask(LogContext logContext, String shard, Throwable e);
    	
	/**
	 * To create a log entry for current DB operation 
	 * @return
	 */
	LogEntry createLogEntry();
	
	/**
	 * Start the DB operation
	 * @param entry
	 */
	void start(LogEntry entry);
	
	void startStatement(LogEntry entry);
	
	void endStatement(LogEntry entry, Throwable e);
	
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
