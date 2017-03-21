package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.task.AbstractIntArrayBulkTask;

public class BatchDeleteSp3Task<T> extends AbstractIntArrayBulkTask<T> {
	private static final String DELETE_SP3_TPL = "sp3_%s_d";

	@Override
	public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos) throws SQLException {
		String deleteSP3 = String.format(DELETE_SP3_TPL, getRawTableName(hints));
		
		String callSql = buildCallSql(deleteSP3, parser.getPrimaryKeyNames().length);
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];

		int i = 0;
		for (Integer index :daoPojos.keySet()) {
			StatementParameters parameters = new StatementParameters();
			addParametersByIndex(parameters, daoPojos.get(index), parser.getPrimaryKeyNames());
			parametersList[i++] = parameters;
		}
		
		int[] result = client.batchCall(callSql, parametersList, hints);
		return result;
	}
}
