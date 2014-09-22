package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseSetEntry implements Comparable<DatabaseSetEntry> {

	private int id;
	private String name;
	private String databaseType;
	private String sharding;
	private String connectionString;
	private int databaseSet_Id;
	
	public static DatabaseSetEntry visitRow(ResultSet rs) throws SQLException {
		DatabaseSetEntry entry = new DatabaseSetEntry();
		entry.setId(rs.getInt(1));
		entry.setName(rs.getString(2));
		entry.setDatabaseType(rs.getString(3));
		entry.setSharding(rs.getString(4));
		entry.setConnectionString(rs.getString(5));
		entry.setDatabaseSet_Id(rs.getInt(6));
		return entry;
	}
	
	@Override
	public int compareTo(DatabaseSetEntry o) {
		return (this.id+this.name+this.databaseType+this.sharding+this.connectionString).compareTo(o.getId()+
				o.getName()+o.getDatabaseType()+o.getSharding()+o.getConnectionString());
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}
	public String getSharding() {
		return sharding == null ? "" : sharding;
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
	public int getDatabaseSet_Id() {
		return databaseSet_Id;
	}
	public void setDatabaseSet_Id(int databaseSet_Id) {
		this.databaseSet_Id = databaseSet_Id;
	}

}
