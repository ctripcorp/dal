package com.ctrip.platform.dal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.dao.helper.DalObjectRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRowCallbackExtractor;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;

public final class DalQueryDao {
	private static final String NO_RESULT_MSG = "There is no result found!";
	private DalClient client;

	public DalQueryDao(String logicDbName) {
		this.client = DalClientFactory.getClient(logicDbName);
	}

	public <T> List<T> query(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper) 
			throws SQLException {
		return client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper));
	}

	public void query(String sql, StatementParameters parameters, DalHints hints, DalRowCallback callback) 
			throws SQLException {
		client.query(sql, parameters, hints, new DalRowCallbackExtractor(callback));
	}

	/**
	 * Query for the only object in the result. It is expected that there is only one result should be found.
	 * If there is no result or more than 1 result found, it will throws exception to indicate the exceptional case.
	 * If you want to get the first object, please use queryFirst instead.  
	 * @throws SQLException If there is no result or more than 1 result found.
	 */
	public <T> T queryForObject(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper) 
			throws SQLException {
		List<T> result = client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper));
		assertEquals(1, result.size(), NO_RESULT_MSG);
		return result.get(0);
	}

	/**
	 * Query for the only object in the result. It is expected that there is only one result should be found.
	 * If there is no result or more than 1 result found, it will throws exception to indicate the exceptional case.
	 * If you want to get the first object, please use queryFirst instead.  
	 * ExecuteScalar
	 * @throws SQLException If there is no result or more than 1 result found.
	 */
	public <T> T queryForObject(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz) 
			throws SQLException {
		List<T> result = client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(new DalObjectRowMapper<T>()));
		assertEquals(1, result.size(), NO_RESULT_MSG);
		return result.get(0);
	}
	
	/**
	 * Query for the first object in the result. It is expected that there is at least one result should be found.
	 * If there is no result found, it will throws exception to indicate the exceptional case.
	 * If you want to get the only one result, please use queryObject instead.  
	 * @throws SQLException If there is no result found.
	 */
	public <T> T queryFirst(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper) 
			throws SQLException {
		List<T> result = client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper, 1));
		assertGreatThan(0, result.size(), NO_RESULT_MSG);
		return result.get(0);
	}

	public <T> List<T> queryTop(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, int count) 
			throws SQLException {
		return client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper, count));
	}

	public <T> List<T> queryFrom(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, int start, int count) throws SQLException {
		hints.set(DalHintEnum.resultSetType, ResultSet.TYPE_SCROLL_INSENSITIVE);
		return client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper, start, count));
	}

	private void assertEquals(int expected, int actual, String message) throws SQLException{
		if(expected != actual)
			throw new SQLException(message);
	}

	private void assertGreatThan(int expected, int actual, String message) throws SQLException{
		if(actual > expected)
			return;
		
		throw new SQLException(message);
	}	
}