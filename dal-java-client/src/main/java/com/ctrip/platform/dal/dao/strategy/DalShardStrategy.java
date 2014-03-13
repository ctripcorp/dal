package com.ctrip.platform.dal.dao.strategy;

import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;

public interface DalShardStrategy {
	/**
	 * Initialize strategy
	 * @param settings
	 */
	void initialize(Map<String, String> settings);

	/**
	 * For most of the case, it will return only 1
	 * If this operation requires cross shard execution, the method will return multiple shard id.
	 * @param configure
	 * @param logicDbName
	 * @param hint
	 * @return
	 */
	Set<String> locateShards(DalConfigure configure, String logicDbName, DalHints hints);
}
