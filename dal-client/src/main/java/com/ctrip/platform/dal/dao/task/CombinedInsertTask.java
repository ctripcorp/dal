package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class CombinedInsertTask<T> extends InsertTaskAdapter<T> implements BulkTask<Integer, T>, KeyHolderAwaredTask {
	public static final String TMPL_SQL_MULTIPLE_INSERT = "INSERT INTO %s(%s) VALUES %s";

	@Override
	public Integer getEmptyValue() {
		return 0;
	}	

	@Override
	public BulkTaskContext<T> createTaskContext(DalHints hints, List<Map<String, ?>> daoPojos, List<T> rawPojos) {
		BulkTaskContext<T> context = new BulkTaskContext<T>(rawPojos);
		Set<String> unqualifiedColumns = filterUnqualifiedColumns(hints, daoPojos, rawPojos);
		context.setUnqualifiedColumns(unqualifiedColumns);
		return context;
	}

	@Override
	public Integer execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos, BulkTaskContext<T> taskContext) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		StringBuilder values = new StringBuilder();

		Set<String> unqualifiedColumns = taskContext.getUnqualifiedColumns();
		
		List<String> finalInsertableColumns = buildValidColumnsForInsert(unqualifiedColumns);
		
		String insertColumns = combineColumns(finalInsertableColumns, COLUMN_SEPARATOR);
		
		int startIndex = 1;
		for (Integer index :daoPojos.keySet()) {
			Map<String, ?> pojo = daoPojos.get(index);
			
			removeUnqualifiedColumns(pojo, unqualifiedColumns);
			
			int paramCount = addParameters(startIndex, parameters, pojo, finalInsertableColumns);
			startIndex += paramCount;
			values.append(String.format("(%s),", combine("?", paramCount, ",")));
		}

		String sql = String.format(TMPL_SQL_MULTIPLE_INSERT,
				getTableName(hints), insertColumns,
				values.substring(0, values.length() - 2) + ")");

		return client.update(sql, parameters, hints);
	}

	@Override
	public BulkTaskResultMerger<Integer> createMerger() {
		return new ShardedIntResultMerger();
	}
}