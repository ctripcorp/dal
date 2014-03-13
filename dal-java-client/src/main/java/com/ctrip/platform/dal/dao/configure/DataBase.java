package com.ctrip.platform.dal.dao.configure;

public class DataBase {
	private String name;
	private boolean master;
	private String databaseType;
	private String sharding;
	private String connectionString;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isMaster() {
		return master;
	}
	public void setMaster(boolean master) {
		this.master = master;
	}
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}
	public String getSharding() {
		return sharding;
	}
	public void setSharding(String sharding) {
		this.sharding = sharding;
	}
	public String getConnectionString() {
		return connectionString;
	}
	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}
}
