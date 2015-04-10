package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.DalParser;

public class DefaultTaskFactory<T> implements TaskFactory {
	private DalParser<T> parser;
	public DefaultTaskFactory(DalParser<T> parser) {
		this.parser = parser;
	}
	
	@Override
	public SingleTask createSingleInsertTask() {
		return new SingleInsertTast<T>(parser);
	}

	@Override
	public SingleTask createSingleDeleteTask() {
		return new SingleDeleteTast<T>(parser);
	}

	@Override
	public SingleTask createSingleUpdateTask() {
		return new SingleUpdateTast<T>(parser);
	}

	@Override
	public BulkTask<Integer> createCombinedInsertTask() {
		return new CombinedInsertTask<T>(parser);
	}

	@Override
	public BulkTask<int[]> createBatchInsertTask() {
		return new BatchInsertTask<T>(parser);
	}

	@Override
	public BulkTask<int[]> createBatchDeleteTask() {
		return new BatchDeleteTask<T>(parser);
	}

	@Override
	public BulkTask<int[]> createBatchUpdateTask() {
		return new BatchUpdateTask<T>(parser);
	}
}
