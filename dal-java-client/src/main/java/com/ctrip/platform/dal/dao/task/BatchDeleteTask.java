package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.StatementParameters;

public class BatchDeleteTask<T> extends AbstractIntArrayBulkTask<T> {
	public BatchDeleteTask(DalParser<T> parser) {
		super(parser);
	}

	@Override
	public int[] execute(DalHints hints, List<Map<String, ?>> daoPojos) throws SQLException {
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		int i = 0;
		List<String> pkNames = Arrays.asList(parser.getPrimaryKeyNames());
		for (Map<String, ?> pojo : daoPojos) {
			StatementParameters parameters = new StatementParameters();
			addParameters(1, parameters, pojo, pkNames);
			parametersList[i++] = parameters;
		}
		
		String deleteSql = buildDeleteSql(getTableName(hints));
		int[] result = client.batchUpdate(deleteSql, parametersList, hints);
		hints.addDetailResults(result);
		return result;
	}
}
