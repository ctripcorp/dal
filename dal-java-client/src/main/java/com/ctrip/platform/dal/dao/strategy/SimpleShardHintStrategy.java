package com.ctrip.platform.dal.dao.strategy;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;

public class SimpleShardHintStrategy implements DalShardStrategy {

	@Override
	public void initialize(Map<String, String> settings) {
	}

	@Override
	public Set<String> locateShards(DalConfigure configure, String logicDbName,
			DalHints hints) {
		String shard = hints.getString(DalHintEnum.shard);
		
		if(shard != null) {
			Set<String> shards = new HashSet<String>();
			shards.add(shard);
			return shards;
		}
		
		return (Set<String>)hints.get(DalHintEnum.shards);
	}
}
