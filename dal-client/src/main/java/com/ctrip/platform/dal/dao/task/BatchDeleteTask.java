package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalContextClient;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

public class BatchDeleteTask<T> extends AbstractIntArrayBulkTask<T> {
	private static final String TMPL_SQL_DELETE = "DELETE FROM %s WHERE %s";

	@Override
	public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos, DalBulkTaskContext<T> taskContext) throws SQLException {
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		List<String> pkNames = Arrays.asList(parser.getPrimaryKeyNames());

		int i = 0;
		for (Integer index :daoPojos.keySet()) {
			StatementParameters parameters = new StatementParameters();
			addParameters(1, parameters, daoPojos.get(index), pkNames);
			parametersList[i++] = parameters;
		}

		String tableName = getRawTableName(hints);
		if (taskContext instanceof DalTableNameConfigure)
			((DalTableNameConfigure) taskContext).addTables(tableName);

		String deleteSql = buildDeleteSql(quote(tableName));

		int[] result;
		if (client instanceof DalContextClient)
			result = ((DalContextClient) client).batchUpdate(deleteSql, parametersList, hints, taskContext);
		else
			throw new DalRuntimeException("The client is not instance of DalClient");
		return result;
	}

	private String buildDeleteSql(String tableName) {
		return String.format(TMPL_SQL_DELETE, tableName, pkSql);
	}
}
