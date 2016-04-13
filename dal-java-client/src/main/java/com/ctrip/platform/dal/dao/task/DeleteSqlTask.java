package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DeleteSqlTask<T> extends TaskAdapter<T> implements SqlTask<Integer>{
	@Override
	public Integer execute(DalClient client, String sql, StatementParameters parameters, DalHints hints) throws SQLException {
		return client.update(sql, parameters, hints);
	}
}
