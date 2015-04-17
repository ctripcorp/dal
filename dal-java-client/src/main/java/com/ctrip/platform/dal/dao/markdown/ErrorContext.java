package com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class ErrorContext {
	private String name;
	private DatabaseCategory dbCategory;
	private int errorCode;
	private String msg;
	private long cost;
	private Class<?> exType;
	private long time;
	
	public ErrorContext(String name, DatabaseCategory dbCategory, long cost, SQLException e){
		this.name = name;
		this.dbCategory = dbCategory;
		this.cost = cost;
		this.errorCode = e.getErrorCode();
		this.exType = e.getClass();
		this.msg = e.getMessage();
		this.time = System.currentTimeMillis();
	}

	public String getName() {
		return name;
	}

	public DatabaseCategory getDbCategory() {
		return dbCategory;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public Class<?> getExType() {
		return exType;
	}

	public long getTime() {
		return time;
	}

	public long getCost() {
		return cost;
	}

	public String getMsg() {
		return msg;
	}
}
