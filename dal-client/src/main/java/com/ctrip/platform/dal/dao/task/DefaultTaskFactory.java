package com.ctrip.platform.dal.dao.task;

import java.util.Map;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalParser;

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
	public <T> BulkTask<int[], T> createBatchInsertTask(DalParser<T> parser) {
		BatchInsertTask<T> batchInsertTask = new BatchInsertTask<T>();
		batchInsertTask.initialize(parser);
		return batchInsertTask;
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
}
