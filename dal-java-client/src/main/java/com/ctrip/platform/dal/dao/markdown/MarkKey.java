package com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;

public class MarkKey {
	private String name;
	private String dbtype;
	private int errorCode;
	private Class<?> exType;
	private long time;
	
	public MarkKey(String name, String type, SQLException e){
		this.name = name;
		this.dbtype = type;
		this.errorCode = e.getErrorCode();
		this.exType = e.getClass();
		this.time = System.currentTimeMillis();
	}

	public String getName() {
		return name;
	}

	public String getDbtype() {
		return dbtype;
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
}
