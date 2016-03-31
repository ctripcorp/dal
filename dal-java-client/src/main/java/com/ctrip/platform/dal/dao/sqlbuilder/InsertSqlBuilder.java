package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.StatementParameters;

public class InsertSqlBuilder implements SqlBuilder {
	public static final String TMPL_SQL_INSERT = "INSERT INTO %s (%s) VALUES(%s)";
	protected static final String COLUMN_SEPARATOR = ", ";
	protected static final String PLACE_HOLDER = "?";
	
	private DatabaseCategory dBCategory;
	private String tableName;
	private List<FieldEntry> fieldEntrys =  new ArrayList<FieldEntry>();
	
	public InsertSqlBuilder(String tableName, DatabaseCategory dBCategory) throws SQLException{
		this.dBCategory = dBCategory;
		this.tableName = tableName;
	}
	
	@Override
	public String getTableName() {
		return tableName;
	}
	
	public InsertSqlBuilder setSensitive(String fieldName, Object paramValue, int sqlType){
		FieldEntry field = new FieldEntry(fieldName, paramValue, sqlType, true);
		fieldEntrys.add(field);
		return this;
	}
	
	public InsertSqlBuilder set(String fieldName, Object paramValue, int sqlType){
		FieldEntry field = new FieldEntry(fieldName, paramValue, sqlType);
		fieldEntrys.add(field);
		return this;
	}
	
	public String build(){
		return build(tableName);
	}
	
	@Override
	public String buildWith(String shardStr) {
		return build(tableName + shardStr);
	}
	
	private String build(String effectiveTableName) {
		StringBuilder fieldsSb = new StringBuilder();
		StringBuilder valueSb = new StringBuilder();

		int i = 0;
		for(FieldEntry entry: fieldEntrys) {
			fieldsSb.append(AbstractSqlBuilder.wrapField(dBCategory, entry.getFieldName()));
			valueSb.append(PLACE_HOLDER);
			if (++i < fieldEntrys.size()) {
				fieldsSb.append(COLUMN_SEPARATOR);
				valueSb.append(COLUMN_SEPARATOR);
			}
		}
		
		return String.format(TMPL_SQL_INSERT, effectiveTableName, fieldsSb.toString(),
				valueSb.toString());
	}
		
	public StatementParameters buildParameters(){
		StatementParameters parameters = new StatementParameters();
		int index = 1;
		for(FieldEntry entry : fieldEntrys) {
			if (entry.isSensitive())
				parameters.setSensitive(index++, entry.getFieldName(), entry.getSqlType(), entry.getParamValue());
			else
				parameters.set(index++, entry.getFieldName(), entry.getSqlType(), entry.getParamValue());
		}
		return parameters;
	}
}