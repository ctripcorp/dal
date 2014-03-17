package com.ctrip.platform.dal.dao.configure;

import java.util.HashMap;
import java.util.Map;

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
	
	public DatabaseSet getDatabaseSet(String logicDbName) {
		return databaseSets.get(logicDbName);
	}
}
