package com.ctrip.platform.dal.dao.helper;

import java.sql.SQLException;
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
import com.ctrip.platform.dal.dao.strategy.DalShardStrategy;

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
		DalConfigure config = DalClientFactory.getDalConfigure();
		
		DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
		String shard = dbSet.getStrategy().locateShard(config, logicDbName, hints);
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
	public static <T> Map<String, List<Map<String, ?>>> shaffle(String logicDbName, DalParser<T> parser, T[] pojos) throws SQLException {
		Map<String, List<Map<String, ?>>> shaffled = new HashMap<String, List<Map<String, ?>>>();
		DalConfigure config = DalClientFactory.getDalConfigure();
		
		DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
		DalShardStrategy strategy = dbSet.getStrategy();
		
		DalHints tmpHints = new DalHints();
		for(T pojo:pojos) {
			Map<String, ?> fields = parser.getFields(pojo);
			tmpHints.set(DalHintEnum.shardColValues, fields);
			String shardId = strategy.locateShard(config, logicDbName, tmpHints);
			List<Map<String, ?>> pojosInShard = shaffled.get(shardId);
			if(pojosInShard == null) {
				pojosInShard = new LinkedList<Map<String, ?>>();
				shaffled.put(shardId, pojosInShard);
			}
			pojosInShard.add(fields);
		}
		
		return shaffled;
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
		
		throw new SQLException(message);
	}
	
	public static <T> void executeByShard(String logicDbName, DalParser<T> parser, T[] pojos, DalHints hints, BulkTask task) throws SQLException {
		Map<String, List<Map<String, ?>>> shaffled = shaffle(logicDbName, parser, pojos);
		
		for(;;){}
	}
	
	public static interface BulkTask {
		void execute(Map<String, List<Map<String, ?>>> shaffled);
	}
}
