package com.ctrip.platform.dal.dao.helper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.DalRowCallback;

public class DalRowCallbackExtractor implements DalResultSetExtractor<List<Object>> {
	private DalRowCallback callback;
	
	public DalRowCallbackExtractor(DalRowCallback callback) {
		this.callback = callback;
	}
	
	@Override
	public List<Object> extract(ResultSet rs) throws SQLException {
		while (rs.next()) {
			callback.process(rs);
		}
		return null;
	}
}
