package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class DeleteSqlBuilder extends AbstractSqlBuilder {
	
	private StringBuilder sql = new StringBuilder();
	
	public DeleteSqlBuilder(String tableName, DatabaseCategory dBCategory) throws SQLException{
		super(dBCategory);
		setTableName(tableName);
	}
	
	public String build(){
		return build(getTableName());
	}
	
	@Override
	public String buildWith(String shardStr) {
		return build(getTableName(shardStr));
	}
	
	private String build(String effectiveTableName) {
		sql = new StringBuilder("DELETE FROM");
		sql.append(" ").append(effectiveTableName);
		sql.append(" ").append(this.getWhereExp());
		return sql.toString();
	}
}
