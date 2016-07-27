package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class BatchDeleteTask<T> extends AbstractIntArrayBulkTask<T> {
	private static final String TMPL_SQL_DELETE = "DELETE FROM %s WHERE %s";

	@Override
	public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos) throws SQLException {
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		List<String> pkNames = Arrays.asList(parser.getPrimaryKeyNames());

		int i = 0;
		for (Integer index :daoPojos.keySet()) {
			StatementParameters parameters = new StatementParameters();
			addParameters(1, parameters, daoPojos.get(index), pkNames);
			parametersList[i++] = parameters;
		}
		
		String deleteSql = buildDeleteSql(getTableName(hints));
		int[] result = client.batchUpdate(deleteSql, parametersList, hints);
		return result;
	}
	
	private String buildDeleteSql(String tableName) {
		return String.format(TMPL_SQL_DELETE, tableName, pkSql);
	}
}
