package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.configure.DalComponent;

import java.util.List;

/**
 * All tasks should be staeless
 * @author jhhe
 *
 */
public interface DalTaskFactory extends DalComponent {
	String getProperty(String key);
	
	<T> SingleTask<T> createSingleInsertTask(DalParser<T> parser);

	<T> SingleTask<T> createSingleReplaceTask(DalParser<T> parser);
	
	<T> SingleTask<T> createSingleDeleteTask(DalParser<T> parser);

	<T> SingleTask<T> createSingleUpdateTask(DalParser<T> parser);
	
	<T> BulkTask<Integer, T> createCombinedInsertTask(DalParser<T> parser);

	<T> BulkTask<Integer, T> createCombinedReplaceTask(DalParser<T> parser);

	<T> BulkTask<Integer, T> createCombinedDeleteTask(DalParser<T> parser);

	<T> BulkTask<int[], T> createBatchInsertTask(DalParser<T> parser);

	<T> BulkTask<int[], T> createBatchReplaceTask(DalParser<T> parser);

	<T> BulkTask<int[], T> createBatchDeleteTask(DalParser<T> parser);

	<T> BulkTask<int[], T> createBatchUpdateTask(DalParser<T> parser);
	
	<T> DeleteSqlTask<T> createDeleteSqlTask(DalParser<T> parser);
	
	<T> UpdateSqlTask<T> createUpdateSqlTask(DalParser<T> parser);

	<T> QuerySqlTask<T> createQuerySqlTask(DalParser<T> parser, DalResultSetExtractor<T> extractor);

	<T> FreeSqlQueryTask<T> createFreeSqlQueryTask(String logicDbName, DalResultSetExtractor<T> extractor);

	FreeSqlUpdateTask createFreeUpdateTask(String logicDbName);

	MultipleQueryTask createMultipleQueryTask(String logicDbName, List<DalResultSetExtractor<?>> extractors);
}
