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

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.helper.DalColumnMapRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;

/**
 * The direct connection implementation for DalClient.
 * @author jhhe
 */
public class DalDirectClient implements DalClient {
	private DalStatementCreator stmtCreator = new DalStatementCreator();
	private DalConnectionManager connManager;
	private DalTransactionManager transManager;

	public DalDirectClient(DalConfigure config, String logicDbName) {
		connManager = new DalConnectionManager(logicDbName, config);
		transManager = new DalTransactionManager(connManager);
	}

	@Override
	public <T> T query(String sql, StatementParameters parameters, final DalHints hints, final DalResultSetExtractor<T> extractor)
			throws SQLException {
		ConnectionAction<T> action = new ConnectionAction<T>() {
			@Override
			public T execute() throws Exception {
				conn = getConnection(hints, this);
				
				preparedStatement = createPreparedStatement(conn, sql, parameters, hints);
				DalWatcher.beginExecute();
				rs = preparedStatement.executeQuery();
				DalWatcher.endExectue();
				
				return extractor.extract(rs);
			}
		};
		action.populate(DalEventEnum.QUERY, sql, parameters);
		
		return doInConnection(action, hints);
	}

	@Override
	public List<?> query(String sql, StatementParameters parameters, final DalHints hints, final List<DalResultSetExtractor<?>> extractors) 
			throws SQLException {
		ConnectionAction<List<?>> action = new ConnectionAction<List<?>>() {
			@Override
			public List<?> execute() throws Exception {
				conn = getConnection(hints, this);
				preparedStatement = createPreparedStatement(conn, sql, parameters, hints);
				List<Object> result = new ArrayList<>();
				DalWatcher.beginExecute();

				preparedStatement.execute();
				for(DalResultSetExtractor<?> extractor: extractors) {
		            ResultSet resultSet = preparedStatement.getResultSet();
	            	result.add((Object)extractor.extract(resultSet));
	                preparedStatement.getMoreResults();
				}

				DalWatcher.endExectue();
				
				return result;
			}
		};
		action.populate(DalEventEnum.QUERY, sql, parameters);
		
		return doInConnection(action, hints);
	}
	
	@Override
	public int update(String sql, StatementParameters parameters, final DalHints hints)
			throws SQLException {
		final KeyHolder generatedKeyHolder = hints.getKeyHolder();
		ConnectionAction<Integer> action = new ConnectionAction<Integer>() {
			@Override
			public Integer execute() throws Exception {
				conn = getConnection(hints, this);
				// For old generated free update, the parameters is nit compiled before invoke direct client
				parameters.compile();
				if(generatedKeyHolder == null)
					preparedStatement = createPreparedStatement(conn, sql, parameters, hints);
				else
					preparedStatement = createPreparedStatement(conn, sql, parameters, hints, generatedKeyHolder);
				
				DalWatcher.beginExecute();
				int rows = preparedStatement.executeUpdate();
				DalWatcher.endExectue();
				
				if(generatedKeyHolder == null)
					return rows;
				
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
		action.populate(generatedKeyHolder == null ?DalEventEnum.UPDATE_SIMPLE:DalEventEnum.UPDATE_KH, sql, parameters);
		
		return doInConnection(action, hints);
	}

	@Override
	public int[] batchUpdate(String[] sqls, final DalHints hints) throws SQLException {
		ConnectionAction<int[]> action = new ConnectionAction<int[]>() {
			@Override
			public int[] execute() throws Exception {
				conn = getConnection(hints, this);
				
				statement = createStatement(conn, hints);
				for(String sql: sqls)
					statement.addBatch(sql);
				
				DalWatcher.beginExecute();
				int[] ret = statement.executeBatch();
				DalWatcher.endExectue();
				
				return ret;
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
			public int[] execute() throws Exception {
				conn = getConnection(hints, this);
				
				statement = createPreparedStatement(conn, sql, parametersList, hints);
				
				DalWatcher.beginExecute();
				int[] ret =  statement.executeBatch();
				DalWatcher.endExectue();
				
				return ret;
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
			public Object execute() throws Exception {
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
			public Object execute() throws Exception {
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
			public Map<String, ?> execute() throws Exception {
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
                
				conn = getConnection(hints, this);
				
				callableStatement = createCallableStatement(conn, callString, parameters, hints);
				
				DalWatcher.beginExecute();
				boolean retVal = callableStatement.execute();
				int updateCount = callableStatement.getUpdateCount();
				
				DalWatcher.endExectue();
				
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
			public int[] execute() throws Exception {
				conn = getConnection(hints, this);
				
				callableStatement = createCallableStatement(conn, callString, parametersList, hints);

				DalWatcher.beginExecute();
				int[] ret =  callableStatement.executeBatch();
				DalWatcher.endExectue();
				
				return ret;
			}
		};
		action.populateSp(callString, parametersList);
		
		return executeBatch(action, hints);
	}
	
	private Map<String, Object> extractReturnedResults(CallableStatement statement, List<StatementParameter> resultParameters, int updateCount, DalHints hints) throws SQLException {
		Map<String, Object> returnedResults = new LinkedHashMap<String, Object>();
		if(hints.is(DalHintEnum.skipResultsProcessing) || resultParameters.size() == 0)
			return returnedResults;

		boolean moreResults;
		int index = 0;
		do {
			// If resultParameters is not the same as what exactly returned, there will be exception. You just
			// need to add enough result parameter to avoid this or you can set skipResultsProcessing
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
		return connManager.doInConnection(action, hints);
	}
	
	private <T> T doInTransaction(ConnectionAction<T> action, DalHints hints)
			throws SQLException {
			return transManager.doInTransaction(action, hints);
	}
	
	public Connection getConnection(DalHints hints, ConnectionAction<?> action) throws SQLException {
		DalWatcher.beginConnect();

		action.connHolder = transManager.getConnection(hints, action.operation);
		Connection conn = action.connHolder.getConn();

		DalWatcher.endConnect();
		return conn;
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
}