package com.ctrip.platform.dal.dao.helper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalRowMapper;

public class DalColumnMapRowMapper implements DalRowMapper<Map<String, Object>> {
	private String[] columns;
	public DalColumnMapRowMapper(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		
		columns = new String[rsmd.getColumnCount()];
		for(int i = 0; i < columns.length; i++)
			columns[i] = rsmd.getColumnName(i + 1);
	}

	public Map<String, Object> map(ResultSet rs, int rowNum) throws SQLException {
		Map<String, Object> mapOfColValues = new LinkedHashMap<String, Object>(columns.length);
		for (int i = 0; i < columns.length; i++) {
			Object obj = rs.getObject(i + 1);
			mapOfColValues.put(columns[i], obj);
		}
		return mapOfColValues;
	}
}