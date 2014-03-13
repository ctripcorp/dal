package com.ctrip.platform.dal.dao.configure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.strategy.DalShardStrategy;

public class DatabaseSet {
	private String name;
	private String provider;
	private String shardStrategy;
	private DalShardStrategy strategy;
	private Map<String, DataBase> databases;
	// Key is shard id, value is all database under in this shard
	private Map<String, List<DataBase>> databaseByShard = new HashMap<String, List<DataBase>>();

	public DatabaseSet(String name, String provider, String shardStrategy, Map<String, DataBase> databases) {
		this.name = name;
		this.provider = provider;
		this.shardStrategy = shardStrategy;
		this.databases = databases;
		
		// Init map by shard
		for(DataBase db: databases.values()) {
			List<DataBase> dbList = databaseByShard.get(db.getName());
			if(dbList == null) {
				dbList = new ArrayList<DataBase>();
				databaseByShard.put(db.getName(), dbList);
			}
			dbList.add(db);
		}
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
