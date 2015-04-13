package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.Map;

public class SingleUpdateSpaTask<T> extends CtripSpaTask<T> {
	private static final String UPDATE_SPA_TPL = "spA_%s_u";
	private String updateSPA;
	
	private static final String RET_CODE = "retcode";
	
	public void initialize(DalParser<T> parser) {
		String tableName = parser.getTableName();
		updateSPA = String.format(UPDATE_SPA_TPL, tableName);		
	}

	@Override
	public int execute(DalHints hints, Map<String, ?> fields) throws SQLException {
		if (null == fields) return 0;
		
		hints = DalHints.createIfAbsent(hints);

		StatementParameters parameters = new StatementParameters();
		String callSql = prepareSpCall(updateSPA, parameters, fields);

		Map<String, ?> results = client.call(callSql, parameters, hints);

		return (Integer) results.get(RET_CODE);
	}
}
