package com.ctrip.platform.dal.dao.helper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;

public interface HintsAwareMapper<T> {
	DalRowMapper<T> mapWith(ResultSet rs, DalHints hints) throws SQLException;
}
