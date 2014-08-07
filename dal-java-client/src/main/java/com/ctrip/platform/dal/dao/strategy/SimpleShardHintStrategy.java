package com.ctrip.platform.dal.dao.strategy;

import java.util.Map;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;

public class SimpleShardHintStrategy extends AbstractRWSeparationStrategy implements DalShardingStrategy {

	@Override
	public void initialize(Map<String, String> settings) {
	}

	@Override
	public String locateDbShard(DalConfigure configure, String logicDbName,
			DalHints hints) {
		return hints.getString(DalHintEnum.shard);
	}
	
	@Override
	public String locateTableShard(DalConfigure configure, String logicDbName,
			DalHints hints) {
		return hints.getString(DalHintEnum.tableShard);
	}

	@Override
	public boolean isShardingByDb() {
		return true;
	}

	@Override
	public boolean isShardingByTable() {
		return true;
	}
}
