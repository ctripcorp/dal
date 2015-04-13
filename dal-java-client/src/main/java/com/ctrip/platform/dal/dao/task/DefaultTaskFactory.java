package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.DalParser;

public class DefaultTaskFactory<T> implements TaskFactory<T> {
	private SingleInsertTask<T> singleInsertTast = new SingleInsertTask<T>();
	private SingleDeleteTask<T> singleDeleteTast = new SingleDeleteTask<T>();
	private SingleUpdateTast<T> singleUpdateTast = new SingleUpdateTast<T>();
	private CombinedInsertTask<T> combinedInsertTask = new CombinedInsertTask<T>();
	
	private BatchInsertTask<T> batchInsertTask = new BatchInsertTask<T>();
	private BatchDeleteTask<T> batchDeleteTask = new BatchDeleteTask<T>();
	private BatchUpdateTask<T> batchUpdateTask = new BatchUpdateTask<T>();

	@Override
	public void initialize(DalParser<T> parser) {
		singleInsertTast.initialize(parser);
		singleDeleteTast.initialize(parser);
		singleUpdateTast.initialize(parser);
		combinedInsertTask.initialize(parser);
		
		batchInsertTask.initialize(parser);
		batchDeleteTask.initialize(parser);
		batchUpdateTask.initialize(parser);
		
	}

	@Override
	public SingleTask<T> createSingleInsertTask() {
		return singleInsertTast;
	}

	@Override
	public SingleTask<T> createSingleDeleteTask() {
		return singleDeleteTast ;
	}

	@Override
	public SingleTask<T> createSingleUpdateTask() {
		return singleUpdateTast;
	}

	@Override
	public BulkTask<Integer, T> createCombinedInsertTask() {
		return combinedInsertTask;
	}

	@Override
	public BulkTask<int[], T> createBatchInsertTask() {
		return batchInsertTask;
	}

	@Override
	public BulkTask<int[], T> createBatchDeleteTask() {
		return batchDeleteTask;
	}

	@Override
	public BulkTask<int[], T> createBatchUpdateTask() {
		return batchUpdateTask;
	}
}
