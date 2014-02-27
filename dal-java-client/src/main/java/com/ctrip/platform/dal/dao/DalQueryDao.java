package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.dao.helper.DalRowCallbackExtractor;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;

public final class DalQueryDao {
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

	public <T> T queryForObject(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper) 
			throws SQLException {
		List<T> result = client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper));
		assertCount(1, result.size());
		return result.get(0);
	}

	/**
	 *  executeScalar
	 * @throws SQLException
	 */
	public <T> T queryForObject(String sql, StatementParameters parameters, DalHints hints, Class<T> clazz) 
			throws SQLException {
		// TODO support
		return null;
	}
	
	private void assertCount(int expected, int actual) throws SQLException{
		if(expected != actual)
			throw new SQLException(String.format("The expected count %d does not equal to actual count %d", expected, actual));
	}

	public <T> T queryFisrt(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper) 
			throws SQLException {
		return client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper, 1)).get(0);
	}

	public <T> List<T> queryTop(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, int count) 
			throws SQLException {
		return client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper, count));
	}

	public <T> List<T> queryFrom(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> mapper, int start, int count) throws SQLException {
		return client.query(sql, parameters, hints, new DalRowMapperExtractor<T>(mapper, start, count));
	}
}