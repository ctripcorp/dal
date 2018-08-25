package com.ctrip.platform.dal.dao.helper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;

/**
 * IMPORTANT NOTE:
 * 
 * This class is stateful and please be careful in parallel execution environment, 
 * it will cache the first result set's mata data to speed up processing.
 * 
 * If you want to use it in several result set but different columns, you must re-create new instance for each of the result set.
 * 
 * Or use with extractors that support HintsAwareExtractor, e.g. DalRowMapperExtractor
 *  
 * @author jhhe
 *
 */
public class DalColumnMapRowMapper implements DalRowMapper<Map<String, Object>>, CustomizableMapper<Map<String, Object>> {
	private volatile String[] columns;
	
	private synchronized void initColumns(ResultSet rs) throws SQLException {
		if(columns != null)
			return;
		ResultSetMetaData rsmd = rs.getMetaData();
		
		String[] temColumns = new String[rsmd.getColumnCount()];
		for(int i = 0; i < temColumns.length; i++) {
		    temColumns[i] = rsmd.getColumnLabel(i + 1);
		}
		
		columns = temColumns;
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

    @Override
    public DalRowMapper<Map<String, Object>> mapWith(String[] columns) throws SQLException {
        DalColumnMapRowMapper mapper = new DalColumnMapRowMapper();
        mapper.columns = columns;
        return mapper;
    }

    @Override
    public DalRowMapper<Map<String, Object>> mapWith(ResultSet rs, DalHints hints) throws SQLException {
        return new DalColumnMapRowMapper();
    }
}