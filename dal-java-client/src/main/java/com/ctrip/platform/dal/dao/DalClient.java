package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * The lowest level of API for accessing database for the whole DAL java client framework.
 * All the DB request will finally go throw here. This API will be shared for both direct and 
 * in-direct(DAS mode) connection to DB.
 * @author jhhe
 */
public interface DalClient {
	/**
	 * Query against the given sql and parameters. The result set will be
	 * processed by the given extractor
	 * @param sql
	 * @param parameters
	 * @param hints
	 * @param extractor
	 * @return the extracted result from the result set
	 * @throws SQLException
	 */
	<T> T query(String sql, StatementParameters parameters, DalHints hints,
			DalResultSetExtractor<T> extractor) throws SQLException;

	/**
	 * Update against the given sql and parameters. 
	 * @param sql
	 * @param parameters
	 * @param hints
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	int update(String sql, StatementParameters parameters, DalHints hints)
			throws SQLException;

	/**
	 * Update against the given sql and parameters. The generated keys will be 
	 * returned in generatedKeyHolder. This is most used for insert into table 
	 * which has auto incremental primary key.
	 * @param sql
	 * @param parameters
	 * @param hints
	 * @param generatedKeyHolder
	 * @return
	 * @throws SQLException
	 */
	int update(String sql, StatementParameters parameters, DalHints hints,
			KeyHolder generatedKeyHolder) throws SQLException;

	/**
	 * Batch update for given sqls.The default behavior is execute in transaction.
	 * You can overwrite this by set forceAutoCommit in hints.
	 * @param sqls
	 * @param hints 
	 * 			when hints set forceAutoCommit the connection auto commit will be true.
	 * @return how many rows been affected for each of the sql
	 * @throws SQLException
	 */
	int[] batchUpdate(String[] sqls, DalHints hints) throws SQLException;

	/**
	 * Batch update for the given sql with all the given parameters in parametersList.
	 * The default behavior is execute in transaction.
	 * You can overwrite this by set forceAutoCommit in hints.
	 * @param sql
	 * @param parametersList
	 * @param hints
	 * return how many rows been affected for each of parameters
	 * @throws SQLException
	 */
	int[] batchUpdate(String sql, StatementParameters[] parametersList,
			DalHints hints) throws SQLException;

	/**
	 * Execute list of commands in the same transaction.
	 * @param commands
	 * @param hints
	 * @throws SQLException
	 */
	void execute(List<DalCommand> commands, DalHints hints) throws SQLException;

	/**
	 * Call stored procedure.
	 * @param callString
	 * @param parameters
	 * @param hints
	 * @return the returned update count and result set in order
	 * @throws SQLException
	 */
	Map<String, ?> call(String callString, StatementParameters parameters,
			DalHints hints) throws SQLException;

	/**
	 * Call stored procedure.
	 * The default behavior is execute in transaction.
	 * You can overwrite this by set forceAutoCommit in hints.
	 * @param callString
	 * @param parametersList
	 * @param hints
	 * return how many rows been affected for each of parameters
	 * @throws SQLException
	 */
	int[] batchCall(String callString, StatementParameters[] parametersList,
			DalHints hints) throws SQLException;
}
