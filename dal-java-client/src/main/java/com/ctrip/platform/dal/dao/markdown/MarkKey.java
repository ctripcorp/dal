package com.ctrip.platform.dal.dao.markdown;

public class MarkKey {
	private String name;
	private String dbtype;
	private Throwable exception;
	
	public MarkKey(String name, String type, Throwable e){
		this.name = name;
		this.dbtype = type;
		this.exception = e;
	}
	
	public String getName() {
		return name;
	}

	public String getDbtype() {
		return dbtype;
	}

	public Throwable getException() {
		return exception;
	}
}
