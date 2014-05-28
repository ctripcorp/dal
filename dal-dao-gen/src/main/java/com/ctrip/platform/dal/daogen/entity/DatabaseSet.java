package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseSet implements Comparable<DatabaseSet> {

	private int id;
	private String name;
	private String provider;
	private String shardingStrategy;
	private int groupId;
	
	public static DatabaseSet visitRow(ResultSet rs) throws SQLException {
		DatabaseSet set = new DatabaseSet();
		set.setId(rs.getInt(1));
		set.setName(rs.getString(2));
		set.setProvider(rs.getString(3));
		set.setShardingStrategy(rs.getString(4));
		set.setGroupId(rs.getInt(5));
		return set;
	}
	
	@Override
	public int compareTo(DatabaseSet o) {
		return this.name.compareTo(o.getName());
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
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getShardingStrategy() {
		return shardingStrategy;
	}
	public void setShardingStrategy(String shardingStrategy) {
		this.shardingStrategy = shardingStrategy;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
}
