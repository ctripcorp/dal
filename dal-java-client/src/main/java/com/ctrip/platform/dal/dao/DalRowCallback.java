package com.ctrip.platform.dal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DalRowCallback {
	void process(ResultSet rs) throws SQLException;
}
