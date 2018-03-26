package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.ResultMerger;

public interface BulkTaskResultMerger<T> extends ResultMerger<T>{
	void recordPartial(String shard, Integer[] partialIndex);
}
