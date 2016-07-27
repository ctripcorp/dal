package com.ctrip.platform.dal.dao.task;


public abstract class AbstractIntArrayBulkTask<T> extends TaskAdapter<T> implements BulkTask<int[], T> {
	public int[] getEmptyValue() {
		return new int[0];
	}
	
	@Override
	public BulkTaskResultMerger<int[]> createMerger() {
		return new ShardedIntArrayResultMerger();
	}
}
