package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;

public class DeleteSqlBuilder {
	
	private StringBuilder sql = new StringBuilder("DELETE FROM");
	
	private String tableName = "";
	
	private SqlWhereBuilder whereBuilder = new SqlWhereBuilder();
	
	public DeleteSqlBuilder(String tableName) throws SQLException{
		if(tableName!=null && !tableName.isEmpty()){
			this.tableName = tableName;
		}else{
			throw new SQLException("table name is illegal.");
		}
	}
	
	public SqlWhereBuilder addConstrant(){
		return whereBuilder;
	}
	
	public String buildDelectSql(){
		sql.append(" ").append(tableName);
		sql.append(" ").append(whereBuilder.getWhereExp());
		return sql.toString();
	}
	
}
