package com.ctrip.platform.dal.dao.client;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.status.DalStatusManager;

public class DalStatementCreator {
	private static final int DEFAULT_RESULT_SET_TYPE = ResultSet.TYPE_FORWARD_ONLY;
	private static final int DEFAULT_RESULT_SET_CONCURRENCY = ResultSet.CONCUR_READ_ONLY;
	
	private DatabaseCategory dbCategory;
	public DalStatementCreator(DatabaseCategory dbCategory) {
	    this.dbCategory = dbCategory;
	}
	
	public Statement createStatement(Connection conn, DalHints hints) throws Exception {
		Statement statement = conn.createStatement(getResultSetType(hints), getResultSetConcurrency(hints));
		
		applyHints(statement, hints);
		
		return statement;
	}

	public PreparedStatement createPreparedStatement(Connection conn, String sql, StatementParameters parameters, DalHints hints) throws Exception {
		PreparedStatement statement = conn.prepareStatement(sql, getResultSetType(hints), getResultSetConcurrency(hints));
		
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
		PreparedStatement statement = conn.prepareStatement(sql, getResultSetType(hints), getResultSetConcurrency(hints));
		
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
	
	public CallableStatement createCallableStatement(Connection conn,  String sql, StatementParameters[] parametersList, DalHints hints) throws Exception {
		CallableStatement statement = conn.prepareCall(sql);
		
		applyHints(statement, hints);
		
		for(StatementParameters parameters: parametersList) {
			setParameter(statement, parameters);
			statement.addBatch();
		}

		return statement;
	}

	private void setParameter(PreparedStatement statement, StatementParameters parameters) throws Exception {
		for (StatementParameter parameter: parameters.values()) {
			if(parameter.isInputParameter())
			    dbCategory.setObject(statement, parameter);
		}
	}
	
	private void setParameter(CallableStatement statement, StatementParameters parameters) throws Exception {
		for (StatementParameter parameter: parameters.values()) {
			if(parameter.isInputParameter()) {
			    dbCategory.setObject(statement, parameter);
			}
		}
	}

	private void registerOutParameters(CallableStatement statement, StatementParameters parameters) throws Exception {
		for (StatementParameter parameter: parameters.values()) {
			if(parameter.isOutParameter()) {
				if(parameter.getName() == null)
					statement.registerOutParameter(parameter.getIndex(), parameter.getSqlType());
				else
					statement.registerOutParameter(parameter.getName(), parameter.getSqlType());
			}
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
		if (timeout != null && timeout >= 0) {
			statement.setQueryTimeout(timeout);
		} else {
			timeout = DalStatusManager.getTimeoutMarkdown().getTimeoutThreshold();
			if (timeout >= 0)
				statement.setQueryTimeout(timeout);
		}
		
	}
	
	private int getResultSetType(DalHints hints) {
		return hints.getInt(DalHintEnum.resultSetType, DEFAULT_RESULT_SET_TYPE);
	}

	private int getResultSetConcurrency(DalHints hints) {
		return hints.getInt(DalHintEnum.resultSetConcurrency, DEFAULT_RESULT_SET_CONCURRENCY);
	}
}
