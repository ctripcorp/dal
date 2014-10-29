package com.ctrip.platform.dal.logging.sql;

import java.util.HashMap;
import java.util.Map;

import com.ctrip.platform.dal.sql.logging.DalClientVersion;

public class OptInfo {
	public static final String KEY = "arch.dal.rw.count";
	
	public static final String CLIENT = "Client";
	private static final String CLIENT_NAME = "Java " + DalClientVersion.version;
	
    private static final String DB = "DB";
	private String databaseSet;
	
	private static final String DBTYPE = "DBType";
	private String databaseType;
	
	private static final String OPTTYPE = "OperationType";
	private String operationType;
	
	public OptInfo(String databaseSet, String databaseType,String operationType){
		this.databaseSet = databaseSet;
		this.databaseType = databaseType;
		this.operationType = operationType;
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
		tag.put(CLIENT, CLIENT_NAME);
		return tag;
	}
}
