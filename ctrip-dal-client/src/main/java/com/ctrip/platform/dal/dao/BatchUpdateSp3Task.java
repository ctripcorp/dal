package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.task.AbstractIntArrayBulkTask;
import com.ctrip.platform.dal.dao.task.BulkTaskContext;

public class BatchUpdateSp3Task<T> extends AbstractIntArrayBulkTask<T> {
	private static final String UPDATE_SP3_TPL = "sp3_%s_u";
	
	@Override
	public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos, BulkTaskContext<T> tastContext) throws SQLException {
		String updateSP3 = String.format(UPDATE_SP3_TPL, getRawTableName(hints));
		
		String callSql = buildCallSql(updateSP3, parser.getColumnNames().length);
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
		
		int[] result = client.batchCall(callSql, parametersList, hints);
		return result;
	}
}
