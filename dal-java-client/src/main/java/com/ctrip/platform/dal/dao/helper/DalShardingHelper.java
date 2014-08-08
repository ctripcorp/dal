package com.ctrip.platform.dal.dao.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.dao.strategy.DalShardingStrategy;

public class DalShardingHelper {
	private static final String EMPTY = "";
	
	public static boolean isShardingEnabled(String logicDbName) {
		return getDatabaseSet(logicDbName).isShardingSupported();
	}
	
	public static <T> String locateShardId(String logicDbName, DalParser<T> parser, T pojo) throws SQLException {
		return locateShardId(logicDbName, new DalHints().set(DalHintEnum.shardColValues, parser.getFields(pojo)));
	}
	
	public static <T> String locateShardId(String logicDbName, DalParser<T> parser, StatementParameters parameters) throws SQLException {
		return locateShardId(logicDbName, new DalHints().set(DalHintEnum.parameters, parameters));
	}
	
	public static boolean isTableShardingEnabled(String logicDbName) {
		DatabaseSet dbSet = getDatabaseSet(logicDbName);
		if(dbSet.isShardingSupported()) {
			try {
				return dbSet.getStrategy().isShardingByTable();
			} catch (SQLException e) {
				// Should never be here
				return false;
			}
		}
		return false;
	}
	
	private static DatabaseSet getDatabaseSet(String logicDbName) {
		return DalClientFactory.getDalConfigure().getDatabaseSet(logicDbName);
	}
	
	/**
	 * Locate DB shard id by hints
	 * @param logicDbName
	 * @param hints
	 * @return
	 * @throws SQLException
	 */
	public static String locateShardId(String logicDbName, DalHints hints) throws SQLException {
		DalConfigure config = DalClientFactory.getDalConfigure();
		
		DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
		String shard = dbSet.getStrategy().locateDbShard(config, logicDbName, hints);
		dbSet.validate(shard);
		
		return shard;
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
		
		DalHints tmpHints = null;
		if(parameters != null) {
			shard = strategy.locateTableShard(config, logicDbName, tmpHints = new DalHints().setParameters(parameters));
			if(shard != null)
				return shard;
		}
		
		if(fields == null)
			return null;
		// dbSet.validate(shard);
		// We cannot validate the id, since it is inside the db
		return strategy.locateTableShard(config, logicDbName, DalHints.createIfAbsent(tmpHints).setShardColValues(fields));
	}
	
	/**
	 * Group pojos by shard id. Should be only used for DB that support sharding.
	 * 
	 * @param logicDbName
	 * @param pojos
	 * @return Grouped pojos
	 * @throws SQLException In case locate shard id faild 
	 */
	public static <T> Map<String, List<Map<String, ?>>> shuffle(String logicDbName, DalParser<T> parser, List<T> pojos) throws SQLException {
		Map<String, List<Map<String, ?>>> shuffled = new HashMap<String, List<Map<String, ?>>>();
		DalConfigure config = DalClientFactory.getDalConfigure();
		
		DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
		DalShardingStrategy strategy = dbSet.getStrategy();
		
		DalHints tmpHints = new DalHints();
		for(T pojo:pojos) {
			Map<String, ?> fields = parser.getFields(pojo);
			tmpHints.set(DalHintEnum.shardColValues, fields);
			String shardId = strategy.locateDbShard(config, logicDbName, tmpHints);
			dbSet.validate(shardId);
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
	 * Shuffle by table shard id.
	 * @param logicDbName
	 * @param pojos
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, List<Map<String, ?>>> shuffleByTable(String logicDbName, List<Map<String, ?>> pojos) throws SQLException {
		Map<String, List<Map<String, ?>>> shuffled = new HashMap<String, List<Map<String, ?>>>();
		DalConfigure config = DalClientFactory.getDalConfigure();
		
		DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
		DalShardingStrategy strategy = dbSet.getStrategy();
		
		DalHints tmpHints = new DalHints();
		for(Map<String, ?> fields: pojos) {
			tmpHints.set(DalHintEnum.shardColValues, fields);
			String shardId = strategy.locateDbShard(config, logicDbName, tmpHints);
			dbSet.validate(shardId);
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
	 * Group pojos by shard id. Should be only used for DB that support sharding.
	 * @param logicDbName
	 * @param pojos
	 * @return Grouped pojos
	 * @throws SQLException In case locate shard id faild 
	 */
	public static <T> Map<String, List<Map<String, ?>>> shuffle(String logicDbName, DalParser<T> parser, T... pojos) throws SQLException {
		return shuffle(logicDbName, parser, Arrays.asList(pojos));
	}
	
	/**
	 * Verify if shard id is already set for combined insert or batch update. 
	 * @param logicDbName
	 * @param hints
	 * @param message
	 * @throws SQLException
	 */
	public static void reqirePredefinedSharding(String logicDbName, DalHints hints, String message) throws SQLException {
		if(!isShardingEnabled(logicDbName))
			return;
		
		// Assume the out transaction already handle sharding logic
		if(DalTransactionManager.isInTransaction())
			return;
		
		String shard = locateShardId(logicDbName, hints);
		if(shard == null)
			throw new SQLException(message);
	}
	
	public static void crossShardOperationAllowed(String logicDbName, DalHints hints, String operation) throws SQLException {
		if(!isShardingEnabled(logicDbName))
			throw new SQLException(logicDbName + " is not configured with sharding strategy");

		// Assume the out transaction already handle sharding logic
		if(DalTransactionManager.isInTransaction())
			throw new SQLException(operation + " is not allowed within transaction");
		
		String shard;
		try {
			shard = locateShardId(logicDbName, hints);
		} catch (Exception e) {
			return;
		}
		if(shard != null)
			throw new SQLException(operation + " requires to be executed only when shard id can not be located in hints. sharid:" + shard);
	}
	
//	public static int[] executeByDbShard(String logicDbName, DalHints hints, List<Map<String, ?>> daoPojos, BulkTask task) throws SQLException {
//		
//	}
	
	public static <T> T executeByTableShard(String logicDbName, DalHints hints, List<Map<String, ?>> daoPojos, BulkTask<T> task) throws SQLException {
		if(isTableShardingEnabled(logicDbName)) {
			DalHints tmpHints = DalHints.copyOf(hints);
			Map<String, List<Map<String, ?>>> pojosInTable = shuffleByTable(logicDbName, daoPojos);
			List<T> results = new ArrayList<T>(pojosInTable.size());
			for(String tableShardId: pojosInTable.keySet()) {
				tmpHints.inTableShard(tableShardId);
				T result = task.execute(tmpHints, pojosInTable.get(tableShardId));
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
	
	public static interface BulkTask<T> {
		T execute(DalHints hints, List<Map<String, ?>> shaffled) throws SQLException;
		T merge(List<T> results);
	}
}
