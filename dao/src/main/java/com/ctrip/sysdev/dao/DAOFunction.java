package com.ctrip.sysdev.dao;

import java.util.Map;

import com.ctrip.sysdev.enums.AvailableTypeEnum;

/**
 * The requirement of a DAO function, the fields the sql will return,
 * the parameters must be pass and the result sql that will be executed
 * 
 * @author gawu
 *
 */
public class DAOFunction {
	
	/**
	 * (Query Only) The fields the sql will return
	 */
	private String[] fields;
	
	private Map<Integer, Class<?>> requiredParams;
	
	private Map<Integer, AvailableTypeEnum> resultFields;
	
	private String sql;

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public Map<Integer, Class<?>> getRequiredParams() {
		return requiredParams;
	}

	public void setRequiredParams(Map<Integer, Class<?>> requiredParams) {
		this.requiredParams = requiredParams;
	}

	public Map<Integer, AvailableTypeEnum> getResultFields() {
		return resultFields;
	}

	public void setResultFields(Map<Integer, AvailableTypeEnum> resultFields) {
		this.resultFields = resultFields;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	
	
}
