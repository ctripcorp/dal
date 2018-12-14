package com.ctrip.platform.dal.dao.task;


public class CombinedInsertTask<T> extends AbstractCombinedInsertTask<T> {
	protected static final String TPL_SQL_COMBINED_INSERT = "INSERT INTO %s(%s) VALUES %s";

	@Override
	protected String getSqlTpl() {
		return TPL_SQL_COMBINED_INSERT;
	}
}