package com.ctrip.platform.dal.dao.client;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.common.db.DruidDataSourceWrapper;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalColumnMapRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalStatementCreator;
import com.ctrip.platform.dal.dao.helper.DalTransactionManager;
import com.ctrip.platform.dal.dao.logging.LogEntry;
import com.ctrip.platform.dal.dao.logging.Logger;
import com.ctrip.platform.dal.dao.logging.MetricsLogger;

// TODO minimize duplicate code
public class DalDirectClient implements DalClient {
	private DalStatementCreator stmtCreator = new DalStatementCreator();
	private DalTransactionManager transManager;
	private String logicDbName;

	public DalDirectClient(DruidDataSourceWrapper connPool, String logicDbName) {
		transManager = new DalTransactionManager(logicDbName, connPool);
		this.logicDbName = logicDbName;
	}
	
	@Override
	public <T> T query(String sql, StatementParameters parameters, final DalHints hints, final DalResultSetExtractor<T> extractor)
			throws SQLException {
		ConnectionAction<T> action = new ConnectionAction<T>() {
			@Override
			T execute() throws Exception {
				conn = getConnection(hints, entry);
				preparedStatement = createPreparedStatement(conn, sql, parameters, hints);
				rs = preparedStatement.executeQuery();
				return extractor.extract(rs);
			}
		};
		action.populate(OperationEnum.QUERY, sql, parameters);
		
		return doInConnection(action, hints);
	}

	@Override
	public int update(String sql, StatementParameters parameters, final DalHints hints)
			throws SQLException {
		ConnectionAction<Integer> action = new ConnectionAction<Integer>() {
			@Override
			Integer execute() throws Exception {
				conn = getConnection(hints, entry);
				
				preparedStatement = createPreparedStatement(conn, sql, parameters, hints);
				
				return preparedStatement.executeUpdate();
			}
		};
		action.populate(OperationEnum.UPDATE_SIMPLE, sql, parameters);
		
		return doInConnection(action, hints);
	}

	@Override
	public int update(String sql, StatementParameters parameters,
			final DalHints hints, final KeyHolder generatedKeyHolder) throws SQLException {
		ConnectionAction<Integer> action = new ConnectionAction<Integer>() {
			@Override
			Integer execute() throws Exception {
				conn = getConnection(hints, entry);
				
				preparedStatement = createPreparedStatement(conn, sql, parameters, hints, generatedKeyHolder);
				int rows = preparedStatement.executeUpdate();
				
				List<Map<String, Object>> generatedKeys = generatedKeyHolder.getKeyList();
				rs = preparedStatement.getGeneratedKeys();
				if (rs == null)
					return rows;
				
				DalRowMapperExtractor<Map<String, Object>> rse =
						new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper(), 1);
				generatedKeys.addAll(rse.extract(rs));
				return rows;
			}
		};
		action.populate(OperationEnum.UPDATE_KH, sql, parameters);
		
