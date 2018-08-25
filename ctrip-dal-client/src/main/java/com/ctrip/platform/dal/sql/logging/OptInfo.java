package com.ctrip.platform.dal.sql.logging;


import java.util.HashMap;
import java.util.Map;


public class OptInfo {
	public static final String KEY = "arch.dal.rw.count";
	public static final String CLIENT = "Client";
	private String version ;
	
    private static final String DB = "DB";
	private String databaseSet;
	
	private static final String DBTYPE = "DBType";
	private String databaseType;

	private static final String TABLES="Tables";
	private String tables;
	
	private static final String OPTTYPE = "OperationType";
	private String operationType;

	public OptInfo(String databaseSet, String version, String databaseType, String operationType, String tables) {
		this.databaseSet = databaseSet;
		this.version = "Java " + version;
		this.databaseType = databaseType;
		this.operationType = operationType;
		this.tables = tables;
	}
	
	public String getDatabaseSet() {
		return databaseSet;
	}
	public void setDatabaseSet(String databaseSet) {
		this.databaseSet = databaseSet;
	}
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}
	public String getOperationType() {
		return operationType;
	}
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	} 
	
	public Map<String, String> toTag(){
		Map<String, String> tag = new HashMap<String, String>();
		tag.put(DB, this.databaseSet);
		tag.put(DBTYPE, this.databaseType);
		tag.put(OPTTYPE, this.operationType);
		tag.put(CLIENT, this.version);
		tag.put(TABLES, this.tables);
		return tag;
	}
}
