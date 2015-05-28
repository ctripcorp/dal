package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.StatementParameters;

public class QuerySqlTask<T> implements SqlTask<T>{
	private DalResultSetExtractor<T> extractor;
	
	public QuerySqlTask(DalResultSetExtractor<T> extractor) {
		this.extractor = extractor;
	}
	
	@Override
	public T execute(DalClient client, String sql, StatementParameters parameters, DalHints hints) throws SQLException {
		return client.query(sql, parameters, hints, extractor);
	}
}
