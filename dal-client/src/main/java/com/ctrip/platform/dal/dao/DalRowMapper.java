package com.ctrip.platform.dal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This interface MUST consider potential multiple-thread concurrent access. 
 * @author jhhe
 *
 */
public interface DalRowMapper<T> {
	/**
	 * Convert current row in result set to application pojo 
	 * @param rs
	 * @return
	 */
	T map(ResultSet rs, int rowNum) throws SQLException;
}
