package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.StatementParameters;

public class BatchUpdateTask<T> extends AbstractIntArrayBulkTask<T> {
	public static final String TMPL_SQL_UPDATE = "UPDATE %s SET %s WHERE %s";

	private String[] defaultUpdateColumnNames;
	private String updateColumns;
	public void initialize(DalParser<T> parser) {
		super.initialize(parser);
		updateColumns = initUpdateColumns();
	}

	@Override
	public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos) throws SQLException {
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		int i = 0;

		String[] updateColumnNames = filterUpdateColumnNames(hints, daoPojos);
		
		for (Integer index :daoPojos.keySet()) {
			Map<String, ?> pojo = daoPojos.get(index);
			StatementParameters parameters = new StatementParameters();

			addParameters(parameters, pojo, updateColumnNames);
			addParameters(parameters, pojo, parser.getPrimaryKeyNames());
			
			parametersList[i++] = parameters;
		}
		
		String batchUpdateSql = buildBatchUpdateSql(getTableName(hints), updateColumnNames);
		
		int[] result = client.batchUpdate(batchUpdateSql, parametersList, hints);
		return result;
	}
	
	private String initUpdateColumns() {
		// TODO check if the order is correct
		Set<String> nonPkColumns = new LinkedHashSet<>(Arrays.asList(parser.getColumnNames()));
		
		for (String column : parser.getPrimaryKeyNames()) {
			nonPkColumns.remove(column);
		}
		
		defaultUpdateColumnNames = nonPkColumns.toArray(new String[nonPkColumns.size()]);

		return String.format(
				combine(TMPL_SET_VALUE, nonPkColumns.size(), COLUMN_SEPARATOR),
				quote(nonPkColumns));
	}
	
	private String[] filterUpdateColumnNames(DalHints hints, Map<Integer, Map<String, ?>> daoPojos) {
		if(hints.is(DalHintEnum.updateNullField))
			return defaultUpdateColumnNames;
		
		Map<String, Boolean> nullable = new HashMap<>();
		
		Set<String> effectiveColumnSet = new HashSet<>();
		for (Integer index :daoPojos.keySet()) {
			Map<String, ?> pojo = daoPojos.get(index);
			for(Map.Entry<String, ?> columnEntry: pojo.entrySet()) {
				if(columnEntry.getValue() != null)
					effectiveColumnSet.add(columnEntry.getKey());
			}
		}		
		
		return effectiveColumnSet.toArray(new String[effectiveColumnSet.size()]);
	}

	private String buildBatchUpdateSql(String tableName, String[] updateColumns) {
		return String.format(TMPL_SQL_UPDATE, tableName, updateColumns,
				pkSql);
	}
}
