package com.ctrip.platform.dao.client;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dao.StatementParameter;

public interface Client {
	
	@SuppressWarnings("rawtypes")
	ResultSet fetch(String sql, List<StatementParameter> parameters, Map keywordParameters) throws SQLException;
	
	@SuppressWarnings("rawtypes")
	int execute(String sql, List<StatementParameter> parameters, Map keywordParameters) throws SQLException;
	
	@SuppressWarnings("rawtypes")
	ResultSet fetchBySp(String sql, List<StatementParameter> parameters, Map keywordParameters) throws SQLException;
	
	@SuppressWarnings("rawtypes")
	int executeSp(String sql, List<StatementParameter> parameters, Map keywordParameters) throws SQLException;

	/**
	 * After each fetch operation, connection need to be closed explicitly.
	 * For execute, we don't need to do it
	 * @throws SQLException
	 */
	void closeConnection() throws SQLException ;
}
