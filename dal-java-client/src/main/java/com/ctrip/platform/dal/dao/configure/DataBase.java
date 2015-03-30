package com.ctrip.platform.dal.dao.configure;


public class DataBase {
	private String name;
	private boolean master;
	private String sharding;
	private String connectionString;
	
	public DataBase(String name, 
			boolean master, 
			String sharding, 
			String connectionString) {
		this.name = name;
		this.master = master;
		this.sharding = sharding;
		this.connectionString = connectionString;
	}
	
	public String getName() {
		return name;
	}

	public boolean isMaster() {
		return master;
	}

	public String getSharding() {
		return sharding;
	}

	public String getConnectionString() {
		return connectionString;
	}
}
