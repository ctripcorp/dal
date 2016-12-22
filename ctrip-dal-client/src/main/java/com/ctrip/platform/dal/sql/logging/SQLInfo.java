package com.ctrip.platform.dal.sql.logging;

import java.util.HashMap;
import java.util.Map;

public class SQLInfo {
	public static final String COUNT = "arch.dal.sql.count";
	public static final String COST = "arch.dal.sql.cost";
	public static final String TITAN = "arch.dal.titan.access";
	public static final String ENV = "environment";
	public static final String SUB_ENV = "subEnvironment";
	public static final String TITAN_COST = "cost";
	
	public static final String CLIENT = "Client";
	private String version;

	private static final String DAO = "DAO";
	private String dao;
	
	private static final String METHOD = "Method";
	private String method;
	
	private static final String SIZE = "Size";
	private Integer size;
	
	private static final String STATUS = "Status";
	private String status;
	
	public SQLInfo(String dao, String version, String method, int size, String status){
		this.dao = dao;
		this.method = method;
		this.version = "Java " + version;
		if (size < 200) {
            this.size = 200;
        } else if (size < 1000) {
        	this.size = 1000;
        } else if (size < 5000) {
        	this.size = 5000;
        } else {
        	this.size = 99999;
        }
		this.status = status;
	}
	
	public String getDao() {
		return dao;
	}
	public void setDao(String dao) {
		this.dao = dao;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Map<String, String> toTag(){
		Map<String, String> tag = new HashMap<String, String>();
		tag.put(DAO, this.dao);
		tag.put(METHOD, this.method);
		tag.put(SIZE, this.size.toString());
		tag.put(STATUS, this.status);
		tag.put(CLIENT, this.version);
		
		return tag;
	}
}
