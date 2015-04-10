package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.StatementParameters;

public class BatchInsertTask<T> extends AbstractIntArrayBulkTask<T> {
	public BatchInsertTask(DalParser<T> parser) {
		super(parser);
	}

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


}
