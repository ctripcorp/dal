package com.ctrip.platform.dal.dao.helper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalRowMapper;

public class ShortRowMapper implements DalRowMapper<Short> {

	@Override
	public Short map(ResultSet rs, int rowNum) throws SQLException {
		return rs.getShort(1);
	}
}
