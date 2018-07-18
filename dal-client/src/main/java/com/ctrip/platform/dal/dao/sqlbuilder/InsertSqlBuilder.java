package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.StatementParameters;

public class InsertSqlBuilder implements TableSqlBuilder {
	public static final String TMPL_SQL_INSERT = "INSERT INTO %s (%s) VALUES(%s)";
	protected static final String COLUMN_SEPARATOR = ", ";
	protected static final String PLACE_HOLDER = "?";
	
	private DatabaseCategory dbCategory;
	private String tableName;
	private List<FieldEntry> fieldEntrys =  new ArrayList<FieldEntry>();
	
	public InsertSqlBuilder from(String tableName) throws SQLException {
		this.tableName = tableName;
		return this;
	}
	
	public InsertSqlBuilder setDatabaseCategory(DatabaseCategory dbCategory) throws SQLException {
		this.dbCategory = dbCategory;
		return this;
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
		return internalBuild(tableName);
	}
	
	@Override
	public String build(String shardStr) {
		return internalBuild(tableName + shardStr);
	}
	
	private String internalBuild(String effectiveTableName) {
		StringBuilder fieldsSb = new StringBuilder();
		StringBuilder valueSb = new StringBuilder();

		int i = 0;
		for(FieldEntry entry: fieldEntrys) {
			fieldsSb.append(AbstractTableSqlBuilder.wrapField(dbCategory, entry.getFieldName()));
			valueSb.append(PLACE_HOLDER);
			if (++i < fieldEntrys.size()) {
				fieldsSb.append(COLUMN_SEPARATOR);
				valueSb.append(COLUMN_SEPARATOR);
			}
		}
		
		return String.format(TMPL_SQL_INSERT, AbstractTableSqlBuilder.wrapField(dbCategory, effectiveTableName), fieldsSb.toString(),
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