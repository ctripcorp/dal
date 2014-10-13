package com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

public class MarkContext {
	private String name;
	private String dbtype;
	private int errorCode;
	private String msg;
	private long cost;
	private Class<?> exType;
	private long time;
	
	public MarkContext(String name, String type, long cost, SQLException e){
		this.name = name;
		this.dbtype = type;
		this.cost = cost;
		this.errorCode = e.getErrorCode();
		this.exType = e.getClass();
		this.msg = e.getMessage();
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

	public long getCost() {
		return cost;
	}

	public String getMsg() {
		return msg;
	}
}
