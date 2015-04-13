package com.ctrip.platform.dal.dao.task;

/**
 * All tasks should be staeless
 * @author jhhe
 *
 */
public interface TaskFactory<T> extends DaoTask<T> {
	SingleTask<T> createSingleInsertTask();
	
	SingleTask<T> createSingleDeleteTask();

	SingleTask<T> createSingleUpdateTask();
	
	BulkTask<Integer, T> createCombinedInsertTask();
	
	BulkTask<int[], T> createBatchInsertTask();

	BulkTask<int[], T> createBatchDeleteTask();

	BulkTask<int[], T> createBatchUpdateTask();
}
