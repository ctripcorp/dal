package com.ctrip.platform.dal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DalResultSetExtractor<T> {
	T extract(ResultSet rs) throws SQLException;
}
