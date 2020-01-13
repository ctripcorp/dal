package com.ctrip.platform.dal.dao.task;

import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.exceptions.DalException;


public abstract class AbstractIntArrayBulkTask<T> extends TaskAdapter<T> implements BulkTask<int[], T> {
	public int[] getEmptyValue() {
		return new int[0];
	}

	@Override
	public BulkTaskContext<T> createTaskContext(DalHints hints, List<Map<String, ?>> daoPojos, List<T> rawPojos) throws DalException {
		BulkTaskContext context = new BulkTaskContext<T>(rawPojos);
		if (context instanceof DalContextConfigure)
			context.setShardingCategory(shardingCategory);
		return context;
	}
	
	@Override
	public BulkTaskResultMerger<int[]> createMerger() {
		return new ShardedIntArrayResultMerger();
	}
}
