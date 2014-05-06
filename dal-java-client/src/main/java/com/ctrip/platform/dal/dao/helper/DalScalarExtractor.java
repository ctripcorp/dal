package com.ctrip.platform.dal.dao.helper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalResultSetExtractor;

public class DalScalarExtractor implements DalResultSetExtractor<Object> {
	@Override
	public Object extract(ResultSet rs) throws SQLException {
		Object result = null;
		while (rs.next()) {
			result = rs.getObject(1);
		}
		return result;
	}
}