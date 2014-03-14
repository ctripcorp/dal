package com.ctrip.platform.dal.dao.configure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.strategy.DalShardStrategy;

public class DatabaseSet {
	private static final String CLASS = "class";
	private static final String ENTRY_SEPARATOR = ";";
	private static final String KEY_VALUE_SEPARATOR = "=";
	
	private String name;
	private String provider;
	private String shardStrategy;
	private DalShardStrategy strategy;
	private Map<String, DataBase> databases;
	// Key is shard id, value is all database under in this shard
	private Map<String, List<DataBase>> databaseByShard = new HashMap<String, List<DataBase>>();

	public DatabaseSet(String name, String provider, String shardStrategy, Map<String, DataBase> databases) throws Exception {
		this.name = name;
		this.provider = provider;
		this.shardStrategy = shardStrategy;
		this.databases = databases;
		initShards();
		initStrategy();
	}
	
	private void initShards() throws Exception {
		// Init map by shard
		for(DataBase db: databases.values()) {
			List<DataBase> dbList = databaseByShard.get(db.getSharding());
			if(dbList == null) {
				dbList = new ArrayList<DataBase>();
				databaseByShard.put(db.getSharding(), dbList);
			}
			dbList.add(db);
		}
	}
	
	private void initStrategy() throws Exception {
		if(shardStrategy == null || shardStrategy.length() == 0)
			return;
		
		String[] values = shardStrategy.split(ENTRY_SEPARATOR);
		String[] strategyDef = values[0].split(KEY_VALUE_SEPARATOR);
		
		if(strategyDef[0].equals(CLASS))
			strategy = (DalShardStrategy)Class.forName(strategyDef[1]).newInstance();
		Map<String, String> settings = new HashMap<String, String>();
		for(int i = 1; i < values.length; i++) {
			String[] entry = values[i].split(KEY_VALUE_SEPARATOR);
			settings.put(entry[0], entry[1]);
		}
		strategy.initialize(settings);
	}

	public String getName() {
		return name;
	}

	public String getProvider() {
		return provider;
	}

	public String getShardStrategy() {
		return shardStrategy;
	}

	public Map<String, DataBase> getDatabases() {
		return databases;
	}
	public void setDatabases(Map<String, DataBase> databases) {
		this.databases = databases;
	}
	
	public Set<String> getAllShards() {
		return databaseByShard.keySet();
	}
}
