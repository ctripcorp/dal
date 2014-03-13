package com.ctrip.platform.dal.dao.configure;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHints;

public class DalConfigure {
	private String name;
	private Map<String, DatabaseSet> databaseSets = new HashMap<String, DatabaseSet>();
	
	public DalConfigure(String name, Map<String, DatabaseSet> databaseSets) {
		this.name = name;
		this.databaseSets = databaseSets;
	}
	
	public String getName() {
		return name;
	}

	public Set<String> getShards(String logicDbName) {
		DatabaseSet dbSet = databaseSets.get(logicDbName);
		if(dbSet == null)
			return null;
		return dbSet.getAllShards();
	}
	
	public String locateDbName(String logicDbName, String realDbName) {
		DatabaseSet dbSet = databaseSets.get(logicDbName);
		if(dbSet == null)
			return null;
		
		return dbSet.getDatabases().get(realDbName).getName();
	}
	
	public String locateDateBaseName(DalHints hint) {
		
		return null;
	}
	
	/**
	 * For write
	 */
	public String locateMasterDbName(String logicDbName, String shard) {
		DatabaseSet dbSet = databaseSets.get(logicDbName);
		if(dbSet == null)
			return null;
		
//		return dbSet.getDatabases().get(realDbName).getName();
		return null;
	}

	/**
	 * For read
	 */
	public String locateSlaveDbName(String logicDbName, String shard) {
		
		return null;
	}
}
