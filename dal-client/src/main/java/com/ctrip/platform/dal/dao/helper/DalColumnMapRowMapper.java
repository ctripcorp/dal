package com.ctrip.platform.dal.dao.helper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalRowMapper;

/**
 * IMPORTANT NOTE:
 * This class is stateful and not thread safe, it will cache the first result set's mata data to speed up processing.
 * If you want to use it in several result set but different columns, you must re-create new instance for each of the result set. 
 * @author jhhe
 *
 */
public class DalColumnMapRowMapper implements DalRowMapper<Map<String, Object>> {
	private String[] columns;
	
	private void initColumns(ResultSet rs) throws SQLException {
		if(columns != null)
			return;
		ResultSetMetaData rsmd = rs.getMetaData();
		
		columns = new String[rsmd.getColumnCount()];
		for(int i = 0; i < columns.length; i++) {
			columns[i] = rsmd.getColumnName(i + 1);
		}
	}

	public Map<String, Object> map(ResultSet rs, int rowNum) throws SQLException {
		initColumns(rs);
		Map<String, Object> mapOfColValues = new LinkedHashMap<String, Object>(columns.length);
		for (int i = 0; i < columns.length; i++) {
			Object obj = rs.getObject(i + 1);
			mapOfColValues.put(columns[i], obj);
		}
		return mapOfColValues;
	}
}