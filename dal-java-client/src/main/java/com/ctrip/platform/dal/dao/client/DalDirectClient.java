package com.ctrip.platform.dal.dao.client;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.common.db.DruidDataSourceWrapper;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.helper.DalColumnMapRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalStatementCreator;
import com.ctrip.platform.dal.dao.helper.DalTransactionManager;
import com.ctrip.platform.dal.dao.logging.CommonUtil;
import com.ctrip.platform.dal.dao.logging.DalEventEnum;
import com.ctrip.platform.dal.dao.logging.LogEntry;
import com.ctrip.platform.dal.dao.logging.Logger;
import com.ctrip.platform.dal.dao.logging.MetricsLogger;

/**
 * The direct connection implementation for DalClient.
 * @author jhhe
 */
public class DalDirectClient implements DalClient {
	private DalStatementCreator stmtCreator = new DalStatementCreator();
	private DalTransactionManager transManager;
	private String logicDbName;

	public DalDirectClient(DruidDataSourceWrapper connPool, String logicDbName) {
		transManager = new DalTransactionManager(logicDbName, connPool);
		this.logicDbName = logicDbName;
	}
	
	public DalDirectClient(DalConfigure config, String logicDbName) {
		transManager = new DalTransactionManager(config, logicDbName);
		this.logicDbName = logicDbName;
	}

	@Override
	public <T> T query(String sql, StatementParameters parameters, final DalHints hints, final DalResultSetExtractor<T> extractor)
			throws SQLException {
		ConnectionAction<T> action = new ConnectionAction<T>() {
			@Override
			T execute() throws Exception {
				conn = getConnection(hints);
				
				preparedStatement = createPreparedStatement(conn, sql, parameters, hints);
				rs = preparedStatement.executeQuery();
				return extractor.extract(rs);
			}
		};
		action.populate(DalEventEnum.QUERY, sql, parameters);
		
		return doInConnection(action, hints);
	}

	@Override
	public int update(String sql, StatementParameters parameters, final DalHints hints)
			throws SQLException {
		ConnectionAction<Integer> action = new ConnectionAction<Integer>() {
			@Override
			Integer execute() throws Exception {
				conn = getConnection(hints);
				
				preparedStatement = createPreparedStatement(conn, sql, parameters, hints);
				
				return preparedStatement.executeUpdate();
			}
		};
		action.populate(DalEventEnum.UPDATE_SIMPLE, sql, parameters);
		
		return doInConnection(action, hints);
	}

	@Override
	public int update(String sql, StatementParameters parameters,
			final DalHints hints, final KeyHolder generatedKeyHolder) throws SQLException {
		ConnectionAction<Integer> action = new ConnectionAction<Integer>() {
			@Override
			Integer execute() throws Exception {
				conn = getConnection(hints);

				preparedStatement = createPreparedStatement(conn, sql, parameters, hints, generatedKeyHolder);
				int rows = preparedStatement.executeUpdate();
				
				List<Map<String, Object>> generatedKeys = generatedKeyHolder.getKeyList();
				rs = preparedStatement.getGeneratedKeys();
				if (rs == null)
					return rows;
				
				DalRowMapperExtractor<Map<String, Object>> rse =
						new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper());
				generatedKeys.addAll(rse.extract(rs));
				return rows;
			}
		};
		action.populate(DalEventEnum.UPDATE_KH, sql, parameters);
		
