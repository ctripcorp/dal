package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.StatementParameters;

public class SingleDeleteTast<T> extends TaskAdapter<T> implements SingleTask {
	public SingleDeleteTast(DalParser<T> parser) {
		super(parser);
	}

	@Override
	public int execute(DalHints hints, Map<String, ?> fields) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		addParameters(parameters, fields, parser.getPrimaryKeyNames());
		String deleteSql = buildDeleteSql(getTableName(hints, parameters, fields));

		return client.update(deleteSql, parameters, hints.setFields(fields));
	}
}
