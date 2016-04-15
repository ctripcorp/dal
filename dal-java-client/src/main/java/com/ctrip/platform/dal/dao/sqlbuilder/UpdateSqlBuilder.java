package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class UpdateSqlBuilder extends AbstractSqlBuilder {
	private List<String> updateFieldNames =  new ArrayList<String>();
	private String updateSql;
	
	public UpdateSqlBuilder(String tableName, DatabaseCategory dbCategory) throws SQLException {
		super(tableName, dbCategory);
	}

	public UpdateSqlBuilder(String tableName, String updateSql, DatabaseCategory dbCategory) throws SQLException {
		super(tableName, dbCategory);
		this.updateSql = updateSql;
	}
	
	public UpdateSqlBuilder update(String fieldName, Object paramValue, int sqlType){
		FieldEntry field = new FieldEntry(fieldName,paramValue,sqlType);
		selectOrUpdataFieldEntrys.add(field);
		updateFieldNames.add(fieldName);
		return this;
	}
	
	public String build(){
		return build(getTableName());
	}
	
	@Override
	public String buildWith(String shardStr) {
		return build(getTableName(shardStr));
	}
	
	private String build(String effectiveTableName) {
		// In case user provides the rqw sql
		if(updateSql != null)
			return updateSql;
		
		StringBuilder sql = new StringBuilder("UPDATE");
		sql.append(" ").append(effectiveTableName);
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
