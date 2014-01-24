package com.ctrip.platform.dal.dao;

import java.sql.ResultSet;

public interface ResultSetVisitor {
	/**
	 * Convert current row in result set to application pojo 
	 * @param rs
	 * @return
	 */
	DaoPojo visitRow(ResultSet rs);
}
