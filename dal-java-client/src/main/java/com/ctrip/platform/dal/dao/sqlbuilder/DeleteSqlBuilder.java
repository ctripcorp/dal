package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DeleteSqlBuilder extends AbstractSqlBuilder {
	private static final String DELETE_TPL = "DELETE FROM %s WHERE %s";

	private String whereClause;
	private StatementParameters parameters;
	
	public DeleteSqlBuilder(String tableName, DatabaseCategory dbCategory) throws SQLException{
		super(tableName, dbCategory);
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
