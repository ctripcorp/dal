package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;

public class ShardedIntResultMerger implements BulkTaskResultMerger<Integer>{
	private int total;
	
	public void recordPartial(String shard, Integer[] partialIndex) {
	}
	
	@Override
	public void addPartial(String shard, Integer affectedRows) throws SQLException {
		total += affectedRows;
	}

	@Override
	public Integer merge() throws SQLException {
		return total;
	}
}
