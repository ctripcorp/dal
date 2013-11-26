package com.ctrip.platform.dao.client;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dao.param.StatementParameter;

public interface Client {
	
	@SuppressWarnings("rawtypes")
	ResultSet fetch(String sql, List<StatementParameter> parameters, Map keywordParameters);
	
	@SuppressWarnings("rawtypes")
	int execute(String sql, List<StatementParameter> parameters, Map keywordParameters);
	
	@SuppressWarnings("rawtypes")
	ResultSet fetchBySp(String sql, List<StatementParameter> parameters, Map keywordParameters);
	
	@SuppressWarnings("rawtypes")
	int executeSp(String sql, List<StatementParameter> parameters, Map keywordParameters);

}
