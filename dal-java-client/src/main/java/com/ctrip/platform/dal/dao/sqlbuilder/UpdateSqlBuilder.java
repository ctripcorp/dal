package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class UpdateSqlBuilder extends AbstractSqlBuilder {
	
	private StringBuilder sql = new StringBuilder();
	
	private String tableName = "";
	
	private List<String> updateFieldNames =  new ArrayList<String>();
	
	public UpdateSqlBuilder(String tableName, DatabaseCategory dBCategory) throws SQLException {
		super(dBCategory);
		if(tableName!=null && !tableName.isEmpty()){
			this.tableName = tableName;
		}else{
			throw new SQLException("table name is illegal.");
		}
	}

	public UpdateSqlBuilder addUpdateField(String fieldName, Object paramValue, int sqlType){
		FieldEntry field = new FieldEntry(fieldName,paramValue,sqlType);
		fieldEntrys.add(field);
		updateFieldNames.add(fieldName);
		return this;
	}
	
	public String build(){
		sql = new StringBuilder("UPDATE");
		sql.append(" ").append(tableName);
		sql.append(buildUpdateField());
		sql.append(" ").append(this.getWhereExp());
		return sql.toString();
	}
	
	private String buildUpdateField(){
		StringBuilder setFields = new StringBuilder(" SET ");
		for(int i=0,count=updateFieldNames.size();i<count;i++){
			String fieldName = updateFieldNames.get(i);
			setFields.append(this.wrapField(fieldName)).append(" = ?");
			if(i<count-1){
				setFields.append(", ");
			}
		}
		return setFields.toString();
	}
	
}
