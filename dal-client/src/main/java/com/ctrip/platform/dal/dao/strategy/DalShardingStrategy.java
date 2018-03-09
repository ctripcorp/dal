package com.ctrip.platform.dal.dao.strategy;

import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;

public interface DalShardingStrategy {
	/**
	 * Key name of table shard separator in sharding strategy config
	 */
	public static final String SEPARATOR = "separator";
	
	/**
	 * Initialize strategy
	 * @param settings
	 */
	void initialize(Map<String, String> settings);

	/**
	 * Check if master should be used for current operation.
	 * @param configure
	 * @param logicDbName
	 * @param hints
	 * @return
	 */
	boolean isMaster(DalConfigure configure, String logicDbName, DalHints hints);
	
	/**
	 * Check if the shard is by DB
	 * @return
	 */
	boolean isShardingByDb();

	/**
	 * Locate target shard that the operation is performed. 
	 * If this operation requires cross shard execution, using Cross Shard Manager.
	 * @param configure
	 * @param logicDbName
	 * @param hints
	 * @return shard Id for DB, null if not located
	 */
	String locateDbShard(DalConfigure configure, String logicDbName, DalHints hints);
	
	/**
	 * Check if the shard is by table
	 * @return
	 */
	boolean isShardingByTable();
	
	/**
	 * Check if sharding is enabled for this table. The assumption is not every table in DB is sharded by table
	 * @param tableName
	 * @return
	 */
	boolean isShardingEnable(String tableName);
	
	/**
	 * Locate table shard suffix. The table name + suffix will be used as real table name 
	 * If this operation requires cross shard execution, using Cross Shard Manager.
	 * @param configure
	 * @param logicDbName
	 * @param hints
	 * @return
	 */
	String locateTableShard(DalConfigure configure, String logicDbName, String tabelName, DalHints hints);
	
	/**
	 * Get the separator between raw table name and table shard id
	 * @return shard Id for table, null if not located
	 */
	String getTableShardSeparator();

}
