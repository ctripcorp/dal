package com.ctrip.platform.dal.dao.strategy;

import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;

public interface DalShardStrategy {
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
	 * Locate target shard that the operation is performed. 
	 * If this operation requires cross shard execution, using Cross Shard Manager.
	 * @param configure
	 * @param logicDbName
	 * @param hints
	 * @return
	 */
	String locateDbShard(DalConfigure configure, String logicDbName, DalHints hints);
	
	/**
	 * Locate table shard suffix. The table name + suffix will be used as real table name 
	 * If this operation requires cross shard execution, using Cross Shard Manager.
	 * @param configure
	 * @param logicDbName
	 * @param hints
	 * @return
	 */
	String locateTableShard(DalConfigure configure, String logicDbName, DalHints hints);

}
