package com.ctrip.platform.dal.dao.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;
import com.ctrip.platform.dal.dao.client.DalWatcher;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.dao.strategy.DalShardingStrategy;
import com.ctrip.platform.dal.dao.task.BulkTask;

public class DalShardingHelper {
	public static boolean isShardingEnabled(String logicDbName) {
		return getDatabaseSet(logicDbName).isShardingSupported();
	}
	
	public static boolean isTableShardingEnabled(String logicDbName, String tableName) {
		return getDatabaseSet(logicDbName).isTableShardingSupported(tableName);
	}
	
	public static String buildShardStr(String logicDbName, String shardId) throws SQLException {
		String separator = getDatabaseSet(logicDbName).getStrategy().getTableShardSeparator();
		return separator == null? shardId: separator + shardId;
	}
	
	public static DatabaseSet getDatabaseSet(String logicDbName) {
		return DalClientFactory.getDalConfigure().getDatabaseSet(logicDbName);
	}
	
	/**
	 * Try to locate DB shard id by hints. If can not be located, return false.
	 * @param logicDbName
	 * @param hints
	 * @return true if shard id can be located
	 * @throws SQLException
	 */
	private static boolean locateShardId(String logicDbName, DalHints hints) throws SQLException {
		DalConfigure config = DalClientFactory.getDalConfigure();
		
		DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
		String shardId = dbSet.getStrategy().locateDbShard(config, logicDbName, hints);
		if(shardId == null)
			return false;
		
		// Fail fast asap
		dbSet.validate(shardId);
		hints.inShard(shardId);
		
		return true;
	}

	/**
	 * Locate table shard id by hints.
	 * @param logicDbName
	 * @param hints
	 * @return
	 * @throws SQLException
	 */
	private static boolean locateTableShardId(String logicDbName, DalHints hints) throws SQLException {
		DalConfigure config = DalClientFactory.getDalConfigure();
		DalShardingStrategy strategy = config.getDatabaseSet(logicDbName).getStrategy();
		
		// First check if we can locate the table shard id with the original hints
		String tableShardId = strategy.locateTableShard(config, logicDbName, hints);
		if(tableShardId == null)
			return false;
		
		hints.inTableShard(tableShardId);
		return true;
	}
	
