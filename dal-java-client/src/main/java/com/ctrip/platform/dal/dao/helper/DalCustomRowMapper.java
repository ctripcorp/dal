package com.ctrip.platform.dal.dao.helper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalRowMapper;

public class DalCustomRowMapper implements DalRowMapper<Map<String, Object>> {
	
	private String[] columns;
	
	public DalCustomRowMapper(String... columns){
		this.setColumns(columns);
	}
	
	public DalCustomRowMapper(List<String> columns){
		this.setColumns(columns);
	}
	
	public void setColumns(String... columns){
		this.columns = columns;
	}
	
	public void setColumns(List<String> columns){
		this.columns = columns.toArray(new String[columns.size()]);
	}
	
	@Override
	public Map<String, Object> map(ResultSet rs, int rowNum)
			throws SQLException {
		Map<String, Object> mapOfColValues = new LinkedHashMap<String, Object>(columns.length);
		for (String column : this.columns) {
			Object obj = rs.getObject(column);
			mapOfColValues.put(column, obj);
		}
		return mapOfColValues;
	}
}
