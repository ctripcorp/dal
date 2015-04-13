package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.task.AbstractIntArrayBulkTask;

public class BatchDeleteSp3Task<T> extends AbstractIntArrayBulkTask<T> {
	private static final String DELETE_SP3_TPL = "sp3_%s_d";

	private String deleteSP3;
	
	public void initialize(DalParser<T> parser) {
		String tableName = parser.getTableName();
		deleteSP3 = String.format(DELETE_SP3_TPL, tableName);
	}

	@Override
	public int[] execute(DalHints hints, List<Map<String, ?>> daoPojos) throws SQLException {
		String callSql = buildCallSql(deleteSP3, parser.getPrimaryKeyNames().length);
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		for (int i = 0; i < daoPojos.size(); i++) {
			StatementParameters parameters = new StatementParameters();
			addParametersByName(parameters, daoPojos.get(i), parser.getPrimaryKeyNames());
			parametersList[i] = parameters;
		}
		
		int[] result = client.batchCall(callSql, parametersList, hints);
		hints.addDetailResults(result);
		return result;
	}

}
