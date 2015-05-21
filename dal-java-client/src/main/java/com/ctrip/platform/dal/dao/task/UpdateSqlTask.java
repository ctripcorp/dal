package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.StatementParameters;

public class UpdateSqlTask implements SqlTask<Integer>{
	@Override
	public Integer execute(DalClient client, String sql, StatementParameters parameters, DalHints hints) throws SQLException {
		return client.update(sql, parameters, hints);
	}

	@Override
	public ResultMerger<Integer> getMerger() {
		return new ResultMerger.IntSummary();
	}
}
