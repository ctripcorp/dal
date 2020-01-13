package com.ctrip.platform.dal.dao.task;


public class SingleInsertTask<T> extends AbstractSingleInsertTask<T> {
	protected static final String TPL_SQL_INSERT = "INSERT INTO %s (%s) VALUES (%s)";

	@Override
	public String getSqlTpl() {
		return TPL_SQL_INSERT;
	}
}
