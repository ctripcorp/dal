package com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class ErrorContext {
	private String name;
	private DatabaseCategory dbCategory;
	private SQLException e;
	private long cost;
	private long time;
	
	public ErrorContext(String name, DatabaseCategory dbCategory, long cost, SQLException e){
		this.name = name;
		this.dbCategory = dbCategory;
		this.cost = cost;
		this.e = e;
		this.time = System.currentTimeMillis();
	}

	public String getName() {
		return name;
	}

	public DatabaseCategory getDbCategory() {
		return dbCategory;
	}

	public int getErrorCode() {
		return e.getErrorCode();
	}

	public Class<?> getExType() {
		return e.getClass();
	}

	public long getTime() {
		return time;
	}

	public long getCost() {
		return cost;
	}

	public String getMsg() {
		return e.getMessage();
	}
}
