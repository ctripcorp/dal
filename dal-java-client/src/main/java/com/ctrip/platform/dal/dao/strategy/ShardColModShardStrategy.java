package com.ctrip.platform.dal.dao.strategy;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;

public class ShardColModShardStrategy implements DalShardStrategy {
	public static final String COLUMNS = "columns";
	public static final String MOD = "mod";
	
	private String[] columns;
	private Integer mod;
	/**
	 * columns are separated by ','
	 * @Override
	 */
	public void initialize(Map<String, String> settings) {
		columns = settings.get(COLUMNS).split(",");
		mod = Integer.parseInt(settings.get(MOD));
	}

	@Override
	public Set<String> locateShards(DalConfigure configure, String logicDbName,
			DalHints hints) {
		if(columns.length == 0)
			return null;
		
		Map<String, Integer> shardColValues = (Map<String, Integer>)hints.get(DalHintEnum.shardColValues);
		
		if(shardColValues != null)
			//configure.getShards(logicDbName);
			return null;//
		
		Set<String> shards = new HashSet<String>();
		for(String column: columns) {
			Integer id = shardColValues.get(column);
			if(id != null)
				shards.add(String.valueOf(id%mod));
		}
		return shards;
	}
}
