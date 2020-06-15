package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.task.DalContextConfigure;
import com.ctrip.platform.dal.dao.task.DalTaskContext;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.sql.SQLException;
import java.util.Map;

public class SingleUpdateSpaTask<T> extends CtripSpaTask<T> {
	private static final String UPDATE_SPA_TPL = "spA_%s_u";
	
	@Override
	public int execute(DalHints hints, Map<String, ?> fields, T rawPojos, DalTaskContext taskContext) throws SQLException {
		if (null == fields) return 0;
		
		hints = DalHints.createIfAbsent(hints);
		String tableName = getRawTableName(hints, fields);
		String updateSPA = String.format(UPDATE_SPA_TPL, tableName);
		
		StatementParameters parameters = new StatementParameters();
		String callSql = prepareSpCall(updateSPA, parameters, fields);

		if (taskContext instanceof DalContextConfigure)
			((DalContextConfigure) taskContext).addTables(tableName);

		if (client instanceof DalContextClient) {
			Map<String, ?> results = ((DalContextClient) client).call(callSql, parameters, hints.setFields(fields), taskContext);
			return (Integer) results.get(RET_CODE);
		} else
			throw new DalRuntimeException("The client is not instance of DalClient");
	}

	@Override
    protected String prepareSpCallForSqlServer(String spName, StatementParameters parameters, Map<String, ?> fields) {
        String callSql = buildSqlServerCallSqlNotNullField(spName, fields);
        addParametersByIndexNotNullField(parameters, fields);
        return callSql;
    }
}
