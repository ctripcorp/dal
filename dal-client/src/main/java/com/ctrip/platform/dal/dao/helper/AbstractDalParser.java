package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.DalParser;

public abstract class AbstractDalParser<T> implements DalParser<T> {
	protected String dataBaseName;
	protected String tableName;
	protected String[] columns;
	protected String[] primaryKeyColumns;
	protected int[] columnTypes;
	protected String[] sensitiveColumnNames;
	protected String versionColumn;
	protected String[] insertableColumnNames;
	protected String[] updatableColumnNames;
	
	public AbstractDalParser(){}
	
	public AbstractDalParser(
			String dataBaseName,
			String tableName,
			String[] columns,
			String[] primaryKeyColumns,
			int[] columnTypes) {
		this.dataBaseName = dataBaseName;
		this.tableName = tableName;
		this.columns = columns;
		this.updatableColumnNames = columns;
		this.insertableColumnNames = columns;
		this.primaryKeyColumns = primaryKeyColumns;
		this.columnTypes = columnTypes;
	}

	public AbstractDalParser(
			String dataBaseName,
			String tableName,
			String[] columns,
			String[] primaryKeyColumns,
			int[] columnTypes,
			String[] sensitiveColumnNames,
			String versionColumn,
			String[] updatableColumnNames,
			String[] insertableColumnNames) {
		this(dataBaseName,
				tableName,
				columns,
				primaryKeyColumns,
				columnTypes);
		this.sensitiveColumnNames = sensitiveColumnNames;
		this.versionColumn = versionColumn;
		this.insertableColumnNames = insertableColumnNames;
		this.updatableColumnNames = updatableColumnNames;
	}
	
	@Override
	public String getDatabaseName() {
		return dataBaseName;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public String[] getColumnNames() {
		return columns;
	}
	
	@Override
	public String[] getPrimaryKeyNames() {
		return primaryKeyColumns;
	}
	
	@Override
	public int[] getColumnTypes() {
		return columnTypes;
	}

	@Override
	public String[] getSensitiveColumnNames() {
		return sensitiveColumnNames;
	}
	
	public String getVersionColumn() {
		return versionColumn;
	}
	
	public String[] getUpdatableColumnNames() {
		return updatableColumnNames;
	}
	
	public String[] getInsertableColumnNames() {
		return insertableColumnNames;
	}
}
