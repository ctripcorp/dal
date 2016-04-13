package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class DeleteSqlBuilder extends AbstractSqlBuilder {
	private String whereClause;
	
	public DeleteSqlBuilder(String tableName, DatabaseCategory dBCategory) throws SQLException{
		super(dBCategory);
		setTableName(tableName);
	}
	
	public DeleteSqlBuilder(String tableName, String whereClause, DatabaseCategory dBCategory) throws SQLException{
		super(dBCategory);
		setTableName(tableName);
		this.whereClause = whereClause;
	}
	
	public String build(){
		return build(getTableName());
	}
	
	@Override
	public String buildWith(String shardStr) {
		return build(getTableName(shardStr));
	}
	
	private String build(String effectiveTableName) {
		StringBuilder sql = new StringBuilder("DELETE FROM");
		sql.append(" ").append(effectiveTableName).append(" ");
		if(whereClause == null)
			sql.append(this.getWhereExp());
		else
			sql.append("WHERE ").append(whereClause);
		
		return sql.toString();
	}
}
