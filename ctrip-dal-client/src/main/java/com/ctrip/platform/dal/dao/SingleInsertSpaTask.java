package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ctrip.platform.dal.common.enums.ParameterDirection;

public class SingleInsertSpaTask<T> extends CtripSpaTask<T> {
	private static final String INSERT_SPA_TPL = "spA_%s_i";

	private String insertSPA;
	private String[] inOutPramNames;
	private String[] outputPramNames;
	
	private static final String RET_CODE = "retcode";
	
	public SingleInsertSpaTask(String[] inOutPramNames, String[] outputPramNames) {
		this.inOutPramNames = inOutPramNames;
		this.outputPramNames = outputPramNames;
	}

	public void initialize(DalParser<T> parser) {
		String tableName = parser.getTableName();
		insertSPA = String.format(INSERT_SPA_TPL, tableName);
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
		if(inOutPramNames != null) {
			for(String name: inOutPramNames)
				parameters.registerInOut(name, getColumnType(name), fields.get(name));
		}
		
		if(outputPramNames != null){
			for(String name: outputPramNames)
				parameters.registerOut(name, getColumnType(name));
		}
	}
	
	private void extract(StatementParameters parameters, KeyHolder holder) {
		if(holder == null) return;
		
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if(inOutPramNames != null) {
			for(String name: inOutPramNames)
				map.put(name, parameters.get(name, ParameterDirection.InputOutput).getValue());
		}
		
		if(outputPramNames != null){
			for(String name: outputPramNames)
				map.put(name, parameters.get(name, ParameterDirection.Output).getValue());
		}
		
		holder.getKeyList().add(map);
	}
}
