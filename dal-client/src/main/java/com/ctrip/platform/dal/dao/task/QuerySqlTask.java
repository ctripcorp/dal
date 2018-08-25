package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

public class QuerySqlTask<T> extends TaskAdapter implements SqlTask<T>{
	private DalResultSetExtractor<T> extractor;
	
	public QuerySqlTask(DalResultSetExtractor<T> extractor) {
		this.extractor = extractor;
	}

	@Override
	public T execute(DalClient client, String sql, StatementParameters parameters, DalHints hints, DalTaskContext taskContext) throws SQLException {
		if (client instanceof DalContextClient)
			return ((DalContextClient) client).query(sql, parameters, hints, extractor, taskContext);
		else
			throw new DalRuntimeException("The client is not instance of DalClient");
	}
}
