package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.exceptions.DalException;

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
		
		Set<String> unqualifiedColumns = filterNullColumns(hints, daoPojos);
		unqualifiedColumns.addAll(notInsertableColumns);
		if(parser.isAutoIncrement() && hints.isIdentityInsertDisabled())
			unqualifiedColumns.add(parser.getPrimaryKeyNames()[0]);

		
		for (Integer index :daoPojos.keySet()) {
			Map<String, ?> pojo = daoPojos.get(index);
			
			removeUnqualifiedColumns(pojo, unqualifiedColumns);
			
			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, pojo);
			parametersList[i++] = parameters;
		}

		String batchInsertSql = buildBatchInsertSql(getTableName(hints), hints, unqualifiedColumns);
		int[] result = client.batchUpdate(batchInsertSql, parametersList, hints);
		return result;
	}
	
	private Set<String> filterNullColumns(DalHints hints, Map<Integer, Map<String, ?>> daoPojos) throws DalException {
		if(hints.isInsertNullField()) {
			return new HashSet<>();
		}
		
		Set<String> nullColumns = new HashSet<>(insertableColumns);
		String[] columnsToCheck = nullColumns.toArray(new String[nullColumns.size()]);
		boolean changed = false;
		for (Integer index :daoPojos.keySet()) {
			if(nullColumns.isEmpty())
				break;

			if(changed) {
				columnsToCheck = nullColumns.toArray(new String[nullColumns.size()]);
				changed = false;
			}
			
			Map<String, ?> pojo = daoPojos.get(index);
			for (int i = 0; i < columnsToCheck.length; i++) {
				String colName = columnsToCheck[i];
				if(pojo.get(colName) != null) {
					nullColumns.remove(colName);
					changed = true;
				}
			}
		}
		
		return nullColumns;
	}

	private void removeUnqualifiedColumns(Map<String, ?> pojo, Set<String> unqualifiedColumns) {
		if(unqualifiedColumns.size() == 0)
			return;
		
		for(String columName: unqualifiedColumns) {
			pojo.remove(columName);
		}
	}
	
	private String buildBatchInsertSql(String tableName, DalHints hints, Set<String> unqualifiedColumns) {
		Set<String> finalInsertableColumns = new HashSet<>(insertableColumns);
		finalInsertableColumns.removeAll(unqualifiedColumns);
		
		int validColumnsSize = finalInsertableColumns.size();
		
		String values = combine(PLACE_HOLDER, validColumnsSize,
				COLUMN_SEPARATOR);

		String insertColumns = hints.isIdentityInsertDisabled() ? columnsForInsert: columnsForInsertWithId;
		return String.format(TMPL_SQL_INSERT, tableName, insertColumns,
				values);
	}
	
	@Override
	public BulkTaskResultMerger<int[]> createMerger() {
		return new ShardedIntArrayResultMerger();
	}
}
