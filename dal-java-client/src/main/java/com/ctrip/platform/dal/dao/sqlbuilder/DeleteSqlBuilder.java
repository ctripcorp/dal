package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class DeleteSqlBuilder extends AbstractSqlBuilder {
	
	private StringBuilder sql = new StringBuilder();
	
	private String tableName = "";
	
	public DeleteSqlBuilder(String tableName, DatabaseCategory dBCategory) throws SQLException{
		super(dBCategory);
		if(tableName!=null && !tableName.isEmpty()){
			this.tableName = tableName;
		}else{
			throw new SQLException("table name is illegal.");
		}
	}
	
	public String build(){
		sql = new StringBuilder("DELETE FROM");
		sql.append(" ").append(tableName);
		sql.append(" ").append(this.getWhereExp());
		return sql.toString();
	}
	
}
