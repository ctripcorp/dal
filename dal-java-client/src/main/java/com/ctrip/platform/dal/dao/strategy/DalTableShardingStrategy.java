package com.ctrip.platform.dal.dao.strategy;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;

public interface DalTableShardingStrategy extends DalShardingStrategy {
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
