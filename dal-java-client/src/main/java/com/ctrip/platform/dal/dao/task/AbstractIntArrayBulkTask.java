package com.ctrip.platform.dal.dao.task;

import java.util.List;

public abstract class AbstractIntArrayBulkTask<T> extends TaskAdapter<T> implements BulkTask<int[], T> {
	
	@Override
	public int[] merge(List<int[]> results) {
		return mergeIntArray(results);
	}

}
