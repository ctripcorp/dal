package com.ctrip.platform.dal.dao.task;

/**
 * All tasks should be staeless
 * @author jhhe
 *
 */
public interface TaskFactory {
	SingleTask createSingleInsertTask();
	
	SingleTask createSingleDeleteTask();

	SingleTask createSingleUpdateTask();
	
	BulkTask<Integer> createCombinedInsertTask();
	
	BulkTask<int[]> createBatchInsertTask();

	BulkTask<int[]> createBatchDeleteTask();

	BulkTask<int[]> createBatchUpdateTask();
}