		return doInConnection(action, hints);
	}

	@Override
	public int[] batchUpdate(String[] sqls, final DalHints hints) throws SQLException {
		ConnectionAction<int[]> action = new ConnectionAction<int[]>() {
			@Override
			int[] execute() throws Exception {
				conn = getConnection(hints);
				statement = createStatement(conn, hints);
				for(String sql: sqls)
					statement.addBatch(sql);
				
				return statement.executeBatch();
			}
		};
		action.populate(sqls);
		
		return executeBatch(action, hints);
	}

	@Override
	public int[] batchUpdate(String sql, StatementParameters[] parametersList,
			final DalHints hints) throws SQLException {
		ConnectionAction<int[]> action = new ConnectionAction<int[]>() {
			@Override
			int[] execute() throws Exception {
				conn = getConnection(hints);
				
				statement = createPreparedStatement(conn, sql, parametersList, hints);
				
				return statement.executeBatch();
			}
		};
		action.populate(sql, parametersList);
		
		return executeBatch(action, hints);
	}

	@Override
	public void execute(DalCommand command, DalHints hints) throws SQLException {
		final DalClient client = this;
		ConnectionAction<?> action = new ConnectionAction<Object>() {
			@Override
			Object execute() throws Exception {
				command.execute(client);
				return null;
			}
		};
		action.populate(command);
		
		doInTransaction(action, hints);
	}
	
	@Override
	public void execute(final List<DalCommand> commands, final DalHints hints)
			throws SQLException {
		final DalClient client = this;
		ConnectionAction<?> action = new ConnectionAction<Object>() {
			@Override
			Object execute() throws Exception {
				for(DalCommand cmd: commands) {
					if(!cmd.execute(client))
						break;
				}
				
				return null;
			}
		};
		action.populate(commands);
		
		doInTransaction(action, hints);
	}

	@Override
	public Map<String, ?> call(String callString,
			StatementParameters parameters, final DalHints hints) throws SQLException {
		ConnectionAction<Map<String, ?>> action = new ConnectionAction<Map<String, ?>>() {
			@Override
			Map<String, ?> execute() throws Exception {
				List<StatementParameter> resultParameters = new ArrayList<StatementParameter>();
				List<StatementParameter> callParameters = new ArrayList<StatementParameter>();
				for (StatementParameter parameter : parameters.values()) {
					if (parameter.isResultsParameter()) {
						resultParameters.add(parameter);
					} else 
					if(parameter.isOutParameter()){
						callParameters.add(parameter);
					}
				}

				conn = getConnection(hints);
				
				callableStatement = createCallableStatement(conn, callString, parameters, hints);
				boolean retVal = callableStatement.execute();
				int updateCount = callableStatement.getUpdateCount();
				Map<String, Object> returnedResults = new LinkedHashMap<String, Object>();
				if (retVal || updateCount != -1) {
					returnedResults.putAll(extractReturnedResults(callableStatement, resultParameters, updateCount, hints));
				}
				returnedResults.putAll(extractOutputParameters(callableStatement, callParameters));
				return returnedResults;
			}
		};
		action.populateSp(callString, parameters);
		
		return doInConnection(action, hints);
	}
	

	@Override
	public int[] batchCall(String callString,
			StatementParameters[] parametersList, final DalHints hints)
			throws SQLException {
		ConnectionAction<int[]> action = new ConnectionAction<int[]>() {
			@Override
			int[] execute() throws Exception {

				conn = getConnection(hints);
				
				callableStatement = createCallableStatement(conn, callString, parametersList, hints);

				return callableStatement.executeBatch();
			}
		};
		action.populateSp(callString, parametersList);
		
		return executeBatch(action, hints);
	}
	
	private Map<String, Object> extractReturnedResults(CallableStatement statement, List<StatementParameter> resultParameters, int updateCount, DalHints hints) throws SQLException {

		Map<String, Object> returnedResults = new LinkedHashMap<String, Object>();
		boolean moreResults;
		if(hints != null && hints.is(DalHintEnum.skipResultsProcessing))
			return returnedResults;

//		boolean skipUndeclaredResults = hints != null && hints.contains(DalHintEnum.skipUndeclaredResults);
		if(resultParameters.size() == 0) {
			// Just filter out all return values
			do {
				moreResults = statement.getMoreResults();
				updateCount = statement.getUpdateCount();
			}
			while (moreResults || updateCount != -1);
			return returnedResults;
		}
		
		int index = 0;
		do {
			
			String key = resultParameters.get(index).getName();
			Object value = updateCount == -1?
				resultParameters.get(index).getResultSetExtractor().extract(statement.getResultSet()) :
				updateCount;
			moreResults = statement.getMoreResults();
			updateCount = statement.getUpdateCount();
			index++;
			returnedResults.put(key, value);
		}
		while (moreResults || updateCount != -1);

		return returnedResults;
	}
	
	private Map<String, Object> extractOutputParameters(CallableStatement statement, List<StatementParameter> callParameters) 
			throws SQLException {

		Map<String, Object> returnedResults = new LinkedHashMap<String, Object>();
		for (StatementParameter parameter : callParameters) {
			Object value = statement.getObject(parameter.getName());
			parameter.setValue(value);
			if (value instanceof ResultSet) {
				value = parameter.getResultSetExtractor().extract(statement.getResultSet());
			}
			returnedResults.put(parameter.getName(), value);
		}
		return returnedResults;
	}
	
	private <T> T executeBatch(ConnectionAction<T> action, DalHints hints) 
			throws SQLException  {
		if(hints.is(DalHintEnum.forceAutoCommit)){
			return doInConnection(action, hints);
		}else{
			return doInTransaction(action, hints);
		}
	}
	
	private <T> T doInConnection(ConnectionAction<T> action, DalHints hints)
			throws SQLException {
		LogEntry entry = createLogEntry(action, hints);
		long start = start();
		Throwable ex = null;
		T result = null;
		
		try {
			result = action.execute();
		} catch (Throwable e) {
			ex = e;
		} finally {
			action.populate(entry);
			cleanup(hints, action);
		}
		
		log(entry, start, result, ex);
		handleException(ex);
		
		return result;
	}
	
	private <T> T doInTransaction(ConnectionAction<T> action, DalHints hints)
			throws SQLException {
		LogEntry entry = null;
		int level = 0;
		long start = start();
		Throwable ex = null;
		T result = null;
		
		try {
			level = startTransaction(hints, action.operation);
			
			entry = createLogEntry(action, hints);
			populate(entry);
			
			result = action.execute();	
			
			endTransaction(level);
			return result;
		} catch (Throwable e) {
			ex = e;
		} finally {
			cleanup(hints, action);
		}
		
		log(entry, start, result, ex);
		handleException(ex, level);
		
		return result;
	}
	
	/*
	 * createLogEntry will check whether current operation is in transaction. 
	 * so it must be put after startTransaction. It is not require so for doInConnection
	 */
	private LogEntry createLogEntry(ConnectionAction<?> action, DalHints hints) {
		LogEntry entry = new LogEntry(hints);
		entry.setEvent(action.operation);
		entry.setDatabaseName(logicDbName);
		entry.setCallString(action.callString);
		entry.setSql(action.sql);
		entry.setSqls(action.sqls);
		entry.setParameters(action.parameters);
		entry.setParametersList(action.parametersList);
		entry.setTransactional(transManager.isInTransaction());
		
		return entry;
	}
	
	private long start() {
		return System.currentTimeMillis();
	}
	
	private void log(LogEntry entry, long start, Object result, Throwable e) throws SQLException {
		long duration = start() - start;
		if(e == null) {
			Logger.success(entry, duration, fetchQueryRows(result));
			MetricsLogger.success(entry, duration);
		}else{
			Logger.fail(entry, duration, e);
			MetricsLogger.fail(entry, duration);
		}
	}

	private int fetchQueryRows(Object result)
	{
		return null != result && result instanceof Collection<?> ? ((Collection<?>)result).size() : 0;
	}
	
	private abstract class ConnectionAction<T> {
		DalEventEnum operation;
		String sql;
		String callString;
		String[] sqls;
		StatementParameters parameters;
		StatementParameters[] parametersList;
		DalCommand command;
		List<DalCommand> commands;
		ConnectionHolder connHolder;
		Connection conn;
		Statement statement;
		PreparedStatement preparedStatement;
		CallableStatement callableStatement;
		ResultSet rs;
		
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
		
		public Connection getConnection(DalHints hints) throws SQLException {
			connHolder = transManager.getConnection(hints, operation);
			return connHolder.getConn();
		}
		
		public void populate(LogEntry entry) {
			connHolder.getMeta().populate(entry);
		}
		
		abstract T execute() throws Exception;
	}
	
	private Statement createStatement(Connection conn, DalHints hints) throws Exception {
		return stmtCreator.createStatement(conn, hints);
	}

	private PreparedStatement createPreparedStatement(Connection conn, String sql, StatementParameters parameters, DalHints hints) throws Exception {
		return stmtCreator.createPreparedStatement(conn, sql, parameters, hints);
	}
	
	private PreparedStatement createPreparedStatement(Connection conn, String sql, StatementParameters parameters, DalHints hints, KeyHolder keyHolder) throws Exception {
		return stmtCreator.createPreparedStatement(conn, sql, parameters, hints, keyHolder);
	}
	
	private PreparedStatement createPreparedStatement(Connection conn, String sql, StatementParameters[] parametersList, DalHints hints) throws Exception {
		return stmtCreator.createPreparedStatement(conn, sql, parametersList, hints);
	}

	private CallableStatement createCallableStatement(Connection conn,  String sql, StatementParameters parameters, DalHints hints) throws Exception {
		return stmtCreator.createCallableStatement(conn, sql, parameters, hints);
	}
	
	private CallableStatement createCallableStatement(Connection conn,  String sql, StatementParameters[] parametersList, DalHints hints) throws Exception {
		return stmtCreator.createCallableStatement(conn, sql, parametersList, hints);
	}
	
	private int startTransaction(DalHints hints, DalEventEnum operation) throws SQLException {
		return transManager.startTransaction(hints, operation);
	}
	
	private void populate(LogEntry entry) throws SQLException {
		transManager.getCurrentConnection().getMeta().populate(entry);
	}

	private void endTransaction(int startLevel) throws SQLException {
		transManager.endTransaction(startLevel);
	}

	private void cleanup(DalHints hints, ConnectionAction<?> action) {
		Statement statement = action.statement != null? 
				action.statement : action.preparedStatement != null?
						action.preparedStatement : action.callableStatement;

		transManager.cleanup(hints, action.rs, statement, action.conn);
		
		action.rs = null;
		action.statement = null;
		action.preparedStatement = null;
		action.callableStatement = null;
		action.connHolder = null;
		action.conn = null;
	}
	
	private void handleException(Throwable e) throws SQLException {
		if(e != null)
			throw transManager.handleException(e);
	}

	private void handleException(Throwable e, int startLevel) throws SQLException {
		if(e != null)
			throw transManager.handleException(e, startLevel);
	}
}
