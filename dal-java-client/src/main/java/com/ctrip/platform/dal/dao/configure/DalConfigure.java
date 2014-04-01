package com.ctrip.platform.dal.dao.configure;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ctrip.datasource.locator.DataSourceLocator;

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
	
	public void warmUpConnections() {
		for(DatabaseSet dbSet: databaseSets.values()){
			Map<String, DataBase> dbs = dbSet.getDatabases();
			for(DataBase db: dbs.values()) {
				Connection conn = null;
				try {
					conn = DataSourceLocator.newInstance().getDataSource(db.getConnectionString()).getConnection();
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
}
