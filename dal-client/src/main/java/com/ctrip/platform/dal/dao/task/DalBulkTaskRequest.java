package com.ctrip.platform.dal.dao.task;

import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isAlreadySharded;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isShardingEnabled;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isTableShardingEnabled;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.shuffle;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.shuffleByTable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DalBulkTaskRequest<K, T> implements DalRequest<K>{
	private String logicDbName;
	private String rawTableName;
	private DalHints hints;
	private List<T> rawPojos;
	private List<Map<String, ?>> daoPojos;
	private BulkTask<K, T> task;
	private BulkTaskResultMerger<K> dbShardMerger;
	Map<String, Map<Integer, Map<String, ?>>> shuffled;
	
	public DalBulkTaskRequest(String logicDbName, String rawTableName, DalHints hints, List<T> rawPojos, BulkTask<K, T> task) {
		this.logicDbName = logicDbName;
		this.rawTableName = rawTableName;
		this.hints = hints;
		this.rawPojos = rawPojos;
		this.task = task;
	}

	@Override
	public void validate() throws SQLException {
		if(null == rawPojos)
			throw new DalException(ErrorCode.ValidatePojoList);

		if(task == null)
			throw new DalException(ErrorCode.ValidateTask);

		dbShardMerger = task.createMerger();
	}
	
	@Override
	public boolean isCrossShard() throws SQLException {
		daoPojos = task.getPojosFields(rawPojos);
		
		if(isAlreadySharded(logicDbName, rawTableName, hints))
			return false;
		
		if(isShardingEnabled(logicDbName)) {
			shuffled = shuffle(logicDbName, hints.getShardId(), daoPojos);
			// Only in one or no shard
			return shuffled.size() <= 1 ? false : true;
		}
		
		// Shard at table level or no shard at all 
		return false;
	}

	@Override
	public Callable<K> createTask() throws SQLException {
		hints = hints.clone();
		handleKeyHolder(false);
		
		// If only one shard is shuffled
		if(shuffled != null) {
			if(shuffled.size() == 0)
				return new BulkTaskCallable<>(logicDbName, rawTableName, hints, new HashMap<Integer, Map<String, ?>>(), task);

			String shard = shuffled.keySet().iterator().next();
			return new BulkTaskCallable<>(logicDbName, rawTableName, hints.inShard(shard), shuffled.get(shard), task);
		}
	
		// Convert to index map
		Map<Integer, Map<String, ?>> daoPojosMap = new HashMap<>();
		for(int i = 0; i < daoPojos.size(); i++)
			daoPojosMap.put(i, daoPojos.get(i));

		return new BulkTaskCallable<>(logicDbName, rawTableName, hints, daoPojosMap, task);
	}

	@Override
	public Map<String, Callable<K>> createTasks() throws SQLException {
		Map<String, Callable<K>> tasks = new HashMap<>();
		
		// I know this is not so elegant.
		handleKeyHolder(true);
		
		for(String shard: shuffled.keySet()) {
			Map<Integer, Map<String, ?>> pojosInShard = shuffled.get(shard);
			
			dbShardMerger.recordPartial(shard, pojosInShard.keySet().toArray(new Integer[pojosInShard.size()]));
			
			tasks.put(shard, new BulkTaskCallable<>(
					logicDbName, rawTableName, hints.clone().inShard(shard), shuffled.get(shard), task));
		}

		return tasks; 
	}
	
	private void handleKeyHolder(boolean requireMerge) {
		if(hints.getKeyHolder() == null)
			return;
		
		hints.getKeyHolder().requireMerge();
	}

	@Override
	public BulkTaskResultMerger<K> getMerger() {
		return dbShardMerger;
	}
	
	private static class BulkTaskCallable<K, T> implements Callable<K> {
		private String logicDbName;
		private String rawTableName;
		private DalHints hints;
		private Map<Integer, Map<String, ?>> shaffled;
		private BulkTask<K, T> task;

		public BulkTaskCallable(String logicDbName, String rawTableName, DalHints hints, Map<Integer, Map<String, ?>> shaffled, BulkTask<K, T> task){
			this.logicDbName = logicDbName;
			this.rawTableName = rawTableName;
			this.hints = hints;
			this.shaffled = shaffled;
			this.task = task;
		}

		@Override
		public K call() throws Exception {
			if(shaffled.isEmpty()) return task.getEmptyValue();
			
			if(isTableShardingEnabled(logicDbName, rawTableName)) {
				return executeByTableShards();
			}else{
				return task.execute(hints, shaffled);
			}
		}

		private K executeByTableShards() throws SQLException {
			BulkTaskResultMerger<K> merger = task.createMerger();
			
			Map<String, Map<Integer, Map<String, ?>>> pojosInTable = shuffleByTable(logicDbName, hints.getTableShardId(), shaffled);
			
			if(pojosInTable.size() > 1 && hints.getKeyHolder() != null) {
				hints.getKeyHolder().requireMerge();
			}
				
			DalHints tmpHints;
			for(String curTableShardId: pojosInTable.keySet()) {
				Map<Integer, Map<String, ?>> pojosInShard = pojosInTable.get(curTableShardId);
				tmpHints = hints.clone();
				
				tmpHints.inTableShard(curTableShardId);
				merger.recordPartial(curTableShardId, pojosInShard.keySet().toArray(new Integer[pojosInShard.size()]));
				
				K partial = task.execute(tmpHints, pojosInShard);
				merger.addPartial(curTableShardId, partial);
				
			}
			return merger.merge();
		}
	}
}
