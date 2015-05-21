package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DeleteSqlTask<T> extends TaskAdapter<T> implements SqlTask<Integer>{
	private static final String TMPL_SQL_DELETE = "DELETE FROM %s WHERE %s";
	
	@Override
	public Integer execute(DalClient client, String whereClause, StatementParameters parameters, DalHints hints) throws SQLException {
		return client.update(String.format(TMPL_SQL_DELETE,
				getTableName(hints, parameters), whereClause), parameters, hints);
	}

	@Override
	public ResultMerger<Integer> getMerger() {
		return new ResultMerger.IntSummary();
	}
}
