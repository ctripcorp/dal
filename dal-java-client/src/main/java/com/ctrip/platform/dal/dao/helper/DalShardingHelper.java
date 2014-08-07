package com.ctrip.platform.dal.dao.helper;

import java.sql.SQLException;
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
	
	public static boolean isShardingEnabled(String logicDbName) {
		return DalClientFactory.getDalConfigure().getDatabaseSet(logicDbName).isShardingSupported();
	}
	
	public static boolean isShardDecided(DalHints hints) {
		return hints.getShardId() != null;
	}
	
	public static <T> String locateShardId(String logicDbName, DalParser<T> parser, T pojo) throws SQLException {
		return locateShardId(logicDbName, new DalHints().set(DalHintEnum.shardColValues, parser.getFields(pojo)));
	}
	
	public static <T> String locateShardId(String logicDbName, DalParser<T> parser, StatementParameters parameters) throws SQLException {
		return locateShardId(logicDbName, new DalHints().set(DalHintEnum.parameters, parameters));
	}
	
	/**
	 * Locate by hints
	 * @param logicDbName
	 * @param hints
	 * @return
	 * @throws SQLException
	 */
	public static String locateShardId(String logicDbName, DalHints hints) throws SQLException {
		if(isShardDecided(hints))
			return hints.getShardId();
		
		DalConfigure config = DalClientFactory.getDalConfigure();
		
		DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
		String shard = dbSet.getStrategy().locateDbShard(config, logicDbName, hints);
		dbSet.validate(shard);
		
		return shard;
	}

	/**
	 * Group pojos by shard id. Should be only used for DB that support sharding.
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
		
		if(isShardDecided(hints))
			return;
		
		// Assume the out transaction already handle sharding logic
		if(DalTransactionManager.isInTransaction())
			return;
		
		String shard = locateShardId(logicDbName, hints);
		if(shard == null)
			throw new SQLException(message);
	}
	
	public static void crossShardOperationAllowed(String logicDbName, DalHints hints, String operation) throws SQLException {
		// Assume the out transaction already handle sharding logic
		if(DalTransactionManager.isInTransaction())
			throw new SQLException(operation + " is not allowed within transaction");
	}
	
	public static <T> void executeByShard(String logicDbName, DalParser<T> parser, T[] pojos, DalHints hints, BulkTask task) throws SQLException {
		Map<String, List<Map<String, ?>>> shaffled = shuffle(logicDbName, parser, pojos);
		
		for(;;){}
	}
	
	public static interface BulkTask {
		void execute(Map<String, List<Map<String, ?>>> shaffled);
	}
}
