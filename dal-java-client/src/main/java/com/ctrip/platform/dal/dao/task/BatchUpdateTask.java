package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.StatementParameters;

public class BatchUpdateTask<T> extends AbstractIntArrayBulkTask<T> {
	public static final String TMPL_SQL_UPDATE = "UPDATE %s SET %s WHERE %s";

	private String updateColumns;
	public void initialize(DalParser<T> parser) {
		super.initialize(parser);
		updateColumns = initUpdateColumns();
	}

	@Override
	public int[] execute(DalHints hints, List<Map<String, ?>> daoPojos) throws SQLException {
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		int i = 0;

		for (Map<String, ?> pojo : daoPojos) {
			StatementParameters parameters = new StatementParameters();

			addParameters(parameters, pojo);
			addParameters(parameters, pojo, parser.getPrimaryKeyNames());
			
			parametersList[i++] = parameters;
		}
		
		String batchUpdateSql = buildBatchUpdateSql(getTableName(hints));
		
		int[] result = client.batchUpdate(batchUpdateSql, parametersList, hints);
		hints.addDetailResults(result);
		return result;
	}
	
	private String initUpdateColumns() {
		// TODO check if the order is correct
		Set<String> nonPkColumns = new LinkedHashSet<>(Arrays.asList(parser.getColumnNames()));
		
		for (String column : parser.getPrimaryKeyNames()) {
			nonPkColumns.remove(column);
		}

		return String.format(
				combine(TMPL_SET_VALUE, nonPkColumns.size(), COLUMN_SEPARATOR),
				quote(nonPkColumns));
	}

	public String buildBatchUpdateSql(String tableName) {
		return String.format(TMPL_SQL_UPDATE, tableName, updateColumns,
				pkSql);
	}
}
