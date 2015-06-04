package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;

/**
 * The only interesting thing for this class is it needs to merge generated keys
 * @author jhhe
 *
 */
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
