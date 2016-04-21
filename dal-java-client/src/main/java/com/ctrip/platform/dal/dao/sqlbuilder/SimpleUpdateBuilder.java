package com.ctrip.platform.dal.dao.sqlbuilder;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.StatementParameters;

public class SimpleUpdateBuilder implements SqlBuilder {
	private String tableName;
	private String updateSql;
	private StatementParameters parameters;
	
	public SimpleUpdateBuilder(String tableName, String updateSql, DatabaseCategory dbCategory) {
		this.tableName = tableName;
		this.updateSql = updateSql;
	}
	
	public SimpleUpdateBuilder with(StatementParameters parameters) {
		this.parameters = parameters;
		return this;
	}
	
	@Override
	public StatementParameters buildParameters() {
		return  parameters;
	}
	
	public String build(){
		return updateSql;
	}
	
	@Override
	public String buildWith(String shardStr) {
		return updateSql.replaceFirst(tableName, tableName + shardStr);
	}

	@Override
	public String getTableName() {
		return tableName;
	}
}
