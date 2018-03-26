package com.ctrip.platform.dal.dao.strategy;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;

public class SimpleShardHintStrategy extends AbstractRWSeparationStrategy implements DalShardingStrategy {
	public static final String SHARD_BY_DB = "shardByDb";
	public static final String SHARD_BY_TABLE = "shardByTable";
	public static final String SHARDED_TABLES = "shardedTables";

	private boolean shardByDb;
	private boolean shardByTable;
	private String separator;
	private Set<String> shardedTables = new HashSet<String>();
	
	@Override
	public void initialize(Map<String, String> settings) {
		if(settings.containsKey(SHARD_BY_DB)) {
			shardByDb = Boolean.parseBoolean(settings.get(SHARD_BY_DB));
		}
		
		if(settings.containsKey(SHARD_BY_TABLE)) {
			shardByTable = Boolean.parseBoolean(settings.get(SHARD_BY_TABLE));
		}
		
		if(settings.containsKey(SHARDED_TABLES)) {
			String[] tables = settings.get(SHARDED_TABLES).split(",");
			for(String table: tables)
				shardedTables.add(table.toLowerCase().trim());
		}
		
		if(settings.containsKey(SEPARATOR)) {
			separator = settings.get(SEPARATOR);
		}
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
		return shardByDb;
	}

	@Override
	public boolean isShardingByTable() {
		return shardByTable;
	}

	@Override
	public boolean isShardingEnable(String tableName) {
		return shardedTables.contains(tableName.toLowerCase());
	}

	@Override
	public String getTableShardSeparator() {
		return separator;
	}
}
