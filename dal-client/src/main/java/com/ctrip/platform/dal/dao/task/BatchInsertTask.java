package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class BatchInsertTask<T> extends InsertTaskAdapter<T> implements BulkTask<int[], T> {
	private static final String TMPL_SQL_INSERT = "INSERT INTO %s (%s) VALUES(%s)";

	@Override
	public int[] getEmptyValue() {
		return new int[0];
	}	

	@Override
	public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos, List<T> rawPojos) throws SQLException {
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		int i = 0;
		
		Set<String> unqualifiedColumns = filterUnqualifiedColumns(hints, daoPojos);
		
		for (Integer index :daoPojos.keySet()) {
			Map<String, ?> pojo = daoPojos.get(index);
			
			removeUnqualifiedColumns(pojo, unqualifiedColumns);
			
			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, pojo);
			parametersList[i++] = parameters;
		}

		String batchInsertSql = buildBatchInsertSql(hints, unqualifiedColumns);
		int[] result = client.batchUpdate(batchInsertSql, parametersList, hints);
		return result;
	}
	
	private String buildBatchInsertSql(DalHints hints, Set<String> unqualifiedColumns) throws SQLException {
		List<String> finalInsertableColumns = buildValidColumnsForInsert(unqualifiedColumns);
		
		String values = combine(PLACE_HOLDER, finalInsertableColumns.size(), COLUMN_SEPARATOR);
		String insertColumns = combineColumns(finalInsertableColumns, COLUMN_SEPARATOR);
		
		return String.format(TMPL_SQL_INSERT, getTableName(hints), insertColumns, values);
	}
	
	@Override
	public BulkTaskResultMerger<int[]> createMerger() {
		return new ShardedIntArrayResultMerger();
	}
}
