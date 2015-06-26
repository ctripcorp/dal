package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public interface SqlTask<T> {
	T execute(DalClient client, String sql, StatementParameters parameters, DalHints hints) throws SQLException;
}
