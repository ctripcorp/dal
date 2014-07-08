package com.ctrip.platform.dal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.dao.helper.DalObjectRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRowCallbackExtractor;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.sql.logging.Logger;

/**
 * DAO class that provides common query based functions.
 *  
 * @author jhhe
 *
 */
public final class DalQueryDao {
	private static final String COUNT_MISMATCH_MSG = "It is expected to return only %d result. But the actually count is %d.";
	private static final String NO_RESULT_MSG = "There is no result found!";
	private DalClient client;

	public DalQueryDao(String logicDbName) {
		this.client = DalClientFactory.getClient(logicDbName);
	}

	/**
	 * Execute query by the given sql with parameters. The result will be wrapped into type defined by the given mapper.
	 * 
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param mapper Helper that converters each row to entity. 
	 * @return List of entities that represent the query result.
	 * @throws SQLException when things going wrong during the execution
	 */
	public <T> List<T> query(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper) 
			throws SQLException {
		Logger.watcherBegin();
		return client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper));
	}

	/**
	 * Execute query by the given sql with parameters. The result will be the list of instance of the given clazz.
	 * 
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param clazz The return type 
	 * @return List of instance of clazz that represent the query result.
	 * @throws SQLException when things going wrong during the execution
	 */
	public <T> List<T> query(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz) 
			throws SQLException {
		Logger.watcherBegin();
		return client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(new DalObjectRowMapper<T>()));
	}

	/**
	 * Execute query by the given sql with parameters. The result will be processed by the given callback.
	 * 
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param callback Helper that process each row.
	 * @throws SQLException when things going wrong during the execution
	 */
	public void query(String sql, StatementParameters parameters, DalHints hints, DalRowCallback callback) 
			throws SQLException {
		Logger.watcherBegin();
		client.query(sql, parameters, hints, new DalRowCallbackExtractor(callback));
	}

	/**
	 * Query for the only object in the result. It is expected that there is only one result should be found.
	 * If there is no result or more than 1 result found, it will throws exception to indicate the exceptional case.
	 * If you want to get the first object, please use queryFirst instead.  
	 * 
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param mapper Helper that converters each row to entity. 
	 * @return entity that represent the query result.
	 * @throws SQLException If there is no result or more than 1 result found.
	 */
	public <T> T queryForObject(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper) 
			throws SQLException {
		Logger.watcherBegin();
		List<T> result = client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper));
		assertEquals(1, result.size());
		return result.get(0);
	}

	/**
	 * Query for the only object in the result. It is expected that there is only one result should be found.
	 * If there is no result or more than 1 result found, it will throws exception to indicate the exceptional case.
	 * If you want to get the first object, please use queryFirst instead.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param clazz The return type  
	 * @return instance of clazz that represent the query result.
	 * @throws SQLException If there is no result or more than 1 result found.
	 */
	public <T> T queryForObject(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz) 
			throws SQLException {
		Logger.watcherBegin();
		List<T> result = client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(new DalObjectRowMapper<T>()));
		assertEquals(1, result.size());
		return result.get(0);
	}

	/**
	 * Query for the first object in the result. It is expected that there is at least one result should be found.
	 * If there is no result found, it will throws exception to indicate the exceptional case.
	 * If you want to get the only one result, please use queryObject instead.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param mapper Helper that converters each row to entity. 
	 * @return entity that represent the query result.
	 * @throws SQLException If there is no result found.
	 */
	public <T> T queryFirst(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper) 
			throws SQLException {
		Logger.watcherBegin();
		List<T> result = client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper, 1));
		assertGreatThan(0, result.size(), NO_RESULT_MSG);
		return result.get(0);
	}

	/**
	 * Query for the first object in the result. It is expected that there is at least one result should be found.
	 * If there is no result found, it will throws exception to indicate the exceptional case.
	 * If you want to get the only one result, please use queryObject instead.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param clazz The return type 
	 * @return instance of clazz that represent the query result.
	 * @throws SQLException If there is no result found.
	 */
	public <T> T queryFirst(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz) 
			throws SQLException {
		Logger.watcherBegin();
		List<T> result = client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(new DalObjectRowMapper<T>(), 1));
		assertGreatThan(0, result.size(), NO_RESULT_MSG);
		return result.get(0);
	}

	/**
	 * Query the first count of object in the result. If the query return more result than 
	 * count. It will return top count of result. If there is not enough result, it will 
	 * return all the results.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param mapper Helper that converters each row to entity.
	 * @param count number of result 
	 * @return list of entity that represent the query result.
	 * @throws SQLException If there is no result found.
	 */
	public <T> List<T> queryTop(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, int count) 
			throws SQLException {
		Logger.watcherBegin();
		return client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper, count));
	}

	/**
	 * Execute query and return partial result against the given start and count.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param mapper Helper that converters each row to entity.
	 * @param start the row number to be started counting
	 * @param count number of result 
	 * @return list of entity that represent the query result.
	 * @throws SQLException If there is no result found.
	 */
	public <T> List<T> queryFrom(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, int start, int count) throws SQLException {
		Logger.watcherBegin();
		hints.set(DalHintEnum.resultSetType, ResultSet.TYPE_SCROLL_INSENSITIVE);
		return client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper, start, count));
	}

	private void assertEquals(int expected, int actual) throws SQLException{
		if(expected != actual)
			throw new SQLException(String.format(COUNT_MISMATCH_MSG, expected, actual));
	}

	private void assertGreatThan(int expected, int actual, String message) throws SQLException{
		if(actual > expected)
			return;
		
		throw new SQLException(message);
	}	
}