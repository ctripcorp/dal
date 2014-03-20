package com.ctrip.platform.dal.dao.strategy;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;

public abstract class AbstractRWSeparationStrategy implements DalShardStrategy {
	@Override
	public boolean useMaster(DalConfigure configure, String logicDbName,
			DalHints hints) {
		// TODO Auto-generated method stub
		return false;
	}
}
