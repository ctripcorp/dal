package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;

/**
 * Can be used for both DB and Table shard
 * @author jhhe
 *
 */
public class ShardedIntArrayResultMerger implements BulkTaskResultMerger<int[]>{
	private Map<String , Integer[]> indexByShard = new HashMap<>();
	private Map<String , int[]> affectedRowsByShard = new HashMap<>();
	
	public void recordPartial(String shard, DalHints hints, Integer[] partialIndex) {
		indexByShard.put(shard, partialIndex);
	}
	
	@Override
	public void addPartial(String shard, int[] affectedRows) throws SQLException {
		affectedRowsByShard.put(shard, affectedRows);
	}

	@Override
	public int[] merge() throws SQLException {
		int count = 0;
		
		for(Integer[] index: indexByShard.values())
			count += index.length;
		
		int[] result = new int[count];
		
		for(String shard: indexByShard.keySet()) {
			Integer[] index = indexByShard.get(shard);
			int[] affectedRows = affectedRowsByShard.get(shard);
			for(int i = 0; i < index.length; i++) {
				result[index[i]] = affectedRows[i];
			}
		}
		
		return result;
	}
}
