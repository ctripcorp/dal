package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ctrip.platform.dal.common.enums.ParameterDirection;

public class SingleInsertSpaTask<T> extends CtripSpaTask<T> {
	private static final String INSERT_SPA_TPL = "spA_%s_i";

	private String insertSPA;
	private String outputIdName;
	
	private static final String RET_CODE = "retcode";
	
	public SingleInsertSpaTask() {
	}

	public void initialize(DalParser<T> parser) {
		super.initialize(parser);
		String tableName = parser.getTableName();
		insertSPA = String.format(INSERT_SPA_TPL, tableName);
		this.outputIdName = parser.isAutoIncrement() ? parser.getPrimaryKeyNames()[0] : null;
	}
	
	@Override
	public int execute(DalHints hints, Map<String, ?> fields) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		StatementParameters parameters = new StatementParameters();
		String callSql = prepareSpCall(insertSPA, parameters, fields);
		
		register(parameters, fields);
		Map<String, ?> results = client.call(callSql, parameters, hints);
		extract(parameters, hints.getKeyHolder());

		return (Integer)results.get(RET_CODE);
	}
	
	private void register(StatementParameters parameters, Map<String, ?> fields) {
		if(outputIdName != null) {
			parameters.registerInOut(outputIdName, getColumnType(outputIdName), fields.get(outputIdName));
		}
	}
	
	private void extract(StatementParameters parameters, KeyHolder holder) {
		if(holder == null) return;
		
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if(outputIdName != null) {
			map.put(outputIdName, parameters.get(outputIdName, ParameterDirection.InputOutput).getValue());
		}
		
		holder.addKey(map);
	}
}
