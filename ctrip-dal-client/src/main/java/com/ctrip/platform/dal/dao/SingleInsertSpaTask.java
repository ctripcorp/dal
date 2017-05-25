package com.ctrip.platform.dal.dao;

import static com.ctrip.platform.dal.common.enums.ParameterDirection.InputOutput;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ctrip.platform.dal.common.enums.ParameterDirection;

public class SingleInsertSpaTask<T> extends CtripSpaTask<T> {
	private static final String INSERT_SPA_TPL = "spA_%s_i";

	private String outputIdName;
	
	private static final String RET_CODE = "retcode";
	
	public SingleInsertSpaTask() {
	}

	public void initialize(DalParser<T> parser) {
		super.initialize(parser);
		outputIdName = parser.isAutoIncrement() ? parser.getPrimaryKeyNames()[0] : null;		
	}
	
	@Override
	public int execute(DalHints hints, Map<String, ?> fields, T rawPojos) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		String insertSPA = String.format(INSERT_SPA_TPL, getRawTableName(hints, fields));
		
		StatementParameters parameters = new StatementParameters();
		String callSql = prepareSpCall(insertSPA, parameters, fields);
		
		register(parameters, fields);
		Map<String, ?> results = client.call(callSql, parameters, hints.setFields(fields));
		extract(parameters, hints.getKeyHolder());

		return (Integer)results.get(RET_CODE);
	}
	
	private void register(StatementParameters parameters, Map<String, ?> fields) {
	    if(CtripTaskFactory.callSpbyName) {
    	    if(outputIdName != null) {
                parameters.registerInOut(outputIdName, getColumnType(outputIdName), fields.get(outputIdName));
    	    }
	    }else {
	        /**
	         * Must to be first one
	         */
    		if(outputIdName != null) {
    			parameters.get(0).setDirection(InputOutput);
    		}
	    }
	}
	
	private void extract(StatementParameters parameters, KeyHolder holder) {
		if(holder == null) return;
		
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		
		if(CtripTaskFactory.callSpbyName) {
		    if(outputIdName != null) {
		        map.put(outputIdName, parameters.get(outputIdName, ParameterDirection.InputOutput).getValue());
		    }
		}else{
            /**
             * Must to be first one
             */
	        if(outputIdName != null) {
	            map.put(outputIdName, parameters.get(0).getValue());
	        }
		}
		
		holder.addKey(map);
	}
}
