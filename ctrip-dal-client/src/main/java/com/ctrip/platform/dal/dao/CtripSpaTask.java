package com.ctrip.platform.dal.dao;


import java.util.Map;

import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;
import com.ctrip.platform.dal.dao.task.SingleTask;
import com.ctrip.platform.dal.dao.task.TaskAdapter;

/**
 * Using Sqlserver special syntax to speed up execution.
 * 
 * Example: exec spA_TestUnicode_u @id=?, @nvarchar1=?, @varchar1=?
 * 
 * @author jhhe
 *
 * @param <T>
 */
public abstract class CtripSpaTask<T> extends TaskAdapter<T> implements SingleTask<T> {
	public static final String RET_CODE = "retcode";
	private DalScalarExtractor extractor = new DalScalarExtractor();
	protected static final String CALL_SP_BY_NAME = "callSpbyName";
	protected static final String CALL_SP_BY_SQLSEVER = "callSpbySqlServerSyntax";
	protected static final String CALL_SPT = "callSpt";

	public void initialize(DalParser<T> parser) {
        super.initialize(parser);
        CallSpByIndexValidator.validate(parser, Boolean.parseBoolean(getTaskSetting(CALL_SP_BY_NAME)));
    }
	
	public String prepareSpCall(String spName, StatementParameters parameters, Map<String, ?> fields) {
	    String callSql;
	    if(Boolean.parseBoolean(getTaskSetting(CALL_SP_BY_SQLSEVER))) {
	        callSql = prepareSpCallForSqlServer(spName, parameters, fields);
	    }else{
	        callSql = prepareSpCallForNameOrSpt(spName, parameters, fields);
	    }        

        parameters.setResultsParameter(RET_CODE, extractor);
        return callSql;
	}

	protected String prepareSpCallForSqlServer(String spName, StatementParameters parameters, Map<String, ?> fields) {
	    String callSql = buildSqlServerCallSql(spName, fields.keySet().toArray(new String[fields.size()]));
        addParametersByIndex(parameters, fields);
        return callSql;
    }

    protected String prepareSpCallForNameOrSpt(String spName, StatementParameters parameters, Map<String, ?> fields) {
	    String callSql = buildCallSql(spName, fields.size());
        if(Boolean.parseBoolean(getTaskSetting(CALL_SP_BY_NAME)))
            addParametersByName(parameters, fields);
        else
            addParametersByIndex(parameters, fields);
        return callSql;
    }

	protected String buildSqlServerCallSqlNotNullField(String spName, Map<String, ?> fields) {
		StringBuilder valuesSb = new StringBuilder();
		for (Map.Entry<String, ?> field : fields.entrySet()) {
			String column = field.getKey();
			if (field.getValue() != null || isPrimaryKey(column)) {
				valuesSb.append(String.format(SQLSERVER_TMPL_SET_VALUE, column)).append(COLUMN_SEPARATOR);
			}
		}
		if (valuesSb.length() >= 2)
			valuesSb.delete(valuesSb.length() - 2, valuesSb.length());
		return String.format(SQLSERVER_TMPL_CALL, spName, valuesSb.toString());
	}

	protected void addParametersByIndexNotNullField(StatementParameters parameters, Map<String, ?> entries) {
		int index = parameters.size() + 1;
		for (Map.Entry<String, ?> entry : entries.entrySet()) {
			String column = entry.getKey();
			Object value = entry.getValue();
			if (value != null || isPrimaryKey(column)) {
				addParameterByIndex(parameters, index++, column, value);
			}
		}
	}

}