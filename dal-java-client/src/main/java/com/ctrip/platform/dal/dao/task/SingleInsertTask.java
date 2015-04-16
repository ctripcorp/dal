package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class SingleInsertTask<T> extends InsertTaskAdapter<T> implements SingleTask<T> {
	
	@Override
	public int execute(DalHints hints, Map<String, ?> fields) throws SQLException {
		removeAutoIncrementPrimaryFields(fields);
		
		String insertSql = buildInsertSql(hints, fields);

		StatementParameters parameters = new StatementParameters();
		addParameters(parameters, fields);
		
		return client.update(insertSql, parameters, hints);
	}
}
