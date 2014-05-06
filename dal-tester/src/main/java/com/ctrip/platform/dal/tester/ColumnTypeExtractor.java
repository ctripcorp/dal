package com.ctrip.platform.dal.tester;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalResultSetExtractor;

public class ColumnTypeExtractor implements DalResultSetExtractor<Object> {
	@Override
	public Object extract(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		
		for(int i = 1; i <= rsmd.getColumnCount(); i++) {
			System.out.println(rsmd.getColumnName(i));
			System.out.println(String.format("Sql Type: %d; DB type: %s; Java Type: %s", rsmd.getColumnType(i), rsmd.getColumnTypeName(i), rsmd.getColumnClassName(i)));
		}
		return null;
	}
}