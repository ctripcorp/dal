package com.ctrip.platform.dal.ext.parser;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DalRowMapper<T> {
	/**
	 * Convert current row in result set to application pojo 
	 * @param rs
	 * @return
	 */
	T map(ResultSet rs, int rowNum) throws SQLException;
}
