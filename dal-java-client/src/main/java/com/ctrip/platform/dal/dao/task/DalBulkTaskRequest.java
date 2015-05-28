package com.ctrip.platform.dal.dao.task;

import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isAlreadySharded;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isShardingEnabled;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isTableShardingEnabled;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.shuffle;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.shuffleByTable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.client.DalWatcher;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DalBulkTaskRequest<K, T> implements DalRequest<K>{
	private String logicDbName;
	private String rawTableName;
	private DalHints hints;
	private List<Map<String, ?>> daoPojos;
	private BulkTask<K, T> task;
	Map<String, List<Map<String, ?>>> shuffled;
	
	public DalBulkTaskRequest(String logicDbName, String rawTableName, DalHints hints, List<Map<String, ?>> daoPojos, BulkTask<K, T> task) {
		this.logicDbName = logicDbName;
		this.rawTableName = rawTableName;
		this.hints = hints;
		this.daoPojos = daoPojos;
		this.task = task;
	}

	@Override
	public void validate() throws SQLException {
		if(null == daoPojos)
			throw new DalException(ErrorCode.ValidatePojoList);
		
		if(task == null)
			throw new DalException(ErrorCode.ValidateTask);
	}
	
	@Override
	public boolean isCrossShard() throws SQLException {
		if(isAlreadySharded(logicDbName, rawTableName, hints))
			return false;
		
		if(isShardingEnabled(logicDbName)) {
			shuffled = shuffle(logicDbName, hints.getShardId(), daoPojos);
			// Only in one shard
			return shuffled.size() == 1 ? false : true;
		}
		
		// Shard at table level or no shard at all 
		return false;
	}

	@Override
	public Callable<K> createTask() throws SQLException {
		// If only one shard is shuffled
		if(shuffled != null)
			hints.inShard(shuffled.keySet().iterator().next());
		
		return new BulkTaskCallable<>(logicDbName, rawTableName, hints, daoPojos, task);
	}

	@Override
	public Map<String, Callable<K>> createTasks() throws SQLException {
		Map<String, Callable<K>> tasks = new HashMap<>();
		
		for(String shard: shuffled.keySet())
			tasks.put(shard, new BulkTaskCallable<>(
					logicDbName, rawTableName, hints.clone().inShard(shard), shuffled.get(shard), task));

		return tasks; 
	}

	@Override
	public ResultMerger<K> getMerger() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static class BulkTaskCallable<K, T> implements Callable<K> {
		private String logicDbName;
		private String rawTableName;
		private DalHints hints;
		private List<Map<String, ?>> shaffled;
		private BulkTask<K, T> task;

		public BulkTaskCallable(String logicDbName, String rawTableName, DalHints hints, List<Map<String, ?>> shaffled, BulkTask<K, T> task){
			this.logicDbName = logicDbName;
			this.rawTableName = rawTableName;
			this.hints = hints;
			this.shaffled = shaffled;
			this.task = task;
		}

		@Override
		public K call() throws Exception {
			if(isTableShardingEnabled(logicDbName, rawTableName)) {
				DalHints tmpHints = hints.clone();
				Map<String, List<Map<String, ?>>> pojosInTable = shuffleByTable(logicDbName, hints.getTableShardId(), shaffled);
				
				List<K> results = new ArrayList<>(pojosInTable.size());
				for(String curTableShardId: pojosInTable.keySet()) {
					tmpHints.inTableShard(curTableShardId);
					K result = task.execute(tmpHints, pojosInTable.get(curTableShardId));
					results.add(result);
				}
				return task.merge(results);
			}else{
				return task.execute(hints, shaffled);
			}
		}
	}	
}
