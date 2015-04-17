package com.ctrip.platform.dal.dao.task;

import java.util.Map;

import com.ctrip.platform.dal.dao.DalParser;

/**
 * All tasks should be staeless
 * @author jhhe
 *
 */
public interface DalTaskFactory {
	void initialize(Map<String, ?> settings);
	
	<T> SingleTask<T> createSingleInsertTask(DalParser<T> parser);
	
	<T> SingleTask<T> createSingleDeleteTask(DalParser<T> parser);

	<T> SingleTask<T> createSingleUpdateTask(DalParser<T> parser);
	
	<T> BulkTask<Integer, T> createCombinedInsertTask(DalParser<T> parser);
	
	<T> BulkTask<int[], T> createBatchInsertTask(DalParser<T> parser);

	<T> BulkTask<int[], T> createBatchDeleteTask(DalParser<T> parser);

	<T> BulkTask<int[], T> createBatchUpdateTask(DalParser<T> parser);
}
