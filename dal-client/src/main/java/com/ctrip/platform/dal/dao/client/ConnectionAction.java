package com.ctrip.platform.dal.dao.client;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.Version;

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
	public Set<String> usedDbs = new HashSet<>();
	public Connection conn;
	public Statement statement;
	public PreparedStatement preparedStatement;
	public CallableStatement callableStatement;
	public ResultSet rs;
	public long start;
	
	public DalLogger logger = DalClientFactory.getDalLogger();
	public LogEntry entry;
	
	void populate(DalEventEnum operation, String sql, StatementParameters parameters) {
		this.operation = operation;
		this.sql = this.wrapAPPID(sql);
		this.parameters = parameters;
	}

	void populate(String[] sqls) {
		this.operation = DalEventEnum.BATCH_UPDATE;
		this.sqls = sqls;
		for(int i = 0; i < this.sqls.length; i++){
			this.sqls[i] = this.wrapAPPID(this.sqls[i]);
		}
	}
	
	void populate(String sql, StatementParameters[] parametersList) {
		this.operation = DalEventEnum.BATCH_UPDATE_PARAM;
		this.sql = this.wrapAPPID(sql);
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

		entry.setTransactional(DalTransactionManager.isInTransaction());

		if(DalTransactionManager.isInTransaction()) {
			meta = DalTransactionManager.getCurrentDbMeta();
		} else {
			if(connHolder != null)
				meta = connHolder.getMeta();
		}
		
		if(meta != null)
			meta.populate(entry);
	}
	
	public void initLogEntry(String logicDbName, DalHints hints) {
		this.entry = logger.createLogEntry();
		
		entry.setClientVersion(Version.getVersion());
		entry.setSensitive(hints.is(DalHintEnum.sensitive));
		entry.setEvent(operation);
		entry.setCallString(callString);
		
		if(sqls != null)	
			entry.setSqls(sqls);
		else
			entry.setSqls(sql);

		if (null != parametersList) {
			String[] params = new String[parametersList.length];
			for (int i = 0; i < parametersList.length; i++) {
				params[i] = parametersList[i].toLogString();
			}
			entry.setPramemters(params);
		} else if (parameters != null) {
			entry.setPramemters(parameters.toLogString());
			hints.setParameters(parameters);
		}
	}
	
	public void start() {
		start = System.currentTimeMillis();
		logger.start(entry);
	}
	
	public void end(Object result, Throwable e) throws SQLException {
		log(result, e);	
		handleException(e);
	}

	private void log(Object result, Throwable e) {
		try {
			entry.setDuration(System.currentTimeMillis() - start);
			if(e == null) {
				logger.success(entry, fetchQueryRows(result));
			}else{
				logger.fail(entry, e);
			}
		} catch (Throwable e1) {
			logger.error("Can not log", e1);
		}
	}

	private int fetchQueryRows(Object result) {
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
				logger.error("Close result set failed.", e);
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
				logger.error("Close statement failed.", e);
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

	private String wrapAPPID(String sql){
		return "/*" + DalClientFactory.getDalLogger().getAppID() + "*/" + sql;
	}
	
	public abstract T execute() throws Exception;
}
