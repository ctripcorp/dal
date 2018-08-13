package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.task.*;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

public class BatchInsertSp3Task<T> extends CtripSp3Task<T> {
	private static final String INSERT_SP3_TPL = "sp3_%s_i";

	@Override
	public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos, DalBulkTaskContext<T> taskContext) throws SQLException {
		String tableName = getRawTableName(hints);
		String insertSP3 = String.format(INSERT_SP3_TPL, tableName);

		String callSql = buildCallSql(insertSP3, parser.getColumnNames().length);
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		
		int i = 0;
		for (Integer index :daoPojos.keySet()) {
			StatementParameters parameters = new StatementParameters();

			if(CtripTaskFactory.callSpbyName)
				addParametersByName(parameters, daoPojos.get(index));
			else
				addParametersByIndex(parameters, daoPojos.get(index));
			
			parametersList[i++] = parameters;
		}

		if (taskContext instanceof DalTableNameConfigure)
			((DalTableNameConfigure) taskContext).addTables(tableName);

		if (client instanceof DalContextClient)
			return ((DalContextClient) client).batchCall(callSql, parametersList, hints, taskContext);
		else
			throw new DalRuntimeException("The client is not instance of DalClient");
	}
}
