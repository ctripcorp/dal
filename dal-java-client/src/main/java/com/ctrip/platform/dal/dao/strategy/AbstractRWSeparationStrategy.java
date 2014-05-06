package com.ctrip.platform.dal.dao.strategy;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.logging.DalEventEnum;

public abstract class AbstractRWSeparationStrategy implements DalShardStrategy {
	@Override
	public boolean isMaster(DalConfigure configure, String logicDbName,
			DalHints hints) {
		if(hints.is(DalHintEnum.masterOnly))
			return true;
		
		// For query, we default to slave
		return hints.get(DalHintEnum.operation) != DalEventEnum.QUERY;
	}
}
