package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DeleteSqlBuilder extends AbstractTableSqlBuilder {
	private static final String DELETE_TPL = "DELETE FROM %s WHERE %s";

	private String whereClause;
	private StatementParameters parameters;
	
	/**
	 * @deprecated not suggested anymore
	 * @param tableName
	 * @param dbCategory
	 * @throws SQLException
	 */
	public DeleteSqlBuilder(String tableName, DatabaseCategory dbCategory) throws SQLException{
		from(tableName).setDatabaseCategory(dbCategory);
		setCompatible(true);
	}
	
	public DeleteSqlBuilder(){}
	
	public DeleteSqlBuilder from(String tableName) throws SQLException {
		super.from(tableName);
		return this;
	}
	
	public DeleteSqlBuilder setDatabaseCategory(DatabaseCategory dbCategory) throws SQLException {
		super.setDatabaseCategory(dbCategory);
		return this;
	}
	
	public String build(){
		return internalBuild(getTableName());
	}
	
	public DeleteSqlBuilder where(String whereClause) {
		this.whereClause = whereClause;
		return this;
	}

	public DeleteSqlBuilder with(StatementParameters parameters) {
		this.parameters = parameters;
		return this;
	}
	
	@Override
	public StatementParameters buildParameters() {
		return  parameters == null ? super.buildParameters() : parameters;
	}
	
	@Override
	public String build(String shardStr) {
		return internalBuild(getTableName(shardStr));
	}
	
	private String internalBuild(String effectiveTableName) {
		String whereStr = whereClause == null ? getWhereExp() : whereClause;
		return String.format(DELETE_TPL, wrapField(effectiveTableName), whereStr);
	}
}
