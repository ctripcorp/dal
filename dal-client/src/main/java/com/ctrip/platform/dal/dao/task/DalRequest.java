package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Callable;

import com.ctrip.platform.dal.dao.ResultMerger;

public interface DalRequest<T> {
    /**
     * @return Caller's class.method name
     */
    String getCaller();
    
    /**
     * If the request is executed in asyn mode
     * @return
     */
    boolean isAsynExecution();

	/**
	 * Validate request
	 * @throws SQLException
	 */
    void validate() throws SQLException;

	/**
	 * @return true if it is cross shard
	 */
    boolean isCrossShard() throws SQLException;
	
	/**
	 * Create single task for incoming request
	 * @return
	 * @throws SQLException
	 */
    Callable<T> createTask() throws SQLException;
	
	/**
	 * To split by DB shard
	 * @return map of shard id to callable
	 */
    Map<String, Callable<T>> createTasks() throws SQLException;
	
	/**
	 * @return result merge in cross shard case
	 */
    ResultMerger<T> getMerger();
    
    /**
     * Doing some cleaning up here
     */
    void endExecution() throws SQLException;
}
