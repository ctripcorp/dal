package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class UpdateSqlBuilder extends AbstractSqlBuilder {
	private static final String UPDATE_TPL = "UPDATE %s %s WHERE %s";

	private List<String> updateFieldNames =  new ArrayList<String>();
	
	public UpdateSqlBuilder(String tableName, DatabaseCategory dbCategory) throws SQLException {
		super(tableName, dbCategory);
	}
	
	public UpdateSqlBuilder update(String fieldName, Object paramValue, int sqlType){
		FieldEntry field = new FieldEntry(fieldName,paramValue,sqlType);
		selectOrUpdataFieldEntrys.add(field);
		updateFieldNames.add(fieldName);
		return this;
	}
	
	public String build(){
		return internalBuild(getTableName());
	}
	
	@Override
	public String build(String shardStr) {
		return internalBuild(getTableName(shardStr));
	}
	
	private String internalBuild(String effectiveTableName) {
		return String.format(UPDATE_TPL, wrapField(effectiveTableName), buildUpdateField(), getWhereExp());
	}
	
	private String buildUpdateField(){
		StringBuilder setFields = new StringBuilder("SET ");
		for(int i=0,count=updateFieldNames.size();i<count;i++){
			String fieldName = updateFieldNames.get(i);
			setFields.append(wrapField(fieldName)).append(" = ?");
			if(i<count-1){
				setFields.append(", ");
			}
		}
		return setFields.toString();
	}
}
