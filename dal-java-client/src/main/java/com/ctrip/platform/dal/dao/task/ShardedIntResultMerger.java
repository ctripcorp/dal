package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;

/**
 * The only interesting thing for this class is it needs to merge generated keys
 * @author jhhe
 *
 */
public class ShardedIntResultMerger implements BulkTaskResultMerger<Integer>{
	private int total;
	private KeyHolder keyHolder;
	
	public void recordPartial(String shard, DalHints hints, Integer[] partialIndex) {
		if(keyHolder == null)
			keyHolder = hints.getKeyHolder();
	}
	
	@Override
	public void addPartial(String shard, Integer affectedRows) throws SQLException {
		total += affectedRows;
	}

	@Override
	public Integer merge() throws SQLException {
		if(keyHolder != null)
			keyHolder.merge();
		
		return total;
	}
}
