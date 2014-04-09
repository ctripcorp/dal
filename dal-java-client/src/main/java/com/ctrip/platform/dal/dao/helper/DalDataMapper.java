package com.ctrip.platform.dal.dao.helper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DalDataMapper {
	
	public static Integer getInteger(ResultSet rs, String colName) throws SQLException {
		Object objVal = rs.getObject(colName);
		if(objVal == null || objVal instanceof Integer)
			return (Integer)objVal;
		
		return Integer.valueOf(((Number)objVal).intValue());
	}

}
