package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.DalParser;

public class DefaultTaskFactory<T> implements TaskFactory<T> {
	private SingleInsertTask<T> singleInsertTask = new SingleInsertTask<T>();
	private SingleDeleteTask<T> singleDeleteTask = new SingleDeleteTask<T>();
	private SingleUpdateTask<T> singleUpdateTask = new SingleUpdateTask<T>();
	private CombinedInsertTask<T> combinedInsertTask = new CombinedInsertTask<T>();
	
	private BatchInsertTask<T> batchInsertTask = new BatchInsertTask<T>();
	private BatchDeleteTask<T> batchDeleteTask = new BatchDeleteTask<T>();
	private BatchUpdateTask<T> batchUpdateTask = new BatchUpdateTask<T>();

	@Override
	public void initialize(DalParser<T> parser) {
		singleInsertTask.initialize(parser);
		singleDeleteTask.initialize(parser);
		singleUpdateTask.initialize(parser);
		combinedInsertTask.initialize(parser);
		
		batchInsertTask.initialize(parser);
		batchDeleteTask.initialize(parser);
		batchUpdateTask.initialize(parser);
		
	}

	@Override
	public SingleTask<T> createSingleInsertTask() {
		return singleInsertTask;
	}

	@Override
	public SingleTask<T> createSingleDeleteTask() {
		return singleDeleteTask;
	}

	@Override
	public SingleTask<T> createSingleUpdateTask() {
		return singleUpdateTask;
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
