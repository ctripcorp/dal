package com.ctrip.platform.dal.dao.helper;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalRowMapper;

public interface SupportPartialResultMapping<T> {
	DalRowMapper<T> mapWith(String[] selectedColumns) throws SQLException;
}
