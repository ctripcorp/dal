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
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.sql.logging.DalEventEnum;
import com.ctrip.platform.dal.sql.logging.LogEntry;
import com.ctrip.platform.dal.sql.logging.Logger;
import com.ctrip.platform.dal.sql.logging.MetricsLogger;

public abstract class ConnectionAction<T> {
	public DalEventEnum operation;
	public String sql;
	public String callString;
	public String[] sqls;
	public StatementParameters parameters;
	public StatementParameters[] parametersList;
	public DalCommand command;
	public List<DalCommand> commands;
	public DalConnection connHolder;
	public Connection conn;
	public Statement statement;
	public PreparedStatement preparedStatement;
	public CallableStatement callableStatement;
	public ResultSet rs;
	
	public long start;
	public LogEntry entry = new LogEntry();
	
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
		DbMeta meta = null;
		
		if(DalTransactionManager.isInTransaction()) {
			meta = DalTransactionManager.getCurrentDbMeta();
		} else {
			if(connHolder != null)
				meta = connHolder.getMeta();
		}
		
		if(meta != null)
			meta.populate(entry);
	}
	
	/*
	 * createLogEntry will check whether current operation is in transaction. 
	 * so it must be put after startTransaction. It is not require so for doInConnection
	 */
	public void initLogEntry(String logicDbName, DalHints hints) {
		entry.setSensitive(hints.is(DalHintEnum.sensitive));
		entry.setEvent(operation);
		entry.setCommandType();
		entry.setCallString(callString);
		if(sqls != null)	
			entry.setSqls(sqls);
		else
			entry.setSqls(sql);

		if(null != parametersList)
		{
			String[] params = new String[parametersList.length];
			for (int i = 0; i < parametersList.length; i++) {
				params[i] = parametersList[i].toLogString();
			}
			entry.setPramemters(params);
		}
		else if(parameters != null){
			entry.setPramemters(parameters.toLogString());
		}
		
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
		closeResultSet();
		closeStatement();
		closeConnection();
	}
	
	private void closeResultSet() {
		if(rs != null) {
			try {
				rs.close();
			} catch (Throwable e) {
				Logger.error("Close result set failed.", e);
			}
		}
		rs = null;
	}
	
	private void closeStatement() {
		Statement _statement = statement != null? 
				statement : preparedStatement != null?
						preparedStatement : callableStatement;

		statement = null;
		preparedStatement = null;
		callableStatement = null;
		
		if(_statement != null) {
			try {
				_statement.close();
			} catch (Throwable e) {
				Logger.error("Close statement failed.", e);
			}
		}		
	}

	private void closeConnection() {
		//do nothing for connection in transaction
		if(DalTransactionManager.isInTransaction())
			return;
		
		// For list of nested commands, the top level action will not hold any connHolder
		if(connHolder == null)
			return;
		
		connHolder.close();
		
		connHolder = null;
		conn = null;
	}

	private void handleException(Throwable e) throws SQLException {
		if(e != null)
			throw e instanceof SQLException ? (SQLException)e : new SQLException(e);
	}

	public abstract T execute() throws Exception;
}
