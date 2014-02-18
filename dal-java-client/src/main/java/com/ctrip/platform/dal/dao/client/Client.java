package com.ctrip.platform.dal.dao.client;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.StatementParameter;

/**
 * @deprecated
 * @author jhhe
 *
 */
public interface Client {
	
	ResultSet fetch(String sql, List<StatementParameter> parameters, Map keywordParameters);
	
	int execute(String sql, List<StatementParameter> parameters, Map keywordParameters);
	
	ResultSet fetchBySp(String sql, List<StatementParameter> parameters, Map keywordParameters);
	
	int executeSp(String sql, List<StatementParameter> parameters, Map keywordParameters);

	/**
	 * After each fetch operation, connection need to be closed explicitly.
	 * For execute, we don't need to do it
	 * @throws SQLException
	 */
	void closeConnection();
}
