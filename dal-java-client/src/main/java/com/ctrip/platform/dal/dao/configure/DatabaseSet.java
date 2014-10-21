package com.ctrip.platform.dal.dao.configure;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.client.DalHA;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;
import com.ctrip.platform.dal.dao.strategy.DalShardingStrategy;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DatabaseSet {
	public static final String SQL_PROVIDER = "sqlProvider";
	public static final String MYSQL_PROVIDER = "mySqlProvider";

	private static final String CLASS = "class";
	private static final String ENTRY_SEPARATOR = ";";
	private static final String KEY_VALUE_SEPARATOR = "=";
	
	private String name;
	private String provider;
	private DatabaseCategory dbCategory;

	private DalShardingStrategy strategy;
	private Map<String, DataBase> databases;
	// Key is shard id, value is all database under in this shard
	private Map<String, List<DataBase>> masterDbByShard = new HashMap<String, List<DataBase>>();
	private Map<String, List<DataBase>> slaveDbByShard = new HashMap<String, List<DataBase>>();

	private List<DataBase> masterDbs = new ArrayList<DataBase>();
	private List<DataBase> slaveDbs = new ArrayList<DataBase>();
	/**
	 * The target DB set does not support shard
	 * @param name
	 * @param provider
	 * @param databases
	 * @throws Exception
	 */
	public DatabaseSet(String name, String provider, Map<String, DataBase> databases) throws Exception {
		this(name, provider, null, databases);
	}
	
	public DatabaseSet(String name, String provider, String shardStrategy, Map<String, DataBase> databases) throws Exception {
		this.name = name;
		this.provider = provider;
		initDbCategory();
		this.databases = databases;

		initStrategy(shardStrategy);
		initShards();
	}
	
	private void initDbCategory() {
		if(provider == null)
			throw new RuntimeException("The provider is NULL in Dal.config for databaseSet:" + name);
		
		if(provider.equals(SQL_PROVIDER)) {
			dbCategory = DatabaseCategory.SqlServer;
			return;
		}
		
		if(provider.equals(MYSQL_PROVIDER)) {
			dbCategory = DatabaseCategory.MySql;
			return;
		}
		
		throw new RuntimeException("The provider: " + provider + " is not recoganized in Dal.config for databaseSet:" + name);
	}
	
	private void initStrategy(String shardStrategy) throws Exception {
		if(shardStrategy == null || shardStrategy.length() == 0)
			return;
		
		String[] values = shardStrategy.split(ENTRY_SEPARATOR);
		String[] strategyDef = values[0].split(KEY_VALUE_SEPARATOR);
		
		if(strategyDef[0].equals(CLASS))
			strategy = (DalShardingStrategy)Class.forName(strategyDef[1]).newInstance();
		Map<String, String> settings = new HashMap<String, String>();
		for(int i = 1; i < values.length; i++) {
			String[] entry = values[i].split(KEY_VALUE_SEPARATOR);
			settings.put(entry[0], entry[1]);
		}
		strategy.initialize(settings);
	}

	private void initShards() throws Exception {
		if(strategy == null || strategy.isShardingByDb() == false){
			// Init with no shard support
			for(DataBase db: databases.values()) {
				if(db.isMaster())
					masterDbs.add(db);
				else
					slaveDbs.add(db);
			}
		}else{
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
	}
	
	public String getName() {
		return name;
	}

	public String getProvider() {
		return provider;
	}

	public DatabaseCategory getDatabaseCategory() {
		return dbCategory;
	}
	
	public boolean isShardingSupported() {
		return strategy != null && strategy.isShardingByDb();
	}

	public boolean isTableShardingSupported(String tableName) {
		return strategy != null && strategy.isShardingByTable() && strategy.isShardingEnable(tableName);
	}

	public Map<String, DataBase> getDatabases() {
		return databases;
	}
	
	public void validate(String shard) throws SQLException {
		if(!masterDbByShard.containsKey(shard))
			throw new SQLException("No shard defined for id: " + shard);
	}
	
	public Set<String> getAllShards() {
		return masterDbByShard.keySet();
	}

	public DalShardingStrategy getStrategy() throws SQLException {
		if(strategy == null)
			throw new SQLException("No sharding stradegy defined");
		return strategy;
	}
	
	public List<DataBase> getMasterDbs(String shard) {
		return masterDbByShard.get(shard);
	}

	public List<DataBase> getSlaveDbs(String shard) {
		return slaveDbByShard.get(shard);
	}
	
	public String getRandomRealDbName(DalHA ha, String shard, boolean isMaster, boolean isSelect) throws DalException {
		return getRandomRealDbName(ha, isMaster, isSelect, getMasterDbs(shard), getSlaveDbs(shard));
	}
	
	public String getRandomRealDbName(DalHA ha, boolean isMaster, boolean isSelect) throws DalException {
		return getRandomRealDbName(ha, isMaster, isSelect, masterDbs, slaveDbs);
	}
	
	private String getRandomRealDbName(DalHA ha, boolean isMaster, boolean isSelect, List<DataBase> masterCandidates, List<DataBase> slaveCandidates) throws DalException {
		if (isMaster)
			return getRandomRealDbName(ha, masterCandidates);
		
		if (isSelect && slaveCandidates.size() > 0)
			return getRandomRealDbName(ha, slaveCandidates);

		return getRandomRealDbName(ha, masterCandidates);
	}
	
	private String getRandomRealDbName(DalHA ha, List<DataBase> dbs) throws DalException {
		List<String> availableDbNames = this.getNotMarkdownDbNames(dbs);
		if(ha == null || availableDbNames.size() == 1){
			int index = (int)(Math.random() * availableDbNames.size());	
			return availableDbNames.get(index);
		}else{
			List<String> dbNames = new ArrayList<String>();
			for (String database : availableDbNames) {
				if(!ha.contains(database))
					dbNames.add(database);
			}
			if(dbNames.isEmpty()){
				ha.setOver(true);
				return null;
			}else{
				int index = (int)(Math.random() * dbNames.size());
				ha.addDB(dbNames.get(index));
				return dbNames.get(index);
			}
		}
	}
	
	private List<String> getNotMarkdownDbNames(List<DataBase> dbs) throws DalException{
		List<String> dbNames = new ArrayList<String>();
		List<String> markdownDbNames = new ArrayList<String>();
		for (DataBase database : dbs) {
			if(MarkdownManager.isMarkdown(database.getConnectionString())){
				markdownDbNames.add(database.getConnectionString());
			}else{
				dbNames.add(database.getConnectionString());
			}
		}
		if(dbNames.isEmpty()){
			if(!markdownDbNames.isEmpty())
				throw new DalException(ErrorCode.MarkdownConnection, StringUtils.join(markdownDbNames, ","));
		}
		
		return dbNames;
	}
}
