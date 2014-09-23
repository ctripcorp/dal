package com.ctrip.platform.dal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.dao.helper.DalObjectRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRowCallbackExtractor;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.sql.exceptions.DalException;
import com.ctrip.platform.dal.sql.exceptions.ErrorCode;

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
		List<T> result = client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper));
		return requireSingle(result);
	}

	/**
	 * Query for the only object in the result. It is expected that there is only one result should be found.
	 * If there is no result, it will return null. If there is more than 1 result found, it will throws exception to indicate the exceptional case.
	 * If you want to get the first object, please use queryFirst instead.  
	 * 
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param mapper Helper that converters each row to entity. 
	 * @return entity that represent the query result. Or null if no result found.
	 * @throws SQLException If there is than 1 result found.
	 */
	public <T> T queryForObjectNullable(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper) 
			throws SQLException {
		List<T> result = client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper));
		return requireSingleNullable(result);
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
		List<T> result = client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(new DalObjectRowMapper<T>()));
		return requireSingle(result);
	}

	/**
	 * Query for the only object in the result. It is expected that there is only one result should be found.
	 * If there is no result, it will return null. If there is more than 1 result found, it will throws exception to indicate the exceptional case.
	 * If you want to get the first object, please use queryFirst instead.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param clazz The return type  
	 * @return instance of clazz that represent the query result. Or null if no result found.
	 * @throws SQLException If there is more than 1 result found.
	 */
	public <T> T queryForObjectNullable(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz) 
			throws SQLException {
		List<T> result = client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(new DalObjectRowMapper<T>()));
		return requireSingleNullable(result);
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
		List<T> result = client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper, 1));
		return requireFirst(result);
	}

	/**
	 * Query for the first object in the result. It is expected that there is at least one result should be found.
	 * If there is no result found, it will return null.
	 * If you want to get the only one result, please use queryObject instead.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param mapper Helper that converters each row to entity. 
	 * @return entity that represent the query result. Null if no result found.
	 * @throws SQLException when things going wrong during the execution
	 */
	public <T> T queryFirstNullable(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper) 
			throws SQLException {
		List<T> result = client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper, 1));
		return requireFirstNullable(result);
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
		List<T> result = client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(new DalObjectRowMapper<T>(), 1));
		return requireFirst(result);
	}

	/**
	 * Query for the first object in the result. It is expected that there is at least one result should be found.
	 * If there is no result found, it will return null.
	 * If you want to get the only one result, please use queryObject instead.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param clazz The return type 
	 * @return instance of clazz that represent the query result. Null if no result found.
	 * @throws SQLException when things going wrong during the execution
	 */
	public <T> T queryFirstNullable(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz) 
			throws SQLException {
		List<T> result = client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(new DalObjectRowMapper<T>(), 1));
		return requireFirstNullable(result);
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
	 * @throws SQLException when things going wrong during the execution
	 */
	public <T> List<T> queryTop(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, int count) 
			throws SQLException {
		return client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper, count));
	}

	/**
	 * Query the first count of object in the result. If the query return more result than 
	 * count. It will return top count of result. If there is not enough result, it will 
	 * return all the results.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param clazz The return type .
	 * @param count number of result 
	 * @return list of instance of clazz that represent the query result.
	 * @throws SQLException when things going wrong during the execution
	 */
	public <T> List<T> queryTop(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz, int count) 
			throws SQLException {
		return client.query(sql, parameters, hints,  new DalRowMapperExtractor<T>(new DalObjectRowMapper<T>(), count));
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
	 * @throws SQLException when things going wrong during the execution
	 */
	public <T> List<T> queryFrom(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, int start, int count) throws SQLException {
		hints.set(DalHintEnum.resultSetType, ResultSet.TYPE_SCROLL_INSENSITIVE);
		return client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper, start, count));
	}

	/**
	 * Execute query and return partial result against the given start and count.
	 *   
	 * @param sql The sql statement to be executed
	 * @param parameters A container that holds all the necessary parameters
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param clazz The return type .
	 * @param start the row number to be started counting
	 * @param count number of result 
	 * @return list of instance of clazz that represent the query result.
	 * @throws SQLException when things going wrong during the execution
	 */
	public <T> List<T> queryFrom(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz, int start, int count) throws SQLException {
		hints.set(DalHintEnum.resultSetType, ResultSet.TYPE_SCROLL_INSENSITIVE);
		return client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(new DalObjectRowMapper<T>(), start, count));
	}
	
	/**
	 * Get result from given list of T. If there is 0 or more than 1 element found, throws exception. 
	 * Else return the first element.
	 * 
	 * @param result The container that hold 0 or more than 1 element.
	 * @return Null if no result found or first element in the results.
	 * @throws SQLException if there is 0 or more than 1 element in the result
	 */
	private <T> T requireSingle(List<T> result) throws SQLException {
		assertEquals(1, result.size());
		return result.get(0);
	}

	/**
	 * Get result from given list of T. If there is no result found, return null. 
	 * If there is more than 1 element found, throws exception.
	 * Else return the first element.
	 * 
	 * @param result The container that hold 0 or more than 1 element.
	 * @return Null if no result found or first element in the results.
	 * @throws SQLException if there is more than 1 element in the result
	 */
	private <T> T requireSingleNullable(List<T> result) throws SQLException {
		if(result.size() == 0)
			return null;
		
		assertEquals(1, result.size());
		return result.get(0);
	}

	/**
	 * Get result from given list of T. If there is no element found, throws exception. 
	 * Else return the first element.
	 * 
	 * @param result The container that hold 0 or more than 1 element.
	 * @return Null if no result found or first element in the results.
	 * @throws SQLException if there is 0 or more than 1 element in the result
	 */
	private <T> T requireFirst(List<T> result) throws SQLException {
		assertGreatThan(0, result.size(), NO_RESULT_MSG);
		return result.get(0);
	}

	/**
	 * Get result from given list of T. If there is no element found, return null. 
	 * Else return the first element.
	 * 
	 * @param result The container that hold 0 or more than 1 element.
	 * @return Null if no result found or first element in the results.
	 * @throws SQLException if there is more than 1 element in the result
	 */
	private <T> T requireFirstNullable(List<T> result) throws SQLException {
		return result.size() == 0 ? null : result.get(0);
	}

	private void assertEquals(int expected, int actual) throws SQLException{
		if(expected != actual)
			throw new DalException(ErrorCode.AssertEqual, expected, actual);
	}

	private void assertGreatThan(int expected, int actual, String message) throws SQLException{
		if(actual > expected)
			return;
		
		throw new DalException(ErrorCode.AssertGreatThan);
	}
}
