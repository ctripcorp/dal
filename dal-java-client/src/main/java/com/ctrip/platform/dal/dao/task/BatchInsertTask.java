package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class BatchInsertTask<T> extends InsertTaskAdapter<T> implements BulkTask<int[], T> {
	private static final String TMPL_SQL_INSERT = "INSERT INTO %s (%s) VALUES(%s)";

	@Override
	public int[] execute(DalHints hints, List<Map<String, ?>> daoPojos) throws SQLException {
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		int i = 0;
		for (Map<String, ?> fields : daoPojos) {
			removeAutoIncrementPrimaryFields(fields);
			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, fields);
			parametersList[i++] = parameters;
		}

		String batchInsertSql = buildBatchInsertSql(getTableName(hints));
		int[] result = client.batchUpdate(batchInsertSql, parametersList, hints);
		hints.addDetailResults(result);
		return result;
	}

	private String buildBatchInsertSql(String tableName) {
		int validColumnsSize = parser.getColumnNames().length;
		if(parser.isAutoIncrement())
			validColumnsSize--;
		
		String values = combine(PLACE_HOLDER, validColumnsSize,
				COLUMN_SEPARATOR);

		return String.format(TMPL_SQL_INSERT, tableName, columnsForInsert,
				values);
	}
	
	@Override
	public int[] merge(List<int[]> results) {
		return mergeIntArray(results);
	}	
}
