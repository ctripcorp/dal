package com.ctrip.platform.dal.dao.sqlbuilder;

import com.ctrip.platform.dal.dao.StatementParameters;

public class SimpleQueryBuilder implements SqlBuilder {
	private String findTemplate;
	private String tableName;
	private String whereClause;
	private StatementParameters parameters;
	
	public SimpleQueryBuilder(String findTemplate, String tableName, String whereClause, StatementParameters parameters) {
		this.findTemplate = findTemplate;
		this.tableName = tableName;
		this.whereClause = whereClause;
		this.parameters = parameters;
	}
	
	public String build(){
		return build(tableName);
	}
	
	@Override
	public String buildWith(String shardStr) {
		return build(tableName + shardStr);
	}
	
	private String build(String effectiveTableName) {
		return String.format(findTemplate, effectiveTableName, whereClause);
	}

	@Override
	public StatementParameters buildParameters() {
		return parameters;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

}
