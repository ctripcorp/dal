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

// TODO minimize duplicate code
public class DalDirectClient implements DalClient {
	private static final boolean SELECTE = true;
	private static final boolean UPDATE = false;
	
	private DalStatementCreator stmtCreator = new DalStatementCreator();
	private DalTransactionManager transManager;

	public DalDirectClient(DruidDataSourceWrapper connPool, String logicDbName) {
		transManager = new DalTransactionManager(logicDbName, connPool);
	}
	
	@Override
	public <T> T query(String sql, StatementParameters parameters,
			DalHints hints, DalResultSetExtractor<T> extractor)
			throws SQLException {
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection(hints, SELECTE);
			
			statement = createPreparedStatement(conn, sql, parameters, hints);
			rs = statement.executeQuery();
			
			return extractor.extract(rs);
		} catch (Throwable e) {
			throw(handleException(e));
		} finally {
			cleanup(rs, statement, conn);
			rs = null;
			statement = null;
			conn = null;
		}
	}

	@Override
	public int update(String sql, StatementParameters parameters, DalHints hints)
			throws SQLException {
		Connection conn = null;
		PreparedStatement statement = null;
		
		try {
			conn = getConnection(hints, UPDATE);
			
			statement = createPreparedStatement(conn, sql, parameters, hints);
			
			return statement.executeUpdate();
		} catch (Throwable e) {
			throw(handleException(e));
		} finally {
			cleanup(statement, conn);
			statement = null;
			conn = null;
		}
	}

	@Override
	public int update(String sql, StatementParameters parameters,
			DalHints hints, KeyHolder generatedKeyHolder) throws SQLException {
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet keys = null;
		
		try {
			conn = getConnection(hints, UPDATE);
			
			statement = createPreparedStatement(conn, sql, parameters, hints, generatedKeyHolder);
			int rows = statement.executeUpdate();
			
			List<Map<String, Object>> generatedKeys = generatedKeyHolder.getKeyList();
			generatedKeys.clear();
			keys = statement.getGeneratedKeys();
			if (keys == null)
				return rows;
			
			DalRowMapperExtractor<Map<String, Object>> rse =
					new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper(), 1);
			generatedKeys.addAll(rse.extract(keys));

//			if (logger.isDebugEnabled()) {
//				logger.debug("SQL update affected " + rows + " rows and returned " + generatedKeys.size() + " keys");
//			}
			return rows;
		} catch (Throwable e) {
			throw(handleException(e));
		} finally {
			cleanup(keys, statement, conn);
			statement = null;
			conn = null;
		}
	}

	@Override
	public int[] batchUpdate(String[] sqls, DalHints hints) throws SQLException {
		Connection conn = null;
		Statement statement = null;
		
		try {
			conn = getConnection(hints, UPDATE);
			
			statement = createStatement(conn, hints);
			for(String sql: sqls)
				statement.addBatch(sql);
			
			return statement.executeBatch();
		} catch (Throwable e) {
			throw(handleException(e));
		} finally {
			cleanup(statement, conn);
			statement = null;
			conn = null;
		}
	}

	@Override
	public int[] batchUpdate(String sql, StatementParameters[] parametersList,
			DalHints hints) throws SQLException {
		Connection conn = null;
		PreparedStatement statement = null;
		
		try {
			conn = getConnection(hints, UPDATE);
			
			statement = createPreparedStatement(conn, sql, parametersList, hints);
			
			return statement.executeBatch();
		} catch (Throwable e) {
			throw(handleException(e));
		} finally {
			cleanup(statement, conn);
			statement = null;
			conn = null;
		}
	}

	@Override
	public void execute(List<DalCommand> commands, DalHints hints)
			throws SQLException {
		int level;
		try {
			level = startTransaction(hints);
			
			for(DalCommand cmd: commands)
				cmd.execute(this);
			
			endTransaction(level);
		} catch (Throwable e) {
			throw(handleException(e));
		}
	}

	@Override
	public Map<String, ?> call(String callString,
			StatementParameters parameters, DalHints hints) throws SQLException {
		Connection conn = null;
		CallableStatement statement = null;
		
		try {
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

			conn = getConnection(hints, UPDATE);
			
			statement = createCallableStatement(conn, callString, parameters, hints);
			boolean retVal = statement.execute();
			int updateCount = statement.getUpdateCount();
//			if (logger.isDebugEnabled()) {
//				logger.debug("CallableStatement.execute() returned '" + retVal + "'");
//				logger.debug("CallableStatement.getUpdateCount() returned " + updateCount);
//			}
			Map<String, Object> returnedResults = new LinkedHashMap<String, Object>();
			if (retVal || updateCount != -1) {
				returnedResults.putAll(extractReturnedResults(statement, resultParameters, updateCount, hints));
			}
			returnedResults.putAll(extractOutputParameters(statement, callParameters));
			return returnedResults;
		} catch (Throwable e) {
			throw(handleException(e));
		} finally {
			cleanup(statement, conn);
			statement = null;
			conn = null;
		}
	}
	
	private Map<String, Object> extractReturnedResults(CallableStatement statement, List<StatementParameter> resultParameters, int updateCount, DalHints hints) throws SQLException {

		Map<String, Object> returnedResults = new LinkedHashMap<String, Object>();
		boolean moreResults;
		if(hints != null && hints.contains(DalHintEnum.skipResultsProcessing))
			return null;

//		boolean skipUndeclaredResults = hints != null && hints.contains(DalHintEnum.skipUndeclaredResults);
		
		int index = 0;
		do {
			String key = resultParameters.get(index).getName();
			Object value = updateCount == -1?
				resultParameters.get(index).getResultSetExtractor().extract(statement.getResultSet()) :
				updateCount;
			moreResults = statement.getMoreResults();
			updateCount = statement.getUpdateCount();
//			if (logger.isDebugEnabled()) {
//				logger.debug("CallableStatement.getUpdateCount() returned " + updateCount);
//			}
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
			Object value = statement.getObject(parameter.getIndex());
			if (value instanceof ResultSet) {
				value = parameter.getResultSetExtractor().extract(statement.getResultSet());
			}
			returnedResults.put(parameter.getName(), value);
		}
		return returnedResults;
	}

	private Statement createStatement(Connection conn, DalHints hints) throws Exception {
		return stmtCreator.createStatement(conn, hints);
	}

	private PreparedStatement createPreparedStatement(Connection conn, String sql, StatementParameters parameters, DalHints hints) throws Exception {
		return stmtCreator.createPreparedStatement(conn, sql, parameters, hints);
	}
	
	public PreparedStatement createPreparedStatement(Connection conn, String sql, StatementParameters parameters, DalHints hints, KeyHolder keyHolder) throws Exception {
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

		private Connection getConnection(DalHints hints, boolean isSelect) throws SQLException {
		return transManager.getConnection(hints, isSelect);
	}

	private void cleanup(Statement statement, Connection conn) {
		transManager.cleanup(statement, conn);
	}
	
	private void cleanup(ResultSet rs, Statement statement, Connection conn) {
		transManager.cleanup(rs, statement, conn);
	}
	
	private SQLException handleException(Throwable e) {
		return transManager.handleException(e);
	}
	
	// TODO Should be used to wrap all try/catch statement in the public methods 
	private class JdbcOperation {
		
	}
}
