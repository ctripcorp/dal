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
	private Map<String, List<DataBase>> masterDbByShard = new HashMap<String, List<DataBase>>();
	private Map<String, List<DataBase>> slaveDbByShard = new HashMap<String, List<DataBase>>();

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
			Map<String, List<DataBase>> dbByShard = db.isMaster() ?
					masterDbByShard : slaveDbByShard;
				
			List<DataBase> dbList = dbByShard.get(db.getSharding());
			if(dbList == null) {
				dbList = new ArrayList<DataBase>();
				dbByShard.put(db.getSharding(), dbList);
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
	
	public Set<String> getAllShards() {
		return masterDbByShard.keySet();
	}

	public DalShardStrategy getStrategy() {
		return strategy;
	}
	
	public List<DataBase> getMasterDbs(String shard) {
		return masterDbByShard.get(shard);
	}

	public List<DataBase> getSlaveDbs(String shard) {
		return slaveDbByShard.get(shard);
	}
	
	public String getRandomRealDbName(String shard, boolean isMaster, boolean isSelect) {
		List<DataBase> dbs;
		if (isMaster)
			return getRandomRealDbName(getMasterDbs(shard));
		
		if (isSelect)
			getRandomRealDbName(getSlaveDbs(shard));

		return getRandomRealDbName(getMasterDbs(shard));
	}
	
	private String getRandomRealDbName(List<DataBase> dbs) {
		int index = (int)(Math.random() * dbs.size());
		return dbs.get(index).getConnectionString();
	}
}
