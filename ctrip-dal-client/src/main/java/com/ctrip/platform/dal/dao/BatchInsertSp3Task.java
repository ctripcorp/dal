package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.task.AbstractIntArrayBulkTask;

public class BatchInsertSp3Task<T> extends AbstractIntArrayBulkTask<T> {
	private static final String INSERT_SP3_TPL = "sp3_%s_i";

	private String insertSP3;

	public void initialize(DalParser<T> parser) {
		String tableName = parser.getTableName();
		insertSP3 = String.format(INSERT_SP3_TPL, tableName);
	}
	
	@Override
	public int[] execute(DalHints hints, List<Map<String, ?>> daoPojos) throws SQLException {
		String callSql = buildCallSql(insertSP3, parser.getColumnNames().length);
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		
		for (int i = 0; i < daoPojos.size(); i++) {
			StatementParameters parameters = new StatementParameters();
			addParametersByName(parameters, daoPojos.get(i));
			parametersList[i] = parameters;
		}
		
		int[] result = client.batchCall(callSql, parametersList, hints);
		hints.addDetailResults(result);
		return result; 
	}

}
