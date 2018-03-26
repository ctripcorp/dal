package com.ctrip.platform.dal.dao.strategy;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;

public abstract class AbstractRWSeparationStrategy implements DalShardingStrategy {
	@Override
	public boolean isMaster(DalConfigure configure, String logicDbName,
			DalHints hints) {
		return false;
	}
	
	/**
	 * This method is a default implementation old interface defined in DalShardingStrategy.
	 * It just route to new method with no table name
	 * 
	 * @param configure
	 * @param logicDbName
	 * @param hints
	 * @return
	 * @deprecated should use locateTableShard with table name parameter
	 */
	public String locateTableShard(DalConfigure configure, String logicDbName, DalHints hints) {
	    return locateTableShard(configure, logicDbName, null, hints);
	}
	
	/**
	 * Call the old way of getting table shard id to make sure subclass compiles ok and not 
	 * break existing logic
	 */
	public String locateTableShard(DalConfigure configure, String logicDbName, String tabelName, DalHints hints) {
	    return locateTableShard(configure, logicDbName, hints);
	}
}
