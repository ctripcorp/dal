package com.ctrip.platform.dal.dao.configure;

import java.util.List;
import java.util.Map;

public class DatabaseSet {
	private String name;
	private String provider;
	private String shardStrategy;
	private Map<String, DataBase> databases;
	// Key is shard id, value is all database under in this shard
	private Map<String, List<DataBase>> databaseByShard;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getShardStrategy() {
		return shardStrategy;
	}
	public void setShardStrategy(String shardStrategy) {
		this.shardStrategy = shardStrategy;
	}
	public Map<String, DataBase> getDatabases() {
		return databases;
	}
	public void setDatabases(Map<String, DataBase> databases) {
		this.databases = databases;
	}
}
