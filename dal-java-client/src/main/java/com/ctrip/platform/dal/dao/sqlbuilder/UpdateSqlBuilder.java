package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UpdateSqlBuilder {
	
	private StringBuilder sql = new StringBuilder("UPDATE ");
	
	private String tableName = "";
	
	private List<String> updateField =  new ArrayList<String>();
	
	private SqlWhereBuilder whereBuilder = new SqlWhereBuilder();	
	
	public UpdateSqlBuilder(String tableName) throws SQLException {
		if(tableName!=null && !tableName.isEmpty()){
			this.tableName = tableName;
		}else{
			throw new SQLException("table name is illegal.");
		}
	}

	public UpdateSqlBuilder addUpdateField(String ...fieldName){
		for(String field:fieldName){
			updateField.add(field);
		}
		return this;
	}
	
	public SqlWhereBuilder addConstrant(){
		return whereBuilder;
	}
	
	public String buildUpdateSql(){
		sql.append(" ").append(tableName);
		buildUpdateField();
		sql.append(" ").append(whereBuilder.getWhereExp());
		return sql.toString();
	}
	
	private void buildUpdateField(){
		sql.append(" SET ");
		for(int i=0,count=updateField.size();i<count;i++){
			sql.append(updateField.get(i)).append(" = ?");
			if(i<count-1){
				sql.append(", ");
			}
		}
	}
	
}
