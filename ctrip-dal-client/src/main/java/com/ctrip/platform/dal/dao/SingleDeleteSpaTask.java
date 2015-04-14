package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.Map;

public class SingleDeleteSpaTask<T> extends CtripSpaTask<T> {
	private static final String DELETE_SPA_TPL = "spA_%s_d";

	private String deleteSPA;
	
	public void initialize(DalParser<T> parser) {
		String tableName = parser.getTableName();
		deleteSPA = String.format(DELETE_SPA_TPL, tableName);
	}

	@Override
	public int execute(DalHints hints, Map<String, ?> fields) throws SQLException {
		if (null == fields) return 0;
		
		hints = DalHints.createIfAbsent(hints);

		StatementParameters parameters = new StatementParameters();
		String callSql = prepareSpCall(deleteSPA, parameters, getPrimaryKeys(fields));

		Map<String, ?> results = client.call(callSql, parameters, hints);
		
		return (Integer) results.get(RET_CODE);
	}

}