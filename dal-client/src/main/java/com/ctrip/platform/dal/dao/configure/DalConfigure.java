package com.ctrip.platform.dal.dao.configure;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.task.DalTaskFactory;

public class DalConfigure {
	private String name;
	private Map<String, DatabaseSet> databaseSets = new ConcurrentHashMap<String, DatabaseSet>();
	private DalLogger dalLogger;
	private DalConnectionLocator locator;
	private DalTaskFactory facory;
	
	public DalConfigure(String name, Map<String, DatabaseSet> databaseSets, DalLogger dalLogger, DalConnectionLocator locator, DalTaskFactory facory) {
		this.name = name;
		this.databaseSets.putAll(databaseSets);
		this.dalLogger = dalLogger;
		this.locator = locator;
		this.facory = facory;
	}
	
	public String getName() {
		return name;
	}
	
	public DatabaseSet getDatabaseSet(String logicDbName) {
		if (!databaseSets.containsKey(logicDbName))
			throw new IllegalArgumentException(
					"Can not find definition for Database Set "
							+ logicDbName
							+ ". Please check spelling or define it in Dal.config");

		return databaseSets.get(logicDbName);
	}
	
	public void warmUpConnections() {
		for(DatabaseSet dbSet: databaseSets.values()){
			Map<String, DataBase> dbs = dbSet.getDatabases();
			for(DataBase db: dbs.values()) {
				Connection conn = null;
				try {
					conn = locator.getConnection(db.getConnectionString());
				} catch (Throwable e) {
					e.printStackTrace();
				}finally {
					if(conn != null)
						try {
							conn.close();
						} catch (Throwable e) {
							e.printStackTrace();
						}
				}
			}
		}
	}
	
	public Set<String> getDatabaseSetNames() {
		return databaseSets.keySet();
	}
	
	public Set<String> getDataSourceNames(){
		Set<String> alldbs = new HashSet<String>();
		for (DatabaseSet set : this.databaseSets.values()) {
			for (DataBase db : set.getDatabases().values()) {
				alldbs.add(db.getConnectionString());
			}
		}
		return alldbs;
	}

	public DalLogger getDalLogger() {
		return dalLogger;
	}

	public DalConnectionLocator getLocator() {
		return locator;
	}

	public DalTaskFactory getFacory() {
		return facory;
	}
}
