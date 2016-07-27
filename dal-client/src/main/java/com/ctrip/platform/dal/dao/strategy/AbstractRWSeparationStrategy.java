package com.ctrip.platform.dal.dao.strategy;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;

public abstract class AbstractRWSeparationStrategy implements DalShardingStrategy {
	@Override
	public boolean isMaster(DalConfigure configure, String logicDbName,
			DalHints hints) {
		return false;
	}
}
