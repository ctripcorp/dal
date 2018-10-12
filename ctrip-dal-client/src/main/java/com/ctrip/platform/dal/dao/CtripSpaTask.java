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
	        callSql = CtripSqlServerSpBuilder.buildSqlServerCallSql(spName, fields.keySet().toArray(new String[fields.size()]));
	        addParametersByIndex(parameters, fields);
	    }else{
	        callSql = buildCallSql(spName, fields.size());
            if(Boolean.parseBoolean(getTaskSetting(CALL_SP_BY_NAME)))
                addParametersByName(parameters, fields);
            else
                addParametersByIndex(parameters, fields);
	    }        

        parameters.setResultsParameter(RET_CODE, extractor);
        return callSql;
	}
}