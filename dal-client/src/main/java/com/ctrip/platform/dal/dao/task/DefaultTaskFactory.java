package com.ctrip.platform.dal.dao.task;

import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

public class DefaultTaskFactory implements DalTaskFactory {
	private Map<String, String> settings;
	
	@Override
	public void initialize(Map<String, String> settings) {
		this.settings = settings;
	}

	@Override
	public String getProperty(String key) {
		return settings.get(key);
	}
	
	public static <T> DatabaseCategory getDbCategory(DalParser<T> parser) {
		return DalClientFactory.getDalConfigure().getDatabaseSet(parser.getDatabaseName()).getDatabaseCategory();
	}
	
	@Override
	public <T> SingleTask<T> createSingleInsertTask(DalParser<T> parser) {
		SingleInsertTask<T> singleInsertTask = new SingleInsertTask<T>();
		singleInsertTask.initialize(parser);
		return singleInsertTask;
	}

	@Override
	public <T> SingleTask<T> createSingleReplaceTask(DalParser<T> parser) {
		if(DatabaseCategory.MySql == getDbCategory(parser)) {
			SingleReplaceTask<T> singleReplaceTask = new SingleReplaceTask<T>();
			singleReplaceTask.initialize(parser);
			return singleReplaceTask;
		}

		return null;
	}

	@Override
	public <T> SingleTask<T> createSingleDeleteTask(DalParser<T> parser) {
		SingleDeleteTask<T> singleDeleteTask = new SingleDeleteTask<T>();
		singleDeleteTask.initialize(parser);
		return singleDeleteTask;
	}

	@Override
	public <T> SingleTask<T> createSingleUpdateTask(DalParser<T> parser) {
		SingleUpdateTask<T> singleUpdateTask = new SingleUpdateTask<T>();
		singleUpdateTask.initialize(parser);
		return singleUpdateTask;
	}

	@Override
	public <T> BulkTask<Integer, T> createCombinedInsertTask(DalParser<T> parser) {
		/**
		 * Oracle has different way of INSERT VALUES, We do not support it yet.
		 */
		if(DatabaseCategory.Oracle == getDbCategory(parser))
			return null;
			
		CombinedInsertTask<T> combinedInsertTask = new CombinedInsertTask<T>();
		combinedInsertTask.initialize(parser);
		return combinedInsertTask;
	}

	@Override
	public <T> BulkTask<Integer, T> createCombinedReplaceTask(DalParser<T> parser) {
		if(DatabaseCategory.MySql == getDbCategory(parser)) {
			CombinedReplaceTask<T> combinedReplaceTask = new CombinedReplaceTask<T>();
			combinedReplaceTask.initialize(parser);
			return combinedReplaceTask;
		}

		return null;
	}

	@Override
	public <T> BulkTask<Integer, T> createCombinedDeleteTask(DalParser<T> parser) {
		if(DatabaseCategory.MySql == getDbCategory(parser)) {
			CombinedDeleteTask<T> combinedDeleteTask = new CombinedDeleteTask<T>();
			combinedDeleteTask.initialize(parser);
			return combinedDeleteTask;
		}

		return null;
	}

	@Override
	public <T> BulkTask<int[], T> createBatchInsertTask(DalParser<T> parser) {
		BatchInsertTask<T> batchInsertTask = new BatchInsertTask<T>();
		batchInsertTask.initialize(parser);
		return batchInsertTask;
	}

	@Override
	public <T> BulkTask<int[], T> createBatchReplaceTask(DalParser<T> parser) {
		if (DatabaseCategory.MySql == getDbCategory(parser)) {
			BatchReplaceTask<T> batchReplaceTask = new BatchReplaceTask<T>();
			batchReplaceTask.initialize(parser);
			return batchReplaceTask;
		}

		return null;
	}

	@Override
	public <T> BulkTask<int[], T> createBatchDeleteTask(DalParser<T> parser) {
		BatchDeleteTask<T> batchDeleteTask = new BatchDeleteTask<T>();
		batchDeleteTask.initialize(parser);
		return batchDeleteTask;
	}

	@Override
	public <T> BulkTask<int[], T> createBatchUpdateTask(DalParser<T> parser) {
		BatchUpdateTask<T> batchUpdateTask = new BatchUpdateTask<T>();
		batchUpdateTask.initialize(parser);
		return batchUpdateTask;
	}

	@Override
	public <T> DeleteSqlTask<T> createDeleteSqlTask(DalParser<T> parser) {
		DeleteSqlTask<T> deleteSqlTask = new DeleteSqlTask<T>();
		deleteSqlTask.initialize(parser);
		return deleteSqlTask;
	}

	@Override
	public <T> UpdateSqlTask<T> createUpdateSqlTask(DalParser<T> parser) {
		UpdateSqlTask<T> updateSqlTask = new UpdateSqlTask<T>();
		updateSqlTask.initialize(parser);
		return updateSqlTask;
	}

	@Override
	public <T> QuerySqlTask<T> createQuerySqlTask(DalParser<T> parser, DalResultSetExtractor<T> extractor) {
		QuerySqlTask<T> querySqlTask = new QuerySqlTask<>(extractor);
		querySqlTask.initialize(parser);
		return querySqlTask;
	}

	@Override
	public <T> FreeSqlQueryTask<T> createFreeSqlQueryTask(String logicDbName, DalResultSetExtractor<T> extractor) {
		FreeSqlQueryTask<T> freeSqlQueryTask = new FreeSqlQueryTask<>(extractor);
		freeSqlQueryTask.initialize(logicDbName);
		return freeSqlQueryTask;
	}

	@Override
	public  FreeSqlUpdateTask createFreeUpdateTask(String logicDbName) {
		FreeSqlUpdateTask freeSqlUpdateTask = new FreeSqlUpdateTask();
		freeSqlUpdateTask.initialize(logicDbName);
		return freeSqlUpdateTask;
	}

	@Override
	public MultipleQueryTask createMultipleQueryTask(String logicDbName, List<DalResultSetExtractor<?>> extractors) {
		MultipleQueryTask multipleQueryTask = new MultipleQueryTask(extractors);
		multipleQueryTask.initialize(logicDbName);
		return multipleQueryTask;
	}
}
