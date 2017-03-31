package com.ctrip.platform.dal.dao;

import java.util.Map;

import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;
import com.ctrip.platform.dal.dao.task.SingleTask;
import com.ctrip.platform.dal.dao.task.TaskAdapter;

public abstract class CtripSpaTask<T> extends TaskAdapter<T> implements SingleTask<T> {
	public static final String RET_CODE = "retcode";
	private DalScalarExtractor extractor = new DalScalarExtractor();
	
	public String prepareSpCall(String SpName, StatementParameters parameters, Map<String, ?> fields) {
		if(CtripTaskFactory.callSpbyName)
			addParametersByName(parameters, fields);
		else
			addParametersByIndex(parameters, fields);
		
		String callSql = buildCallSql(SpName, fields.size());
		parameters.setResultsParameter(RET_CODE, extractor);
		return callSql;
	}
}
