package com.ctrip.platform.dal.dao.helper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalRowMapper;

public class DalObjectRowMapper<T> implements DalRowMapper<T> {

	@SuppressWarnings("unchecked")
	public T map(ResultSet rs, int rowNum) throws SQLException {
		return (T)rs.getObject(1);
	}
}