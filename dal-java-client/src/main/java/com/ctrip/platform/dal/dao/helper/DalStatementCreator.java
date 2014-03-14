package com.ctrip.platform.dal.dao.helper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalStatementCreator {
	public Statement createStatement(Connection conn, DalHints hints) throws Exception {
		Statement statement = conn.createStatement(
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		
		applyHints(statement, hints);
		
		return statement;
	}

	public PreparedStatement createPreparedStatement(Connection conn, String sql, StatementParameters parameters, DalHints hints) throws Exception {
		PreparedStatement statement = conn.prepareStatement(sql,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		
		applyHints(statement, hints);
		setParameter(statement, parameters);
		
		return statement;
	}
	
	public PreparedStatement createPreparedStatement(Connection conn, String sql, StatementParameters parameters, DalHints hints, KeyHolder keyHolder) throws Exception {
		PreparedStatement statement = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		
		applyHints(statement, hints);
		setParameter(statement, parameters);
		
		return statement;
	}
	
	public PreparedStatement createPreparedStatement(Connection conn, String sql, StatementParameters[] parametersList, DalHints hints) throws Exception {
		PreparedStatement statement = conn.prepareStatement(sql,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		
		applyHints(statement, hints);
		for(StatementParameters parameters: parametersList) {
			setParameter(statement, parameters);
			statement.addBatch();
		}
		
		return statement;
	}
	
	public CallableStatement createCallableStatement(Connection conn,  String sql, StatementParameters parameters, DalHints hints) throws Exception {
		CallableStatement statement = conn.prepareCall(sql);
		
		applyHints(statement, hints);
		setParameter(statement, parameters);
		registerOutParameters(statement, parameters);

		return statement;
	}

	private void setParameter(PreparedStatement statement, StatementParameters parameters) throws Exception {
		for (StatementParameter parameter: parameters.values()) {
			if(parameter.isInputParameter())
				statement.setObject(parameter.getIndex(), parameter.getValue(), parameter.getSqlType());
		}
	}
	
	private void setParameter(CallableStatement statement, StatementParameters parameters) throws Exception {
		for (StatementParameter parameter: parameters.values()) {
			if(parameter.isInputParameter()) {
				if(parameter.getValue() == null)
					statement.setNull(parameter.getName(), parameter.getSqlType());
				else
					statement.setObject(parameter.getName(), parameter.getValue(), parameter.getSqlType());
			}
		}
	}
	
	private void registerOutParameters(CallableStatement statement, StatementParameters parameters) throws Exception {
		for (StatementParameter parameter: parameters.values()) {
			if(parameter.isOutParameter())
				statement.registerOutParameter(parameter.getName(), parameter.getSqlType());
		}
	}
	
	private void applyHints(Statement statement, DalHints hints) throws SQLException {
		Integer fetchSize = (Integer)hints.get(DalHintEnum.fetchSize);
		
		if(fetchSize != null && fetchSize > 0)
			statement.setFetchSize(fetchSize);

		Integer maxRows = (Integer)hints.get(DalHintEnum.maxRows);
		if (maxRows != null && maxRows > 0)
			statement.setMaxRows(maxRows);

		Integer timeout = (Integer)hints.get(DalHintEnum.timeout);
		if (timeout != null && timeout > 0)
			statement.setQueryTimeout(timeout);
	}
}
