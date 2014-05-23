package com.ctrip.platform.dal.dao.client;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.logging.DalEventEnum;
import com.ctrip.platform.dal.dao.logging.LogEntry;
import com.ctrip.platform.dal.dao.logging.Logger;
import com.ctrip.platform.dal.dao.logging.MetricsLogger;

public abstract class ConnectionAction<T> {
	DalEventEnum operation;
	String sql;
	String callString;
	String[] sqls;
	StatementParameters parameters;
	StatementParameters[] parametersList;
	DalCommand command;
	List<DalCommand> commands;
	DalConnection connHolder;
	Connection conn;
	Statement statement;
	PreparedStatement preparedStatement;
	CallableStatement callableStatement;
	ResultSet rs;
	
	long start;
	LogEntry entry;
	
	void populate(DalEventEnum operation, String sql, StatementParameters parameters) {
		this.operation = operation;
		this.sql = sql;
		this.parameters = parameters;
	}

	void populate(String[] sqls) {
		this.operation = DalEventEnum.BATCH_UPDATE;
		this.sqls = sqls;
	}
	
	void populate(String sql, StatementParameters[] parametersList) {
		this.operation = DalEventEnum.BATCH_UPDATE_PARAM;
		this.sql = sql;
		this.parametersList = parametersList;
	}
	
	void populate(DalCommand command) {
		this.operation = DalEventEnum.EXECUTE;
		this.command = command;
	}
	
	void populate(List<DalCommand> commands) {
		this.operation = DalEventEnum.EXECUTE;
		this.commands = commands;
	}
	
	void populateSp(String callString, StatementParameters parameters) {
		this.operation = DalEventEnum.CALL;
		this.callString = callString;
		this.parameters = parameters;
	}
	
	void populateSp(String callString, StatementParameters []parametersList) {
		this.operation = DalEventEnum.BATCH_CALL;
		this.callString = callString;
		this.parametersList = parametersList;
	}
	
	public void populateDbMeta() {
		if(connHolder == null || connHolder.getMeta() == null)
			return;
		
		connHolder.getMeta().populate(entry);
	}
	
	/*
	 * createLogEntry will check whether current operation is in transaction. 
	 * so it must be put after startTransaction. It is not require so for doInConnection
	 */
	public void initLogEntry(String logicDbName, DalHints hints) {
		entry = new LogEntry(hints);
		entry.setEvent(operation);
		entry.setDatabaseName(logicDbName);
		entry.setCallString(callString);
		entry.setSql(sql);
		entry.setSqls(sqls);
		entry.setParameters(parameters);
		entry.setParametersList(parametersList);
		entry.setTransactional(DalTransactionManager.isInTransaction());
	}
	
	public void start() {
		start = System.currentTimeMillis();
	}
	
	public void end(Object result, Throwable e) throws SQLException {
		log(result, e);
		handleException(e);
	}

	private void log(Object result, Throwable e) {
		try {
			long duration = System.currentTimeMillis() - start;
			if(e == null) {
				Logger.success(entry, duration, fetchQueryRows(result));
				MetricsLogger.success(entry, duration);
			}else{
				Logger.fail(entry, duration, e);
				MetricsLogger.fail(entry, duration);
			}
		} catch (Throwable e1) {
			Logger.error("Can not log", e1);
		}
	}

	private int fetchQueryRows(Object result)
	{
		return null != result && result instanceof Collection<?> ? ((Collection<?>)result).size() : 0;
	}
	
	public void cleanup() {
		Statement _statement = statement != null? 
				statement : preparedStatement != null?
						preparedStatement : callableStatement;

		DalConnectionManager.cleanup(rs, _statement, connHolder);
		
		rs = null;
		statement = null;
		preparedStatement = null;
		callableStatement = null;
		connHolder = null;
		conn = null;
	}
	
	private void handleException(Throwable e) throws SQLException {
		if(e != null)
			throw e instanceof SQLException ? (SQLException)e : new SQLException(e);
	}

	abstract T execute() throws Exception;
}