	/**
	 * Locate table shard id by hints.
	 * @param logicDbName
	 * @param hints
	 * @return
	 * @throws SQLException
	 */
	public static String locateTableShardId(String logicDbName, DalHints hints, StatementParameters parameters, Map<String, ?> fields) throws SQLException {
		DalConfigure config = DalClientFactory.getDalConfigure();
		DalShardingStrategy strategy = config.getDatabaseSet(logicDbName).getStrategy();
		
		// First check if we can locate the table shard id with the original hints
		String shard = strategy.locateTableShard(config, logicDbName, hints);
		if(shard != null)
			return shard;
		
		shard = strategy.locateTableShard(config, logicDbName, new DalHints().setParameters(parameters).setFields(fields));
		if(shard != null)
			return shard;

		throw new SQLException("Can not locate table shard for " + logicDbName);

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
	
	/**
	 * Verify if shard id is already set for potential corss shard batch operation.
	 * This includes combined insert, batch insert and batch delete.
	 * It will first check if sharding is enabled. Then detect if necessary sharding id can be located.
	 * If all meet, then allow the operation 
	 * TODO do more analyze of the logic here
	 * @param logicDbName
	 * @param hints
	 * @param message
	 * @throws SQLException
	 * @return if all sharding id can be located.
	 */
	public static boolean isAlreadySharded(String logicDbName, String tableName, DalHints hints) throws SQLException {
		// For normal case, both DB and table sharding are not enabled
		if(!(isShardingEnabled(logicDbName) || isTableShardingEnabled(logicDbName, tableName)))
			return true;
		
		// Assume the out transaction already handle sharding logic
		// This may have potential issue if PD think they can do cross DB operation
		// TOD check here
		if(DalTransactionManager.isInTransaction())
			return true;

		hints.cleanUp();

		// Verify if DB shard is defined
		if(isShardingEnabled(logicDbName) && !locateShardId(logicDbName, hints))
			return false;
		
		// Verify if table shard is defined
		if(isTableShardingEnabled(logicDbName, tableName) && !locateTableShardId(logicDbName, hints))
			return false;
		
		return true;
	}
	
	public static void detectDistributedTransaction(String logicDbName, DalHints hints, List<Map<String, ?>>  daoPojos) throws SQLException {
		if(!isShardingEnabled(logicDbName))
			return;
		
		if(!DalTransactionManager.isInTransaction())
			return;
		
		String shardId = null;
		if(locateShardId(logicDbName, hints)) {
			shardId = hints.getShardId();
			isSameShard(shardId);
		} else {
			detectDistributedTransaction(shuffle(logicDbName, shardId, daoPojos).keySet());
		}
	}
	
	private static void detectDistributedTransaction(Set<String> shardIds) throws SQLException {
		if(!DalTransactionManager.isInTransaction())
			return;
		
		// Not allowed for distributed transaction
		if(shardIds.size() > 1)
			throw new SQLException("Potential distributed operation detected in shards: " + shardIds);
		
		String shardId = shardIds.iterator().next();
		
		isSameShard(shardId);
	}

	private static void isSameShard(String shardId) throws SQLException {
		if(shardId.equals(DalTransactionManager.getCurrentDbMeta().getShardId()))
			throw new SQLException("Operation is not allowed in different database shard within current transaction. Current shardId: " + DalTransactionManager.getCurrentDbMeta().getShardId() + ". Requeted shardId: " + shardId);
	}
	
	public static <T> T executeByDbShard(String logicDbName, String rawTableName, DalHints hints, List<Map<String, ?>>  daoPojos, BulkTask<T> task) throws SQLException {
		DalWatcher.crossShardBegin();
		T result;
		
		if(isShardingEnabled(logicDbName)) {
			List<T> results = new LinkedList<>();
			Map<String, List<Map<String, ?>>> shuffled = shuffle(logicDbName, hints.getShardId(), daoPojos);
			for(String shard: shuffled.keySet()) {
				hints.inShard(shard);
				T tmpResult = executeByTableShard(logicDbName, rawTableName, hints, shuffled.get(shard), task);
				results.add(tmpResult);
			}
			result = task.merge(results);
		} else {
			result = executeByTableShard(logicDbName, rawTableName, hints, daoPojos, task);
		}
		
		DalWatcher.crossShardEnd();
		return result; 
	}
	
	public static <T> T executeByTableShard(String logicDbName, String tabelName, DalHints hints, List<Map<String, ?>> daoPojos, BulkTask<T> task) throws SQLException {
		if(isTableShardingEnabled(logicDbName, tabelName)) {
			DalHints tmpHints = hints.clone();
			Map<String, List<Map<String, ?>>> pojosInTable = shuffleByTable(logicDbName, hints.getTableShardId(), daoPojos);
			
			List<T> results = new ArrayList<T>(pojosInTable.size());
			for(String curTableShardId: pojosInTable.keySet()) {
				tmpHints.inTableShard(curTableShardId);
				T result = task.execute(tmpHints, pojosInTable.get(curTableShardId));
				results.add(result);
			}
			return task.merge(results);
		}else{
			return task.execute(hints, daoPojos);
		}
	}
	
	public static int[] combine(int[]... counts) {
		int total = 0;
		for(int[] countsInTable: counts)
			total += countsInTable.length;
		
		int[] totalCounts = new int[total];
		int cur = 0;
		for(int[] countsInTable: counts) {
			System.arraycopy(countsInTable, 0, totalCounts, cur, countsInTable.length);
			cur += countsInTable.length;
		}
		
		return totalCounts;
	}
	
//	public static interface BulkTask<T> {
//		T execute(DalHints hints, List<Map<String, ?>> shaffled) throws SQLException;
//		T merge(List<T> results);
//	}
//	
//	public static abstract class AbstractIntArrayBulkTask implements BulkTask<int[]> {
//		@Override
//		public int[] merge(List<int[]> results) {
//			return DalShardingHelper.combine(results.toArray(new int[results.size()][]));
//		}
//	}
}
