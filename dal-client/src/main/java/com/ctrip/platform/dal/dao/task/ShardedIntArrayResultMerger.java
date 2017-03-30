package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Can be used for both DB and Table shard
 * @author jhhe
 *
 */
public class ShardedIntArrayResultMerger implements BulkTaskResultMerger<int[]>{
	private Map<String , Integer[]> indexByShard = new HashMap<>();
	private Map<Integer, Integer> affectedRowsMap = new TreeMap<>();
	
	public void recordPartial(String shard, Integer[] partialIndex) {
		indexByShard.put(shard, partialIndex);
	}
	
	@Override
	public void addPartial(String shard, int[] affectedRows) throws SQLException {
		Integer[] indexList = indexByShard.get(shard);
		int i = 0;
		for(Integer index: indexList)
			affectedRowsMap.put(index, affectedRows[i++]);
	}

	@Override
	public int[] merge() throws SQLException {
		int[] affectedRowsList = new int[affectedRowsMap.size()];
		
		int i = 0;
		for(Integer affectedRows: affectedRowsMap.values())
			affectedRowsList[i++] = affectedRows;
		
		return affectedRowsList;
	}
}
