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
		return build(getTableName());
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
	public String buildWith(String shardStr) {
		return build(getTableName(shardStr));
	}
	
	private String build(String effectiveTableName) {
		String whereStr = whereClause == null ? getWhereExp() : whereClause;
		return String.format(DELETE_TPL, effectiveTableName, whereStr);
	}
}
