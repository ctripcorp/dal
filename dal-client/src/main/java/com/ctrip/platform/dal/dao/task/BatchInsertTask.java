package com.ctrip.platform.dal.dao.task;


public class BatchInsertTask<T> extends AbstractBatchInsertTask<T> {
	private static final String TPL_SQL_BATCH_INSERT = "INSERT INTO %s (%s) VALUES (%s)";

	@Override
	protected String getSqlTpl() {
		return TPL_SQL_BATCH_INSERT;
	}
}
