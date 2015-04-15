package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;

public class SingleInsertTask<T> extends BaseInsertTask<T> implements SingleTask<T> {
	
	@Override
	public int execute(DalHints hints, Map<String, ?> fields) throws SQLException {
		removeAutoIncrementPrimaryFields(fields);
		
		String insertSql = buildInsertSql(hints, fields);

		StatementParameters parameters = new StatementParameters();
		addParameters(parameters, fields);
		
		KeyHolder keyHolder = hints.getKeyHolder();
		return keyHolder == null ?
			client.update(insertSql, parameters, hints):
			client.update(insertSql, parameters, hints, keyHolder);
	}
}
