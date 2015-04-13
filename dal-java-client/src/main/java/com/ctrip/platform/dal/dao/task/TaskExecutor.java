package com.ctrip.platform.dal.dao.task;

import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.detectDistributedTransaction;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isAlreadySharded;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isShardingEnabled;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isTableShardingEnabled;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalDetailResults;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;
import com.ctrip.platform.dal.dao.client.DalWatcher;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.dao.strategy.DalShardingStrategy;

public class TaskExecutor<T> {
	private DalParser<T> parser;
	private final String logicDbName;
	private final String rawTableName;

	public TaskExecutor(DalParser<T> parser) {
		this.parser = parser;
		logicDbName = parser.getDatabaseName();
		rawTableName = parser.getTableName();
	}
	
	public int execute(DalHints hints, T[] daoPojos, SingleTask<T> task) throws SQLException {
		if(isEmpty(daoPojos)) return 0;
		
		return execute(hints, Arrays.asList(daoPojos), task);
	}
	
	// TODO revise return type
	public int execute(DalHints hints, List<T> daoPojos, SingleTask<T> task) throws SQLException {
		if(isEmpty(daoPojos)) return 0;

		List<Map<String, ?>> pojos = getPojosFields(daoPojos);
		detectDistributedTransaction(logicDbName, hints, pojos);
		
		int count = 0;
		hints = hints.clone();// To avoid shard id being polluted by each pojos
		for (Map<String, ?> fields : pojos) {
			DalWatcher.begin();// TODO check if we needed

			try {
				count += task.execute(hints, fields);
			} catch (SQLException e) {
				// TODO do we need log error here?
				if (hints.isStopOnError())
					throw e;
			}
		}
		
		return count;	
	}
	
	public <K> K execute(DalHints hints, T[] daoPojos, BulkTask<K, T> task, K emptyValue) throws SQLException {
		if(isEmpty(daoPojos)) return emptyValue;
		
		return execute(hints, Arrays.asList(daoPojos), task, emptyValue);
	}
	
	public <K> K execute(DalHints hints, List<T> daoPojos, BulkTask<K, T> task, K emptyValue) throws SQLException {
		if(isEmpty(daoPojos)) return emptyValue;
		
		hints.setDetailResults(new DalDetailResults<K>());

		if(isAlreadySharded(logicDbName, rawTableName, hints))
			return task.execute(hints, getPojosFields(daoPojos));
		else
			return executeByDbShard(logicDbName, rawTableName, hints, getPojosFields(daoPojos), task);
	}
	
	private <K> K executeByDbShard(String logicDbName, String rawTableName, DalHints hints, List<Map<String, ?>>  daoPojos, BulkTask<K, T> task) throws SQLException {
		DalWatcher.crossShardBegin();
		K result;
		
		if(isShardingEnabled(logicDbName)) {
			List<K> results = new LinkedList<>();
			Map<String, List<Map<String, ?>>> shuffled = shuffle(logicDbName, hints.getShardId(), daoPojos);
			for(String shard: shuffled.keySet()) {
				hints.inShard(shard);
				K tmpResult = executeByTableShard(logicDbName, rawTableName, hints, shuffled.get(shard), task);
				results.add(tmpResult);
			}
			result = task.merge(results);
		} else {
			result = executeByTableShard(logicDbName, rawTableName, hints, daoPojos, task);
		}
		
		DalWatcher.crossShardEnd();
		return result; 
	}
	
	private <K> K executeByTableShard(String logicDbName, String tabelName, DalHints hints, List<Map<String, ?>> daoPojos, BulkTask<K, T> task) throws SQLException {
		if(isTableShardingEnabled(logicDbName, tabelName)) {
			DalHints tmpHints = hints.clone();
			Map<String, List<Map<String, ?>>> pojosInTable = shuffleByTable(logicDbName, hints.getTableShardId(), daoPojos);
			
			List<K> results = new ArrayList<>(pojosInTable.size());
			for(String curTableShardId: pojosInTable.keySet()) {
				tmpHints.inTableShard(curTableShardId);
				K result = task.execute(tmpHints, pojosInTable.get(curTableShardId));
				results.add(result);
			}
			return task.merge(results);
		}else{
			return task.execute(hints, daoPojos);
		}
	}
	
	/**
	 * Group pojos by shard id. Should be only used for DB that support sharding.
	 * 
	 * @param logicDbName
	 * @param pojos
	 * @return Grouped pojos
	 * @throws SQLException In case locate shard id faild
	 */
	private static Map<String, List<Map<String, ?>>> shuffle(String logicDbName, String shardId, List<Map<String, ?>> daoPojos) throws SQLException {
		Map<String, List<Map<String, ?>>> shuffled = new HashMap<String, List<Map<String, ?>>>();
		
		DalConfigure config = DalClientFactory.getDalConfigure();
		
		DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
		DalShardingStrategy strategy = dbSet.getStrategy();
		
		DalHints tmpHints = new DalHints();
		for(Map<String, ?> pojo:daoPojos) {
			
			String tmpShardId = shardId == null ? 
					strategy.locateDbShard(config, logicDbName, tmpHints.setFields(pojo)) :
					shardId;
			
			dbSet.validate(tmpShardId);

			List<Map<String, ?>> pojosInShard = shuffled.get(tmpShardId);
			if(pojosInShard == null) {
				pojosInShard = new LinkedList<Map<String, ?>>();
				shuffled.put(tmpShardId, pojosInShard);
			}
			
			pojosInShard.add(pojo);
		}
		
		detectDistributedTransaction(shuffled.keySet());
		
		return shuffled;
	}
	
	/**
	 * Shuffle by table shard id.
	 * @param logicDbName
	 * @param pojos
	 * @return
	 * @throws SQLException
	 */
	private static Map<String, List<Map<String, ?>>> shuffleByTable(String logicDbName, String tableShardId, List<Map<String, ?>> pojos) throws SQLException {
		Map<String, List<Map<String, ?>>> shuffled = new HashMap<String, List<Map<String, ?>>>();
		DalConfigure config = DalClientFactory.getDalConfigure();
		
		DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
		DalShardingStrategy strategy = dbSet.getStrategy();
		
		DalHints tmpHints = new DalHints();
		for(Map<String, ?> fields: pojos) {
			String shardId = tableShardId == null ?
					strategy.locateTableShard(config, logicDbName, tmpHints.setFields(fields)) :
					tableShardId;

			List<Map<String, ?>> pojosInShard = shuffled.get(shardId);
			if(pojosInShard == null) {
				pojosInShard = new LinkedList<Map<String, ?>>();
				shuffled.put(shardId, pojosInShard);
			}
			
			pojosInShard.add(fields);
		}
		
		return shuffled;
	}
	
	private boolean isEmpty(List<T> daoPojos) {
		return null == daoPojos || daoPojos.size() == 0;
	}
	
	private boolean isEmpty(T... daoPojos) {
		if(null == daoPojos)
			return true;
		
		return daoPojos.length == 1 && daoPojos[0] == null;
	}
	
	public List<Map<String, ?>> getPojosFields(List<T> daoPojos) {
		List<Map<String, ?>> pojoFields = new LinkedList<Map<String, ?>>();
		if (null == daoPojos || daoPojos.size() < 1)
			return pojoFields;
		
		for (T pojo: daoPojos){
			pojoFields.add(parser.getFields(pojo));
		}
		
		return pojoFields;
	}
}
