package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class BatchInsertTask<T> extends InsertTaskAdapter<T> implements BulkTask<int[], T> {
	private static final String TMPL_SQL_INSERT = "INSERT INTO %s (%s) VALUES(%s)";

	@Override
	public int[] getEmptyValue() {
		return new int[0];
	}	

	@Override
	public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos) throws SQLException {
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		int i = 0;
		
		for (Integer index :daoPojos.keySet()) {
			Map<String, ?> pojo = daoPojos.get(index);
			
			if(!hints.is(DalHintEnum.diableAutoIncrementalId))
				removeAutoIncrementPrimaryFields(pojo);
			
			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, pojo);
			parametersList[i++] = parameters;
		}

		String batchInsertSql = buildBatchInsertSql(getTableName(hints), hints);
		int[] result = client.batchUpdate(batchInsertSql, parametersList, hints);
		return result;
	}

	private String buildBatchInsertSql(String tableName, DalHints hints) {
		int validColumnsSize = parser.getColumnNames().length;
		if(parser.isAutoIncrement() && !hints.is(DalHintEnum.diableAutoIncrementalId))
			validColumnsSize--;
		
		String values = combine(PLACE_HOLDER, validColumnsSize,
				COLUMN_SEPARATOR);

		String insertColumns = hints.is(DalHintEnum.diableAutoIncrementalId) ? columnsForInsertWithId: columnsForInsert;
		return String.format(TMPL_SQL_INSERT, tableName, insertColumns,
				values);
	}
	
	@Override
	public BulkTaskResultMerger<int[]> createMerger() {
		return new ShardedIntArrayResultMerger();
	}
}
