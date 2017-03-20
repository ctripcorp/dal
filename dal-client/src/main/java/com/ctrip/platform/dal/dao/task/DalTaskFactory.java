package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.configure.DalComponent;

/**
 * All tasks should be staeless
 * @author jhhe
 *
 */
public interface DalTaskFactory extends DalComponent {
	String getProperty(String key);
	
	<T> SingleTask<T> createSingleInsertTask(DalParser<T> parser);
	
	<T> SingleTask<T> createSingleDeleteTask(DalParser<T> parser);

	<T> SingleTask<T> createSingleUpdateTask(DalParser<T> parser);
	
	<T> BulkTask<Integer, T> createCombinedInsertTask(DalParser<T> parser);
	
	<T> BulkTask<int[], T> createBatchInsertTask(DalParser<T> parser);

	<T> BulkTask<int[], T> createBatchDeleteTask(DalParser<T> parser);

	<T> BulkTask<int[], T> createBatchUpdateTask(DalParser<T> parser);
	
	<T> DeleteSqlTask<T> createDeleteSqlTask(DalParser<T> parser);
	
	<T> UpdateSqlTask<T> createUpdateSqlTask(DalParser<T> parser);
}
