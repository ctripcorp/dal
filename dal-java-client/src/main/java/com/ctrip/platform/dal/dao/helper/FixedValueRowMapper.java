package com.ctrip.platform.dal.dao.helper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalRowMapper;

/**
 * Always return same value for each row. Useful for test
 * @author jhhe
 *
 * @param <T>
 */
public class FixedValueRowMapper<T> implements DalRowMapper<T> {
	private T value;
	
	/**
	 * Value will be null for each row
	 */
	public FixedValueRowMapper() {
	}
	
	public FixedValueRowMapper(T value) {
		this.value = value;
	}

	@Override
	public T map(ResultSet rs, int rowNum) throws SQLException {
		return value;
	}
}