		return doInConnection(action, hints);
//		Connection conn = null;
//		PreparedStatement statement = null;
//		ResultSet keys = null;
//		
//		try {
//			conn = getConnection(hints, UPDATE);
//			
//			statement = createPreparedStatement(conn, sql, parameters, hints, generatedKeyHolder);
//			int rows = statement.executeUpdate();
//			
//			List<Map<String, Object>> generatedKeys = generatedKeyHolder.getKeyList();
//			keys = statement.getGeneratedKeys();
//			if (keys == null)
//				return rows;
//			
//			DalRowMapperExtractor<Map<String, Object>> rse =
//					new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper(), 1);
//			generatedKeys.addAll(rse.extract(keys));
//
////			if (logger.isDebugEnabled()) {
////				logger.debug("SQL update affected " + rows + " rows and returned " + generatedKeys.size() + " keys");
////			}
//			return rows;
//		} catch (Throwable e) {
//			throw(handleException(e));
//		} finally {
//			cleanup(keys, statement, conn);
//			statement = null;
//			conn = null;
//		}
	}

	// TODO wrap into command
	@Override
	public int[] batchUpdate(String[] sqls, final DalHints hints) throws SQLException {
		ConnectionAction<int[]> action = new ConnectionAction<int[]>() {
			@Override
			int[] execute() throws Exception {
				conn = getConnection(hints, entry);
				statement = createStatement(conn, hints);
				for(String sql: sqls)
					statement.addBatch(sql);
				
				return statement.executeBatch();
			}
		};
		action.populate(sqls);
		
		return doInTransaction(action, hints);
//		Connection conn = null;
//		Statement statement = null;
//		int level = 0;
//		try {
//			level = startTransaction(hints);
//			
//			conn = getConnection(hints, UPDATE);
//			statement = createStatement(conn, hints);
//			for(String sql: sqls)
//				statement.addBatch(sql);
//			
//			int[] rows = statement.executeBatch();
//			endTransaction(level);
//			
//			return rows;
//		} catch (Throwable e) {
//			throw(handleException(e, level));
//		} finally {
//			cleanup(statement, conn);
//			statement = null;
//			conn = null;
//		}
	}

	@Override
	public int[] batchUpdate(String sql, StatementParameters[] parametersList,
			final DalHints hints) throws SQLException {
		ConnectionAction<int[]> action = new ConnectionAction<int[]>() {
			@Override
			int[] execute() throws Exception {
				conn = getConnection(hints, entry);
				
				statement = createPreparedStatement(conn, sql, parametersList, hints);
				
				return statement.executeBatch();
			}
		};
		action.populate(sql, parametersList);
		
		return doInTransaction(action, hints);
//		Connection conn = null;
//		PreparedStatement statement = null;
//		int level = 0;
//		try {
//			level = startTransaction(hints);
//			
//			conn = getConnection(hints, UPDATE);
//			
//			statement = createPreparedStatement(conn, sql, parametersList, hints);
//			
//			int[] rows = statement.executeBatch();
//			endTransaction(level);
//			
//			return rows;
//		} catch (Throwable e) {
//			throw(handleException(e, level));
//		} finally {
//			cleanup(statement, conn);
//			statement = null;
//			conn = null;
//		}
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
//		int level = 0;
//		try {
//			level = startTransaction(hints);
//			
//			for(DalCommand cmd: commands) {
//				if(!cmd.execute(this))
//					break;
//			}
//			
//			endTransaction(level);
//		} catch (Throwable e) {
//			throw handleException(e, level);
//		}
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

				conn = getConnection(hints, entry);
				
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
	
	private <T> T doInConnection(ConnectionAction<T> action, DalHints hints)
			throws SQLException {
		LogEntry entry = null;
		populateHints(hints,action);
		long start = start();
		try {
			//conn.getSchema()
			entry = Logger.create(action.getSqlLog(), action.parameters, logicDbName, "TBI", action.operation.getEventId(), action.operation.getOperation());
			action.entry = entry;

			T result = action.execute();
			
			Logger.log(entry);
			MetricsLogger.success(entry, start);
			return result;
		} catch (Throwable e) {
			Logger.log(entry, e);
			MetricsLogger.fail(entry, start);
			throw(handleException(e));
		} finally {
			cleanup(action);
		}
	}
	
	private <T> T doInTransaction(ConnectionAction<T> action, DalHints hints)
			throws SQLException {
		LogEntry entry = null;
		int level = 0;
		populateHints(hints,action);
		long start = start();
		try {
			//conn.getSchema()
			level = startTransaction(hints);
			entry = Logger.create(action.getSqlLog(), action.parameters, logicDbName, "TBI", action.operation.getEventId(), action.operation.getOperation());
			action.entry = entry;
			
			T result = action.execute();
			
			endTransaction(level);
			Logger.log(entry);
			MetricsLogger.success(entry, start);
			return result;
		} catch (Throwable e) {
			Logger.log(entry, e);
			MetricsLogger.fail(entry, start);
			throw handleException(e, level);
		} finally {
			cleanup(action);
		}
	}
	
	private long start() {
		return System.currentTimeMillis();
	}
	
	private abstract class ConnectionAction<T> {
		OperationEnum operation;
		String sql;
		String callString;
		String[] sqls;
		StatementParameters parameters;
		StatementParameters[] parametersList;
		List<DalCommand> commands;
		LogEntry entry;
		Connection conn;
		Statement statement;
		PreparedStatement preparedStatement;
		CallableStatement callableStatement;
		ResultSet rs;
		T result;
		
		void populate(OperationEnum operation, String sql, StatementParameters parameters) {
			this.operation = operation;
			this.sql = sql;
			this.parameters = parameters;
		}

		void populate(String[] sqls) {
			this.operation = OperationEnum.BATCH_UPDATE;
			this.sqls = sqls;
		}
		
		void populate(String sql, StatementParameters[] parametersList) {
			this.operation = OperationEnum.BATCH_UPDATE_PARAM;
			this.sql = sql;
			this.parametersList = parametersList;
		}
		
		void populate(List<DalCommand> commands) {
			this.operation = OperationEnum.EXECUTE;
			this.commands = commands;
		}
		
		void populateSp(String callString, StatementParameters parameters) {
			this.operation = OperationEnum.CALL;
			this.callString = callString;
			this.parameters = parameters;
		}
		
		/**
		 * TODO: How to display batch SQLs
		 * @return
		 */
		String getSqlLog() {
			if(null != sql)
				return sql;
			else if(null != callString)
				return callString;
			else if(sqls != null)
				return StringUtils.join(sqls, ";");
			else
				return "";
		}
		
		abstract T execute() throws Exception;
	}
	
	private void populateHints(DalHints hints, ConnectionAction<?> action) {
		hints.set(DalHintEnum.operation, action.operation);
		hints.set(DalHintEnum.sql, action.sql);
		hints.set(DalHintEnum.callString, action.callString);
		hints.set(DalHintEnum.parameters, action.parameters);
		hints.set(DalHintEnum.parametersList, action.parametersList);
		hints.set(DalHintEnum.commands, action.commands);
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
	
	private int startTransaction(DalHints hints) throws SQLException {
		return transManager.startTransaction(hints);
	}

	private void endTransaction(int startLevel) throws SQLException {
		transManager.endTransaction(startLevel);
	}

	private Connection getConnection(DalHints hints, LogEntry entry) throws SQLException {
		// Apply sharding strategy here
		return transManager.getConnection(hints);
	}
	
	private void cleanup(ConnectionAction<?> action) {
		Statement statement = action.statement != null? 
				action.statement : action.preparedStatement != null?
						action.preparedStatement : action.callableStatement;

		transManager.cleanup(action.rs, statement, action.conn);
		
		action.rs = null;
		action.statement = null;
		action.preparedStatement = null;
		action.callableStatement = null;
		action.conn = null;
	}
	
	private SQLException handleException(Throwable e) {
		return transManager.handleException(e);
	}
	
	private SQLException handleException(Throwable e, int startLevel) {
		return transManager.handleException(e, startLevel);
	}
}
