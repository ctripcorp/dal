package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.task.DalBulkTaskContext;
import com.ctrip.platform.dal.dao.task.DalContextConfigure;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;


public class BatchDeleteSp3Task<T> extends CtripSp3Task<T> {
	private static final String DELETE_SP3_TPL = "sp3_%s_d";

	@Override
	public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos, DalBulkTaskContext<T> taskContext) throws SQLException {
		String tableName=getRawTableName(hints);
		String deleteSP3 = String.format(DELETE_SP3_TPL, tableName);
		
		String callSql = buildCallSql(deleteSP3, parser.getPrimaryKeyNames().length);
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];

		int i = 0;
		for (Integer index :daoPojos.keySet()) {
			StatementParameters parameters = new StatementParameters();

			if(CtripTaskFactory.callSpbyName)
				addParametersByName(parameters, daoPojos.get(index), parser.getPrimaryKeyNames());
			else
				addParametersByIndex(parameters, daoPojos.get(index), parser.getPrimaryKeyNames());
			
			parametersList[i++] = parameters;
		}

		if (taskContext instanceof DalContextConfigure)
			((DalContextConfigure) taskContext).addTables(tableName);

		if (client instanceof DalContextClient)
			return ((DalContextClient) client).batchCall(callSql, parametersList, hints, taskContext);
		else
			throw new DalRuntimeException("The client is not instance of DalClient");
	}
}
