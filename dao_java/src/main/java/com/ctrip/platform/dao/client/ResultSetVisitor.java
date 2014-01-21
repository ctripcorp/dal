package com.ctrip.platform.dao.client;

import java.sql.ResultSet;

public interface ResultSetVisitor {
	/**
	 * Convert current row in result set to application pojo 
	 * @param rs
	 * @return
	 */
	DaoPojo visit(ResultSet rs);
}
