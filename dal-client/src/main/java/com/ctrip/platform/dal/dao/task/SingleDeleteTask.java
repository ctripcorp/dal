package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalContextClient;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

public class SingleDeleteTask<T> extends TaskAdapter<T> implements SingleTask<T> {
	private static final String TMPL_SQL_DELETE = "DELETE FROM %s WHERE %s";

	@Override
	public int execute(DalHints hints, Map<String, ?> fields, T rawPojo, DalTaskContext taskContext) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		addParameters(parameters, fields, parser.getPrimaryKeyNames());

		String tableName = getRawTableName(hints, parameters, fields);
		if (taskContext instanceof DalTableNameConfigure)
			((DalTableNameConfigure) taskContext).addTables(tableName);

		String deleteSql = buildDeleteSql(quote(tableName));
		if (client instanceof DalContextClient)
			return ((DalContextClient) client).update(deleteSql, parameters, hints.setFields(fields), taskContext);
		else
			throw new DalRuntimeException("The client is not instance of DalClient");
	}

	private String buildDeleteSql(String tableName) {
		return String.format(TMPL_SQL_DELETE, tableName, pkSql);
	}

}
